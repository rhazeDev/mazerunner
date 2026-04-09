package mazerunner;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import mazerunner.MazeRunner.Difficulty;

import Database.Database;
import Dialogz.GameResult;

public class GamePanel extends JPanel implements ActionListener, KeyListener {
    private static final long serialVersionUID = 1L;

    private static final int CELL_SIZE = 32;
    private static final int GAME_SPEED = 60;
    private static final double TIMER_MAX = 120.0;
    private static final int EFFECT_DURATION = 15;

    private static final Color FLOOR_COLOR = new Color(250, 245, 235);
    private static final Color BACKGROUND_COLOR = new Color(70, 130, 180);

    private static final Color NAV_BG = new Color(30, 41, 59);
    private static final Color NAV_BORDER_BOTTOM = new Color(15, 23, 42);
    private static final Color NAV_ACCENT = new Color(59, 130, 246);

    private Player player;
    private MazeGenerator mazeGenerator;
    private MazeRunner gameFrame;
    private Timer gameTimer;
    private Timer countdownTimer;
    private List<Monster> monsters;
    private List<GameElement> gameElements;
    private List<Bullet> movingBullets;
    private List<Effect> activeEffects;

    private DecimalFormat timeFormat = new DecimalFormat("0.00");
    private double timeLeft = TIMER_MAX;
    private int[][] maze;
    private boolean gameOver;
    private boolean gamePaused;
    private boolean gameWon;
    private Difficulty currentDifficulty;
    private boolean gameEndingInProgress = false;

    private JButton restartButton;
    private JPanel statusPanel;
    private JLabel timeLabel;
    private JLabel bulletLabel;
    private JLabel difficultyLabel;

    /** Non-opaque; only {@link #pauseMenuPanel} draws on top of the maze during pause. */
    private final JLayeredPane gameOverlayLayer = new JLayeredPane();
    private final JPanel pauseMenuPanel;

    private ImageIcon explosionImage;
    private ImageIcon killImage;
    private ImageIcon wallImage;

    private SoundManager soundManager;

    public GamePanel(MazeRunner gameFrame) {
        super();

        this.gameFrame = gameFrame;
        this.soundManager = SoundManager.getInstance();
        
        setLayout(new BorderLayout());
        setFocusable(true);
        addKeyListener(this);
        setBackground(BACKGROUND_COLOR);
        explosionImage = MazeRunner.ImageAssets.loadIcon(GamePanel.class, "/images/explode.png");
        killImage = MazeRunner.ImageAssets.loadIcon(GamePanel.class, "/images/kill.png");
        wallImage = MazeRunner.ImageAssets.loadIcon(GamePanel.class, "/images/wall.png");

        statusPanel = new JPanel(new BorderLayout());
        statusPanel.setOpaque(true);
        statusPanel.setBackground(NAV_BG);
        statusPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 3, 0, NAV_BORDER_BOTTOM),
                new EmptyBorder(8, 20, 8, 20)));

        timeLabel = new JLabel();
        ImageIcon timerIcon = MazeRunner.ImageAssets.loadIcon(GamePanel.class, "/images/time.png");
        if (timerIcon.getIconWidth() > 0) {
            timeLabel.setIcon(timerIcon);
        }
        timeLabel.setText(timeFormat.format(TIMER_MAX));
        timeLabel.setFont(new Font("Arial", Font.BOLD, 20));
        timeLabel.setForeground(Color.WHITE);
        timeLabel.setIconTextGap(8);

        bulletLabel = new JLabel();
        ImageIcon bulletIcon = MazeRunner.ImageAssets.loadIcon(GamePanel.class, "/images/bullet_pickup.png");
        if (bulletIcon.getIconWidth() <= 0) {
            bulletIcon = MazeRunner.ImageAssets.loadIcon(GamePanel.class, "/images/bullet.png");
        }
        if (bulletIcon.getIconWidth() > 0) {
            bulletLabel.setIcon(bulletIcon);
        }
        bulletLabel.setText("3");
        bulletLabel.setFont(new Font("Arial", Font.BOLD, 20));
        bulletLabel.setForeground(Color.WHITE);
        bulletLabel.setIconTextGap(8);

        difficultyLabel = new JLabel(MazeRunner.gameDifficulty + " MODE");
        difficultyLabel.setFont(new Font("Arial", Font.BOLD, 18));
        difficultyLabel.setHorizontalAlignment(SwingConstants.CENTER);
        difficultyLabel.setForeground(Color.WHITE);
        difficultyLabel.setOpaque(true);
        difficultyLabel.setBackground(new Color(51, 65, 85));
        difficultyLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(71, 85, 105), 1),
                new EmptyBorder(6, 20, 6, 20)));

        restartButton = new JButton("Restart");
        ImageIcon restartIcon = MazeRunner.ImageAssets.loadIcon(GamePanel.class, "/images/restart.png");
        if (restartIcon.getIconWidth() > 0) {
            restartButton.setIcon(restartIcon);
        }
        restartButton.setFont(new Font("Arial", Font.BOLD, 16));
        restartButton.setForeground(Color.WHITE);
        restartButton.setBackground(NAV_ACCENT);
        restartButton.setFocusPainted(false);
        restartButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(37, 99, 235), 1),
                new EmptyBorder(8, 18, 8, 18)));
        restartButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        restartButton.setPreferredSize(new Dimension(160, 44));
        restartButton.setContentAreaFilled(true);
        restartButton.setOpaque(true);

        restartButton.addActionListener(e -> {
            initGame(currentDifficulty != null ? currentDifficulty : Difficulty.EASY);
        });

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        leftPanel.setOpaque(false);
        leftPanel.add(timeLabel);
        leftPanel.add(bulletLabel);

        JPanel centerWrap = new JPanel(new GridBagLayout());
        centerWrap.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1.0;
        c.anchor = GridBagConstraints.CENTER;
        centerWrap.add(difficultyLabel, c);

        statusPanel.add(leftPanel, BorderLayout.WEST);
        statusPanel.add(centerWrap, BorderLayout.CENTER);
        statusPanel.add(restartButton, BorderLayout.EAST);
        add(statusPanel, BorderLayout.NORTH);

        gameOverlayLayer.setOpaque(false);
        pauseMenuPanel = buildPauseMenuPanel();
        pauseMenuPanel.setVisible(false);
        gameOverlayLayer.add(pauseMenuPanel, JLayeredPane.MODAL_LAYER);
        gameOverlayLayer.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                syncPauseMenuBounds();
            }
        });
        add(gameOverlayLayer, BorderLayout.CENTER);

        installPauseEscapeShortcut();

        gameTimer = new Timer(GAME_SPEED, this);

        monsters = new ArrayList<>();
        gameElements = new ArrayList<>();
        movingBullets = new ArrayList<>();
        activeEffects = new ArrayList<>();
    }

    public void updateGameInfo() {
        if (timeLabel != null) {
            timeLabel.setText(timeFormat.format(timeLeft));
        }

        if (bulletLabel != null && player != null) {
            bulletLabel.setText(String.valueOf(player.getBullets()));
        }
    }

    public void initGame(Difficulty difficulty) {
        currentDifficulty = difficulty;

        gameOver = false;
        gamePaused = false;
        gameWon = false;
        gameEndingInProgress = false;
        timeLeft = TIMER_MAX;

        int mazeSize;
        int monsterCount;

        switch (difficulty) {
            case EASY:
                mazeSize = 23;
                monsterCount = 4;
                break;
            case MEDIUM:
                mazeSize = 25;
                monsterCount = 6;
                break;
            case HARD:
                mazeSize = 27;
                monsterCount = 10;
                break;
            default:
                mazeSize = 22;
                monsterCount = 4;
                break;
        }

        mazeGenerator = new MazeGenerator(mazeSize, mazeSize);
        maze = mazeGenerator.generateMaze();

        monsters.clear();
        gameElements.clear();
        movingBullets.clear();
        activeEffects.clear();

        player = new Player(1, 1, this);

        for (int i = 0; i < monsterCount; i++) {
            Point position = mazeGenerator.getRandomEmptyCell();

            while (position.distance(1, 1) < 8 || isCornerPosition(position.x, position.y)) {
                position = mazeGenerator.getRandomEmptyCell();
            }

            monsters.add(new Monster(position.x, position.y, this));
        }

        createGameElements();
        startGameTimer();
        updateGameInfo();
        gameTimer.start();
        requestFocusInWindow();

        difficultyLabel.setText(MazeRunner.gameDifficulty + " MODE");
        pauseMenuPanel.setVisible(false);
    }

    private void syncPauseMenuBounds() {
        int w = gameOverlayLayer.getWidth();
        int h = gameOverlayLayer.getHeight();
        if (w > 0 && h > 0) {
            pauseMenuPanel.setBounds(0, 0, w, h);
        }
    }

    private void installPauseEscapeShortcut() {
        KeyStroke esc = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        JRootPane root = gameFrame.getRootPane();
        root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(esc, "mazeRunnerTogglePause");
        root.getActionMap().put("mazeRunnerTogglePause", new AbstractAction() {
            private static final long serialVersionUID = 1L;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (gameFrame.getContentPane() != GamePanel.this || gameOver || player == null) {
                    return;
                }
                if (gamePaused) {
                    hidePauseMenu();
                } else {
                    showPauseMenu();
                }
            }
        });
    }

    private void showPauseMenu() {
        if (gameOver || player == null) {
            return;
        }
        gamePaused = true;
        syncPauseMenuBounds();
        pauseMenuPanel.setVisible(true);
        pauseMenuPanel.requestFocusInWindow();
        repaint();
    }

    private void hidePauseMenu() {
        gamePaused = false;
        pauseMenuPanel.setVisible(false);
        requestFocusInWindow();
        repaint();
    }

    private JPanel buildPauseMenuPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(true);
        panel.setBackground(new Color(30, 41, 59));
        panel.setFocusable(true);
        panel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    hidePauseMenu();
                }
            }
        });

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.insets = new Insets(6, 12, 6, 12);

        JLabel title = new JLabel("GAME PAUSED", SwingConstants.CENTER);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Tahoma", Font.BOLD, 32));
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(title, gbc);

        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonRow.setOpaque(false);

        JButton resumeBtn = new JButton("RESUME");
        resumeBtn.setFont(new Font("Tahoma", Font.BOLD, 14));
        resumeBtn.setForeground(Color.WHITE);
        resumeBtn.setBackground(new Color(46, 125, 50));
        resumeBtn.setFocusPainted(false);
        resumeBtn.setPreferredSize(new Dimension(110, 40));
        resumeBtn.addActionListener(e -> hidePauseMenu());

        JButton restartBtn = new JButton("RESTART");
        restartBtn.setFont(new Font("Tahoma", Font.BOLD, 14));
        restartBtn.setForeground(Color.WHITE);
        restartBtn.setBackground(new Color(255, 152, 0));
        restartBtn.setFocusPainted(false);
        restartBtn.setPreferredSize(new Dimension(110, 40));
        restartBtn.addActionListener(e -> {
            hidePauseMenu();
            initGame(currentDifficulty != null ? currentDifficulty : Difficulty.EASY);
        });

        JButton menuBtn = new JButton("MENU");
        menuBtn.setFont(new Font("Tahoma", Font.BOLD, 14));
        menuBtn.setForeground(Color.WHITE);
        menuBtn.setBackground(new Color(21, 101, 192));
        menuBtn.setFocusPainted(false);
        menuBtn.setPreferredSize(new Dimension(110, 40));
        menuBtn.addActionListener(e -> {
            hidePauseMenu();
            gameFrame.showStartScreen();
        });

        buttonRow.add(resumeBtn);
        buttonRow.add(restartBtn);
        buttonRow.add(menuBtn);
        gbc.gridy = 1;
        gbc.insets = new Insets(28, 12, 12, 12);
        panel.add(buttonRow, gbc);

        return panel;
    }

    private void createGameElements() {
        int bombCount = currentDifficulty == Difficulty.EASY ? 5 : (currentDifficulty == Difficulty.MEDIUM ? 8 : 12);

        for (int i = 0; i < bombCount; i++) {
            Point position = mazeGenerator.getRandomEmptyCell();
            while (isCornerPosition(position.x, position.y)) {
                position = mazeGenerator.getRandomEmptyCell();
            }

            gameElements.add(new Bomb(position.x, position.y));
        }

        int bulletCount = 10;
        for (int i = 0; i < bulletCount; i++) {
            Point position = mazeGenerator.getRandomEmptyCell();
            gameElements.add(new BulletPickup(position.x, position.y));
        }

        Point keyPosition = mazeGenerator.getRandomEmptyCell();
        gameElements.add(new Key(keyPosition.x, keyPosition.y));

        Point doorPosition = mazeGenerator.getRandomEmptyCell();
        while (doorPosition.distance(1, 1) < mazeGenerator.getWidth() / 2) {
            doorPosition = mazeGenerator.getRandomEmptyCell();
        }
        gameElements.add(new Door(doorPosition.x, doorPosition.y));

        Point portal1Pos = mazeGenerator.getRandomEmptyCell();
        Point portal2Pos = mazeGenerator.getRandomEmptyCell();
        while (portal1Pos.distance(portal2Pos) < 10) {
            portal2Pos = mazeGenerator.getRandomEmptyCell();
        }
        Portal portal1 = new Portal(portal1Pos.x, portal1Pos.y, portal2Pos);
        Portal portal2 = new Portal(portal2Pos.x, portal2Pos.y, portal1Pos);
        gameElements.add(portal1);
        gameElements.add(portal2);
    }

    private boolean isCornerPosition(int x, int y) {
        if (x <= 0 || y <= 0 || x >= mazeGenerator.getWidth() - 1 || y >= mazeGenerator.getHeight() - 1) {
            return true;
        }

        int wallCount = 0;
        if (maze[y - 1][x] == MazeGenerator.WALL)
            wallCount++;
        if (maze[y][x + 1] == MazeGenerator.WALL)
            wallCount++;
        if (maze[y + 1][x] == MazeGenerator.WALL)
            wallCount++;
        if (maze[y][x - 1] == MazeGenerator.WALL)
            wallCount++;

        return wallCount >= 2;
    }

    private void startGameTimer() {
        if (countdownTimer != null && countdownTimer.isRunning()) {
            countdownTimer.stop();
        }

        countdownTimer = new Timer(100, e -> {
            if (!gamePaused && !gameOver) {
                timeLeft -= 0.1;
                timeLeft = Math.round(timeLeft * 1000) / 1000.0;
                updateGameInfo();

                if (timeLeft <= 0) {
                    endGame(false);
                }
            }
        });
        countdownTimer.start();
    }

    public void endGame(boolean won) {
        if (gameEndingInProgress)
            return;

        gameEndingInProgress = true;
        gameOver = true;
        gameWon = won;

        if (won) {
            soundManager.playSound(SoundManager.GAME_WIN);
        } else {
            soundManager.playSound(SoundManager.GAME_OVER);
            if (player != null) {
                soundManager.playSound(SoundManager.PLAYER_CRASH);
                addEffect(player.getX(), player.getY(), EffectType.EXPLOSION);
            }
        }

        Timer delayTimer = new Timer(500, e -> {
            if (gameTimer != null)
                gameTimer.stop();
            if (countdownTimer != null)
                countdownTimer.stop();

            Timer dialogTimer = new Timer(500, event -> {
                double gameTime = TIMER_MAX - timeLeft;
                boolean isNewBestTime = false;
                double bestTime = Double.MAX_VALUE;
                int rank = 0;

                bestTime = Database.getBestTime(MazeRunner.currentUser, MazeRunner.gameDifficulty);

                if (won) {
                    Database.saveResult(MazeRunner.currentUser, gameTime, MazeRunner.gameDifficulty);
                    if (bestTime == Double.MAX_VALUE) {
                        bestTime = gameTime;
                        isNewBestTime = true;
                    } else {
                        isNewBestTime = gameTime < bestTime;
                    }

                    rank = Database.getUserRank(MazeRunner.currentUser, MazeRunner.gameDifficulty);
                } else {
                    if (bestTime < Double.MAX_VALUE) {
                        rank = Database.getUserRank(MazeRunner.currentUser, MazeRunner.gameDifficulty);
                    }
                }

                if (gameTime < bestTime) {
                    bestTime = gameTime;
                }

                GameResult resultDialog = new GameResult();
                resultDialog.setTitle(won ? "Game Finished" : "Game Over");

                String gameTimeStr = won ? timeFormat.format(gameTime) : "--";
                String bestTimeStr;

                if (bestTime == Double.MAX_VALUE) {
                    bestTimeStr = won ? gameTimeStr : "--";
                } else {
                    bestTimeStr = timeFormat.format(bestTime);
                }

                resultDialog.setTimes(gameTimeStr, bestTimeStr, isNewBestTime, won);
                resultDialog.setDifficulty(MazeRunner.gameDifficulty);
                resultDialog.setRank(rank, isNewBestTime && won);
                resultDialog.setButtonActions(
                        actionEvent -> {
                            resultDialog.dispose();
                            initGame(currentDifficulty);
                            SwingUtilities.invokeLater(() -> {
                                gameFrame.setVisible(true);
                                gameFrame.setExtendedState(JFrame.NORMAL);
                                gameFrame.toFront();
                                gameFrame.requestFocus();
                                gameFrame.requestFocusInWindow();
                                requestFocusInWindow();
                            });
                        },
                        actionEvent -> {
                            resultDialog.dispose();
                            gameFrame.showStartScreen();
                        });
                resultDialog.setVisible(true);
            });
            dialogTimer.setRepeats(false);
            dialogTimer.start();
        });
        delayTimer.setRepeats(false);
        delayTimer.start();
    }

    public boolean canMoveTo(int x, int y) {
        if (x < 0 || y < 0 || x >= mazeGenerator.getWidth() || y >= mazeGenerator.getHeight()) {
            return false;
        }

        return maze[y][x] != MazeGenerator.WALL;
    }

    public void addEffect(int x, int y, EffectType type) {
        ImageIcon effectImage = type == EffectType.EXPLOSION ? explosionImage : killImage;
        if (effectImage != null && effectImage.getIconWidth() > 0) {
            activeEffects.add(new Effect(x, y, EFFECT_DURATION, effectImage));
        }
    }

    public void checkCollisions() {
        if (player == null || gameOver)
            return;

        for (int i = gameElements.size() - 1; i >= 0; i--) {
            GameElement element = gameElements.get(i);
            if (player.getX() == element.getX() && player.getY() == element.getY()) {
                element.onCollision(player);
            }
        }

        for (Monster monster : monsters) {
            if (player.getX() == monster.getX() && player.getY() == monster.getY() && !monster.isDead()) {
                soundManager.playSound(SoundManager.PLAYER_CRASH);
                endGame(false);
                return;
            }
        }

        for (int i = movingBullets.size() - 1; i >= 0; i--) {
            if (i >= movingBullets.size())
                continue;

            Bullet bullet = movingBullets.get(i);

            if (!canMoveTo(bullet.getX(), bullet.getY())) {
                if (i < movingBullets.size()) {
                    movingBullets.remove(i);
                }
                continue;
            }

            boolean bulletHit = false;
            for (Monster monster : monsters) {
                if (bullet.getX() == monster.getX() && bullet.getY() == monster.getY() && !monster.isDead()) {
                    monster.setDead(true);
                    
                    soundManager.playSound(SoundManager.MONSTER_KILL);
                    addEffect(monster.getX(), monster.getY(), EffectType.KILL);

                    if (i < movingBullets.size()) {
                        movingBullets.remove(i);
                        bulletHit = true;
                    }
                    break;
                }
            }

            if (bulletHit)
                continue;

            for (int j = gameElements.size() - 1; j >= 0; j--) {
                GameElement element = gameElements.get(j);
                if (element instanceof Bomb &&
                        bullet.getX() == element.getX() &&
                        bullet.getY() == element.getY()) {
                    
                    soundManager.playSound(SoundManager.BOMB_EXPLODE);
                    addEffect(element.getX(), element.getY(), EffectType.EXPLOSION);
                    gameElements.remove(j);

                    if (i < movingBullets.size()) {
                        movingBullets.remove(i);
                    }

                    bulletHit = true;
                    break;
                }
            }
        }

        if (player.hasKey()) {
            for (GameElement element : gameElements) {
                if (element instanceof Door) {
                    ((Door) element).setOpen(true);
                    break;
                }
            }
        }
    }

    public void fireBullet(int direction) {
        if (player != null && player.getBullets() > 0) {
            player.useBullet();
            updateGameInfo();

            soundManager.playSound(SoundManager.BULLET_SHOOT);

            Bullet bullet = new Bullet(player.getX(), player.getY(), player.getDirection());
            movingBullets.add(bullet);
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (maze == null) {
            return;
        }

        int panelWidth = getWidth();
        int panelHeight = getHeight();

        if (gamePaused) {
            g.setColor(BACKGROUND_COLOR);
            g.fillRect(0, 0, panelWidth, panelHeight);
            return;
        }

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        g.setColor(BACKGROUND_COLOR);
        g.fillRect(0, 0, panelWidth, panelHeight);

        int navH = statusPanel.getHeight();
        if (navH <= 0) {
            navH = Math.max(statusPanel.getPreferredSize().height, 56);
        }

        int availW = Math.max(1, panelWidth);
        int availH = Math.max(1, panelHeight - navH);

        double mazePixelW = mazeGenerator.getWidth() * (double) CELL_SIZE;
        double mazePixelH = mazeGenerator.getHeight() * (double) CELL_SIZE;
        double scale = Math.min(availW / mazePixelW, availH / mazePixelH);

        double scaledW = mazePixelW * scale;
        double scaledH = mazePixelH * scale;
        int offsetX = (int) Math.round((availW - scaledW) / 2.0);
        int offsetY = navH + (int) Math.round((availH - scaledH) / 2.0);

        AffineTransform savedTransform = g2d.getTransform();
        g2d.translate(offsetX, offsetY);
        g2d.scale(scale, scale);

        for (int y = 0; y < mazeGenerator.getHeight(); y++) {
            for (int x = 0; x < mazeGenerator.getWidth(); x++) {
                int screenX = x * CELL_SIZE;
                int screenY = y * CELL_SIZE;

                if (maze[y][x] == MazeGenerator.WALL) {
                    if (wallImage != null && wallImage.getIconWidth() > 0) {
                        g2d.drawImage(wallImage.getImage(), screenX, screenY, CELL_SIZE, CELL_SIZE, null);
                    } else {
                        g2d.setColor(Color.GRAY);
                        g2d.fillRect(screenX, screenY, CELL_SIZE, CELL_SIZE);
                    }
                } else {
                    g2d.setColor(FLOOR_COLOR);
                    g2d.fillRect(screenX, screenY, CELL_SIZE, CELL_SIZE);
                }
            }
        }

        int logicOffsetX = 0;
        int logicOffsetY = 0;

        for (GameElement element : gameElements) {
            element.draw(g2d, logicOffsetX, logicOffsetY, CELL_SIZE);
        }

        for (Monster monster : monsters) {
            if (!monster.isDead()) {
                monster.draw(g2d, logicOffsetX, logicOffsetY, CELL_SIZE);
            }
        }

        for (Bullet bullet : movingBullets) {
            bullet.draw(g2d, logicOffsetX, logicOffsetY, CELL_SIZE);
        }

        for (Effect effect : activeEffects) {
            effect.draw(g2d, logicOffsetX, logicOffsetY, CELL_SIZE);
        }

        if (player != null && (!gameOver || gameWon)) {
            player.draw(g2d, logicOffsetX, logicOffsetY, CELL_SIZE);
        }

        g2d.setTransform(savedTransform);

        if (gameOver) {
            g.setColor(new Color(0, 0, 0, 150));
            g.fillRect(0, 0, panelWidth, panelHeight);

            g.setFont(new Font("Arial", Font.BOLD, 48));
            g.setColor(Color.WHITE);
            String message = gameWon ? "YOU WON!" : "GAME OVER";

            FontMetrics fm = g.getFontMetrics();
            int messageWidth = fm.stringWidth(message);
            int messageHeight = fm.getHeight();

            g.drawString(message, (panelWidth - messageWidth) / 2,
                    (panelHeight - messageHeight) / 2 + fm.getAscent());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameOver && !gamePaused && player != null) {
            for (Monster monster : monsters) {
                if (!monster.isDead()) {
                    monster.update();
                }
            }

            for (int i = movingBullets.size() - 1; i >= 0; i--) {
                if (i >= movingBullets.size())
                    continue;

                Bullet bullet = movingBullets.get(i);
                bullet.update();

                if (bullet.getX() < 0 || bullet.getY() < 0 ||
                        bullet.getX() >= mazeGenerator.getWidth() ||
                        bullet.getY() >= mazeGenerator.getHeight()) {
                    movingBullets.remove(i);
                }
            }

            for (int i = activeEffects.size() - 1; i >= 0; i--) {
                Effect effect = activeEffects.get(i);
                effect.update();
                if (effect.isDone()) {
                    activeEffects.remove(i);
                }
            }

            checkCollisions();
        }

        repaint();
    }

    public void keyPressed(KeyEvent e) {
        if (gameOver || player == null) {
            return;
        }
        if (gamePaused) {
            return;
        }

        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
            case KeyEvent.VK_W:
                player.setDirection(0);
                player.move(0, -1);
                break;

            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_S:
                player.setDirection(2);
                player.move(0, 1);
                break;

            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_A:
                player.setDirection(3);
                player.move(-1, 0);
                break;

            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_D:
                player.setDirection(1);
                player.move(1, 0);
                break;

            case KeyEvent.VK_SPACE:
                fireBullet(player.getDirection());
                break;
        }

        checkCollisions();
        updateGameInfo();
    }

    public void keyReleased(KeyEvent e) {
    }

    public void keyTyped(KeyEvent e) {
    }

    public Player getPlayer() {
        return player;
    }

    public int[][] getMaze() {
        return maze;
    }

    public MazeGenerator getMazeGenerator() {
        return mazeGenerator;
    }

    public List<GameElement> getGameElements() {
        return gameElements;
    }

    public enum EffectType {
        EXPLOSION, KILL
    }

    private class Effect {
        private int x, y;
        private int duration;
        private int currentFrame;
        private ImageIcon image;

        public Effect(int x, int y, int duration, ImageIcon image) {
            this.x = x;
            this.y = y;
            this.duration = duration;
            this.currentFrame = 0;
            this.image = image;
        }

        public void update() {
            currentFrame++;
        }

        public boolean isDone() {
            return currentFrame >= duration;
        }

        public void draw(Graphics g, int offsetX, int offsetY, int cellSize) {
            int screenX = x * cellSize + offsetX;
            int screenY = y * cellSize + offsetY;

            if (image != null) {
                float scale = 1.0f + 0.2f * (float) Math.sin(currentFrame * Math.PI / 5);
                int adjustedSize = (int) (cellSize * scale);
                int adjustX = (cellSize - adjustedSize) / 2;
                int adjustY = (cellSize - adjustedSize) / 2;

                g.drawImage(image.getImage(), screenX + adjustX, screenY + adjustY,
                        adjustedSize, adjustedSize, null);
            }
        }
    }
}