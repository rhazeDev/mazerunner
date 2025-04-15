package mazerunner;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MazeRunner extends JFrame {
    private static final long serialVersionUID = 1L;
    private static final int FRAME_WIDTH = 1200;
    private static final int FRAME_HEIGHT = 900;
    
    private GamePanel gamePanel;
    private JPanel startScreen;
    
    public enum Difficulty {
        EASY, MEDIUM, HARD
    }
    
    public MazeRunner() {
        setTitle("Maze Runner");
        setSize(FRAME_WIDTH, FRAME_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        createStartScreen();
        setContentPane(startScreen);
        setVisible(true);
    }
    
    private void createStartScreen() {
        startScreen = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(0, 0, new Color(25, 25, 112), 
                                                  getWidth(), getHeight(), new Color(70, 130, 180));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        
        startScreen.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        
        JButton instructionButton = new JButton();
        instructionButton.setPreferredSize(new Dimension(40, 40));
        instructionButton.setBorderPainted(false);
        instructionButton.setContentAreaFilled(false);
        instructionButton.setFocusPainted(false);
        
        try {
            ImageIcon instructionIcon = new ImageIcon(getClass().getResource("/images/instruction.png"));
            if (instructionIcon.getIconWidth() <= 0) {
                instructionIcon = new ImageIcon("images/instruction.png");
            }
            instructionButton.setIcon(instructionIcon);
        } catch (Exception e) {
            instructionButton.setText("?");
            instructionButton.setFont(new Font("Arial", Font.BOLD, 20));
            instructionButton.setForeground(Color.WHITE);
        }
        
        instructionButton.addActionListener(e -> showInstructions());
        
        JPanel topRightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topRightPanel.setOpaque(false);
        topRightPanel.add(instructionButton);
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 0.05;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        startScreen.add(topRightPanel, gbc);
        
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        
        JLabel titleLabel = new JLabel("MAZE RUNNER");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 72));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JLabel subtitleLabel = new JLabel("Navigate the maze, collect the key, escape through the door!");
        subtitleLabel.setFont(new Font("Arial", Font.ITALIC, 18));
        subtitleLabel.setForeground(Color.LIGHT_GRAY);
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        titlePanel.add(subtitleLabel, BorderLayout.SOUTH);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 0.2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        startScreen.add(titlePanel, gbc);
        
        JPanel buttonsPanel = new JPanel(new GridBagLayout());
        buttonsPanel.setOpaque(false);
        
        JPanel buttonContainer = new JPanel(new GridLayout(4, 1, 0, 20));
        buttonContainer.setOpaque(false);
        
        JButton easyButton = createStyledButton("Easy Mode", Difficulty.EASY);
        JButton mediumButton = createStyledButton("Medium Mode", Difficulty.MEDIUM);
        JButton hardButton = createStyledButton("Hard Mode", Difficulty.HARD);
        JButton exitButton = createStyledButton("Exit Game", null);
        exitButton.addActionListener(e -> System.exit(0));
        
        buttonContainer.add(easyButton);
        buttonContainer.add(mediumButton);
        buttonContainer.add(hardButton);
        buttonContainer.add(exitButton);
        
        buttonsPanel.add(buttonContainer);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 0.6;
        gbc.fill = GridBagConstraints.BOTH;
        startScreen.add(buttonsPanel, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        gbc.weighty = 0.15;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.SOUTH;
    }
    
    private void showInstructions() {
    	Instruction instructionFrame = new Instruction();
    	instructionFrame.setVisible(true);
    }
    
    private JButton createStyledButton(String text, Difficulty difficulty) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 24));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(70, 130, 180));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(300, 60));
        
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(100, 149, 237));
            }
            
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(70, 130, 180));
            }
        });
        
        if (difficulty != null) {
            button.addActionListener(e -> startGame(difficulty));
        }
        
        return button;
    }
    
    private void startGame(Difficulty difficulty) {
        if (gamePanel == null) {
            gamePanel = new GamePanel(this);
        }
        
        setContentPane(gamePanel);
        revalidate();
        repaint();
        
        gamePanel.initGame(difficulty);
        gamePanel.requestFocusInWindow();
    }
    
    public void showStartScreen() {
        setContentPane(startScreen);
        revalidate();
        repaint();
    }
    
    public static void main(String[] args) {
        try {
        	new Database.conn();
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> new MazeRunner());
    }
}