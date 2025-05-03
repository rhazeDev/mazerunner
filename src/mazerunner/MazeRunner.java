package mazerunner;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.*;
import java.awt.FlowLayout;

import javax.swing.*;

import java.util.List;
import java.util.ArrayList;

import Database.Database;
import Database.UserData;
import Dialogz.Instruction;
import Dialogz.Leaderboard;
import Dialogz.Login;

public class MazeRunner extends JFrame {
    private static final long serialVersionUID = 1L;
    private static final int FRAME_WIDTH = 1200;
    private static final int FRAME_HEIGHT = 900;
    
    private GamePanel gamePanel;
    private JPanel startScreen;
    private JPanel difficultyPanel;
    private JButton startGameButton;
    private JPanel initialButtonPanel;
    private static JLabel welcomeLabel;
    
    Database database = new Database();
    UserData UserData;
    public static String gameDifficulty = "EASY";
    public static String currentUser = "Guest";

    public static void setCurrentUser(String username) {
    	currentUser = username;
    }

    public static String setDifficulty(String difficulty) {
        gameDifficulty = difficulty;
        return gameDifficulty;
    }
    
    public static void refreshUser() {
    	welcomeLabel.setText("Hello " + (currentUser != null ? currentUser : "Guest") + "!");
    }
    
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
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        
        welcomeLabel = new JLabel("Hello " + (currentUser != null ? currentUser : "Guest") + "!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 36));
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 0));
        
        JPanel topLeftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topLeftPanel.setOpaque(false);
        topLeftPanel.add(welcomeLabel);
        topPanel.add(topLeftPanel, BorderLayout.WEST);
        
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
        topPanel.add(topRightPanel, BorderLayout.EAST);
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 0.05;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        startScreen.add(topPanel, gbc);
        
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        
        JLabel titleLabel = new JLabel("MAZE RUNNER");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 92));
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
        
        initialButtonPanel = new JPanel(new GridLayout(3, 1, 0, 20));
        initialButtonPanel.setOpaque(false);
        
        startGameButton = createStyledButton("Start Game", null);
        startGameButton.addActionListener(e -> showDifficultyOptions());
        
        JButton leaderboardButton = new JButton("Leaderboard");
        leaderboardButton.setFont(new Font("Arial", Font.BOLD, 24));
        leaderboardButton.setForeground(Color.WHITE);
        leaderboardButton.setBackground(new Color(128, 0, 128));
        leaderboardButton.setFocusPainted(false);
        leaderboardButton.setBorderPainted(false);
        leaderboardButton.setPreferredSize(new Dimension(300, 60));
        
        leaderboardButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                leaderboardButton.setBackground(new Color(147, 112, 219));
            }
            
            public void mouseExited(MouseEvent e) {
                leaderboardButton.setBackground(new Color(128, 0, 128));
            }
        });
        
        leaderboardButton.addActionListener(e -> showLeaderboard());
        
        // Create logout button with different color
        JButton logoutButton = new JButton("Switch User");
        logoutButton.setFont(new Font("Arial", Font.BOLD, 24));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setBackground(new Color(178, 34, 34));
        logoutButton.setFocusPainted(false);
        logoutButton.setBorderPainted(false);
        logoutButton.setPreferredSize(new Dimension(300, 60));
        
        logoutButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                logoutButton.setBackground(new Color(220, 20, 60));
            }
            
            public void mouseExited(MouseEvent e) {
                logoutButton.setBackground(new Color(178, 34, 34));
            }
        });
        
        logoutButton.addActionListener(e -> logout());
        
        initialButtonPanel.add(startGameButton);
        initialButtonPanel.add(leaderboardButton);
        initialButtonPanel.add(logoutButton);
        
        // Difficulty panel with 4 buttons (3 difficulties + back button)
        difficultyPanel = new JPanel(new GridLayout(4, 1, 0, 20));
        difficultyPanel.setOpaque(false);
        difficultyPanel.setVisible(false);
        
        JButton easyButton = createStyledButton("Easy Mode", Difficulty.EASY);
        JButton mediumButton = createStyledButton("Medium Mode", Difficulty.MEDIUM);
        JButton hardButton = createStyledButton("Hard Mode", Difficulty.HARD);
        
        // Create back button with different color
        JButton backButton = new JButton("Back");
        backButton.setFont(new Font("Arial", Font.BOLD, 24));
        backButton.setForeground(Color.WHITE);
        backButton.setBackground(new Color(50, 50, 150)); // Dark blue color
        backButton.setFocusPainted(false);
        backButton.setBorderPainted(false);
        backButton.setPreferredSize(new Dimension(300, 60));
        
        backButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                backButton.setBackground(new Color(70, 70, 180)); // Lighter blue on hover
            }
            
            public void mouseExited(MouseEvent e) {
                backButton.setBackground(new Color(50, 50, 150)); // Back to original blue
            }
        });
        
        backButton.addActionListener(e -> returnToMainMenu());
        
        difficultyPanel.add(easyButton);
        difficultyPanel.add(mediumButton);
        difficultyPanel.add(hardButton);
        difficultyPanel.add(backButton);
        
        buttonsPanel.add(initialButtonPanel);
        buttonsPanel.add(difficultyPanel);
        
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
    
    private void showDifficultyOptions() {
        if (!currentUser.equalsIgnoreCase("guest")) {
        	initialButtonPanel.setVisible(false);
            difficultyPanel.setVisible(true);
            
            startScreen.revalidate();
            startScreen.repaint();
        } else {
        	JOptionPane option = new JOptionPane();
        	option.showMessageDialog(option, "Log in first");
        }
    }
    
    private void returnToMainMenu() {
        difficultyPanel.setVisible(false);
        initialButtonPanel.setVisible(true);
        
        startScreen.revalidate();
        startScreen.repaint();
    }
    
    private void logout() {
        SwingUtilities.invokeLater(() -> {
            Login loginFrame = new Login();
            loginFrame.setVisible(true);
        });
    }
    
    private void showInstructions() {
    	Instruction instructionFrame = new Instruction();
    	instructionFrame.setVisible(true);
    }
    
    private void showLeaderboard() {
        Leaderboard leaderboardFrame = new Leaderboard(currentUser);
        leaderboardFrame.setVisible(true);
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
                String[] btnTexts = button.getText().split("\s");
                gameDifficulty = btnTexts[0].toUpperCase();
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
        if (difficultyPanel != null) {
            difficultyPanel.setVisible(false);
        }
        if (initialButtonPanel != null) {
            initialButtonPanel.setVisible(true);
        }
        
        setContentPane(startScreen);
        revalidate();
        repaint();
    }
    
    public static void main(String[] args) {
    	MazeRunner game = new MazeRunner();
    	game.setVisible(true);
        Login frame = new Login();
        frame.setVisible(true);
    }
}