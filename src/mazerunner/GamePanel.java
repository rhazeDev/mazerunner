package mazerunner;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import mazerunner.MazeRunner.Difficulty;

import Dialogz.GameResult;
import Database.Database;

public class GamePanel extends JPanel implements ActionListener, KeyListener {
    private static final long serialVersionUID = 1L;

    private static final int CELL_SIZE = 32;
    private static final int GAME_SPEED = 60;
    private static final double TIMER_MAX = 120.0;
    private static final int EFFECT_DURATION = 15;

    private static final Color FLOOR_COLOR = new Color(250, 245, 235);
    private static final Color BACKGROUND_COLOR = new Color(70, 130, 180);

    private Player player;
    private MazeGenerator mazeGenerator;
    private MazeRunner gameFrame;
    private Timer gameTimer;
    private Timer countdownTimer;
    private List<Monster> monsters;
    private List<GameElement> gameElements;
    private List<Bullet> movingBullets;
    private List<Effect> activeEffects;

    private DecimalFormat timeFormat = new DecimalFormat("0.0");
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

    private ImageIcon explosionImage;
    private ImageIcon killImage;
    
    private SoundManager soundManager;

    public GamePanel(MazeRunner gameFrame) {
        super();

        this.gameFrame = gameFrame;
        this.soundManager = SoundManager.getInstance();
        
        setLayout(new BorderLayout());
        setFocusable(true);
        addKeyListener(this);
        setBackground(BACKGROUND_COLOR);
        try {
            explosionImage = new ImageIcon(getClass().getResource("/images/explode.png"));
            if (explosionImage.getIconWidth() <= 0) {
                explosionImage = new ImageIcon("images/explode.png");
            }

            killImage = new ImageIcon(getClass().getResource("/images/kill.png"));
            if (killImage.getIconWidth() <= 0) {
                killImage = new ImageIcon("images/kill.png");
            }
        } catch (Exception e) {
            System.out.println("Error loading effect images: " + e.getMessage());
            explosionImage = null;
            killImage = null;
        }

        statusPanel = new JPanel();
        statusPanel.setLayout(new BorderLayout());

        timeLabel = new JLabel();
        try {
            ImageIcon timerIcon = new ImageIcon(getClass().getResource("/images/time.png"));
            if (timerIcon.getIconWidth() <= 0) {
                timerIcon = new ImageIcon("images/time.png");
            }
            timeLabel.setIcon(timerIcon);
        } catch (Exception e) {
            System.out.println("Error loading timer icon: " + e.getMessage());
        }
        timeLabel.setText(timeFormat.format(TIMER_MAX));
        timeLabel.setFont(new Font("Arial", Font.BOLD, 20));

        bulletLabel = new JLabel();
        try {
            ImageIcon bulletIcon = new ImageIcon(getClass().getResource("/images/bullet_pickup.png"));
            if (bulletIcon.getIconWidth() <= 0) {
                bulletIcon = new ImageIcon("images/bullet.png");
            }
            bulletLabel.setIcon(bulletIcon);
        } catch (Exception e) {
            System.out.println("Error loading bullet icon: " + e.getMessage());
        }
        bulletLabel.setText("3");
        bulletLabel.setFont(new Font("Arial", Font.BOLD, 20));

        JLabel difficultyLabel = new JLabel(MazeRunner.gameDifficulty + " MODE");
        difficultyLabel.setFont(new Font("Arial", Font.BOLD, 20));
        difficultyLabel.setHorizontalAlignment(SwingConstants.CENTER);

        restartButton = new JButton("Restart");
        try {
            ImageIcon restartIcon = new ImageIcon(getClass().getResource("/images/restart.png"));
            if (restartIcon.getIconWidth() <= 0) {
                restartIcon = new ImageIcon("images/restart.png");
            }

            if (restartIcon.getIconWidth() > 0) {
                restartButton.setIcon(restartIcon);
            }
        } catch (Exception e) {
            System.out.println("Error loading restart icon: " + e.getMessage());
        }

        restartButton.addActionListener(e -> {
            initGame(currentDifficulty != null ? currentDifficulty : Difficulty.EASY);
        });

        JPanel leftPanel = new JPanel();
        leftPanel.add(timeLabel);
        leftPanel.add(new JLabel("   "));
        leftPanel.add(bulletLabel);

        statusPanel.add(leftPanel, BorderLayout.WEST);
        statusPanel.add(difficultyLabel, BorderLayout.CENTER);
        statusPanel.add(restartButton, BorderLayout.EAST);
        add(statusPanel, BorderLayout.NORTH);

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

        Component[] components = statusPanel.getComponents();
        for (Component comp : components) {
            if (comp instanceof JLabel && comp == statusPanel.getComponent(1)) {
                ((JLabel) comp).setText(MazeRunner.gameDifficulty);
                break;
            }
        }
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
        if (effectImage != null) {
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
        int mazeWidth = mazeGenerator.getWidth() * CELL_SIZE;
        int mazeHeight = mazeGenerator.getHeight() * CELL_SIZE;

        int offsetX = Math.max(0, (panelWidth - mazeWidth) / 2);
        int offsetY = Math.max(0, (panelHeight - mazeHeight) / 2);

        for (int y = 0; y < mazeGenerator.getHeight(); y++) {
            for (int x = 0; x < mazeGenerator.getWidth(); x++) {
                int screenX = x * CELL_SIZE + offsetX;
                int screenY = y * CELL_SIZE + offsetY;

                if (maze[y][x] == MazeGenerator.WALL) {
                    try {
                        ImageIcon wallImage = new ImageIcon(getClass().getResource("/images/wall.png"));
                        if (wallImage.getIconWidth() <= 0) {
                            wallImage = new ImageIcon("images/wall.png");
                        }

                        if (wallImage.getIconWidth() > 0) {
                            g.drawImage(wallImage.getImage(), screenX, screenY, CELL_SIZE, CELL_SIZE, null);
                        } else {
                            g.setColor(Color.GRAY);
                            g.fillRect(screenX, screenY, CELL_SIZE, CELL_SIZE);
                        }
                    } catch (Exception e) {
                        g.setColor(Color.GRAY);
                        g.fillRect(screenX, screenY, CELL_SIZE, CELL_SIZE);
                    }
                } else {
                    g.setColor(FLOOR_COLOR);
                    g.fillRect(screenX, screenY, CELL_SIZE, CELL_SIZE);
                }
            }
        }

        for (GameElement element : gameElements) {
            element.draw(g, offsetX, offsetY, CELL_SIZE);
        }

        for (Monster monster : monsters) {
            if (!monster.isDead()) {
                monster.draw(g, offsetX, offsetY, CELL_SIZE);
            }
        }

        for (Bullet bullet : movingBullets) {
            bullet.draw(g, offsetX, offsetY, CELL_SIZE);
        }

        for (Effect effect : activeEffects) {
            effect.draw(g, offsetX, offsetY, CELL_SIZE);
        }

        if (player != null && (!gameOver || gameWon)) {
            player.draw(g, offsetX, offsetY, CELL_SIZE);
        }

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
        if (gameOver || player == null)
            return;

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

            case KeyEvent.VK_ESCAPE:
                gamePaused = true;

                Dialogz.GamePause pauseDialog = new Dialogz.GamePause();
                pauseDialog.setButtonActions(
                        actionEvent -> {
                            gamePaused = false;
                            pauseDialog.dispose();
                            requestFocusInWindow();
                        },
                        actionEvent -> {
                            pauseDialog.dispose();
                            initGame(currentDifficulty);
                        },
                        actionEvent -> {
                            pauseDialog.dispose();
                            gameFrame.showStartScreen();
                        });

                pauseDialog.setVisible(true);
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