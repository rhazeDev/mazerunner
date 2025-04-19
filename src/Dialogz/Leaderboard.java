package Dialogz;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import Database.Database;
import Database.UserData;

public class Leaderboard extends JFrame {
    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JPanel leaderboardEntriesPanel;
    private JPanel difficultyPanel;
    private JScrollPane scrollPane;
    private String currentDifficulty = "EASY";
    private JButton easyBtn, mediumBtn, hardBtn;
    private Color backgroundColor = new Color(65, 105, 255); // SteelBlue
    
    private Database database;
    private String currentUser;
    
    private static final Color[] ROW_COLORS = {
            new Color(255, 215, 0),   // Yellow for 1st place
            new Color(192, 192, 192),   // DodgerBlue for 2nd place
            new Color(205, 127, 50),    // Tomato for 3rd place
            new Color(0, 102, 204),  // LightSteelBlue for others
            new Color(0, 102, 204),
            new Color(0, 102, 204),
            new Color(0, 102, 204),
            new Color(0, 102, 204),
            new Color(0, 102, 204),
            new Color(0, 102, 204)
    };

    /**
     * Create the frame.
     */
    public Leaderboard(String currentUser) {
        this.currentUser = currentUser;
        this.database = new Database();
        
        setTitle("Maze Runner - Leaderboard");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(507, 700); // Fixed width of 500px
        setLocationRelativeTo(null);
        setResizable(false); // Make window non-resizable to enforce the width limit
        
        contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout(0, 0));
        setContentPane(contentPane);
        
        // Main panel with fixed absolute positioning
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(null);  // Use null layout for absolute positioning
        mainPanel.setBackground(new Color(0, 0, 128));
        contentPane.add(mainPanel, BorderLayout.CENTER);
        
        // Title - positioned absolutely
        JLabel titleLabel = new JLabel("Leaderboard", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 40));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(0, 10, 490, 50);  // Fixed position
        mainPanel.add(titleLabel);
        
        // Difficulty buttons panel - positioned absolutely
        difficultyPanel = new JPanel();
        difficultyPanel.setLayout(new GridLayout(1, 3, 10, 0));
        difficultyPanel.setBounds(10, 70, 470, 40);  // Fixed position
        difficultyPanel.setOpaque(false);
        mainPanel.add(difficultyPanel);
        
        // Create difficulty buttons with fixed colors
        easyBtn = createDifficultyButton("Easy", "EASY", new Color(65, 105, 225));  // Blue
        mediumBtn = createDifficultyButton("Medium", "MEDIUM", new Color(33, 37, 41));
        hardBtn = createDifficultyButton("Hard", "HARD", new Color(33, 37, 41));
        
        // Add buttons to panel
        difficultyPanel.add(easyBtn);
        difficultyPanel.add(mediumBtn);
        difficultyPanel.add(hardBtn);
        
        // Set initial selected button
        updateDifficultyButtonsState();
        
        // Create leaderboard entries panel
        leaderboardEntriesPanel = new JPanel();
        leaderboardEntriesPanel.setLayout(new BoxLayout(leaderboardEntriesPanel, BoxLayout.Y_AXIS));
        leaderboardEntriesPanel.setOpaque(false);
        
        // Add to a scroll pane with hidden scrollbars
        scrollPane = new JScrollPane(leaderboardEntriesPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setBounds(10, 130, 470, 480);  // Fixed position
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        
        // Hide the scrollbars
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        
        mainPanel.add(scrollPane);
        
        // Back button at the bottom
        JButton backBtn = new JButton("Back to Menu");
        backBtn.setFont(new Font("Arial", Font.BOLD, 16));
        backBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        contentPane.add(backBtn, BorderLayout.SOUTH);
        
        // Load leaderboard data
        loadLeaderboardData();
    }
    
    private JButton createDifficultyButton(String text, String difficulty, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 16));
        btn.setFocusPainted(false);
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);  // Always white text on buttons
        
        // Make sure the UI manager doesn't override our color settings
        btn.setContentAreaFilled(true);
        btn.setBorderPainted(false);
        
        btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                currentDifficulty = difficulty;
                updateDifficultyButtonsState();
                loadLeaderboardData();
            }
        });
        
        return btn;
    }
    
    private void updateDifficultyButtonsState() {
        // Update button backgrounds based on current selection
        easyBtn.setBackground(currentDifficulty.equals("EASY") ? new Color(65, 105, 225) : new Color(33, 37, 41));
        mediumBtn.setBackground(currentDifficulty.equals("MEDIUM") ? new Color(65, 105, 255) : new Color(33, 37, 41));
        hardBtn.setBackground(currentDifficulty.equals("HARD") ? new Color(65, 105, 225) : new Color(33, 37, 41));
        
        // Ensure the text stays white
        easyBtn.setForeground(Color.WHITE);
        mediumBtn.setForeground(Color.WHITE);
        hardBtn.setForeground(Color.WHITE);
    }
    
    private void loadLeaderboardData() {
        // Clear current entries
        leaderboardEntriesPanel.removeAll();
        
        // Get top 10 scores for current difficulty
        List<UserData> scores = database.leaderboardData(currentDifficulty);
        
        // If no data available, use empty list
        if (scores == null) {
            scores = new ArrayList<>();
        }
        
        // Handle visibility of difficulty buttons based on entry count
        if (scores.size() >= 8) {
            // Don't change visibility, just ignore the hiding logic
            // difficultyPanel.setVisible(false);  <- This line was causing the problem
        } else {
            // Keep it visible
            difficultyPanel.setVisible(true);
        }
        
        // Create entries for top 10 scores
        for (int i = 0; i < Math.min(scores.size(), 10); i++) {
            UserData score = scores.get(i);
            JPanel entryPanel = createLeaderboardEntry(i + 1, score.getUsername(), score.getTime(), 
                                                     ROW_COLORS[i % ROW_COLORS.length], score.getUsername().equals(currentUser));
            leaderboardEntriesPanel.add(entryPanel);
            
            // Add a small gap between entries
            if (i < Math.min(scores.size(), 10) - 1) {
                leaderboardEntriesPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            }
        }
        
        // Add current user if not in top 10
        boolean currentUserInTop10 = false;
        for (int i = 0; i < Math.min(scores.size(), 10); i++) {
            if (scores.get(i).getUsername().equals(currentUser)) {
                currentUserInTop10 = true;
                break;
            }
        }
        
        if (!currentUserInTop10) {
            // Find current user's position and score
            int position = -1;
            double time = 0;
            
            for (int i = 0; i < scores.size(); i++) {
                if (scores.get(i).getUsername().equals(currentUser)) {
                    position = i + 1;
                    time = scores.get(i).getTime();
                    break;
                }
            }
            
            if (position > 0) {
                // Add separator
                JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
                separator.setForeground(Color.WHITE);
                separator.setBackground(Color.BLACK);
                separator.setMaximumSize(new Dimension(470, 2));
                leaderboardEntriesPanel.add(Box.createRigidArea(new Dimension(0, 10)));
                leaderboardEntriesPanel.add(separator);
                leaderboardEntriesPanel.add(Box.createRigidArea(new Dimension(0, 10)));
                
                // Add current user's entry
                JPanel userPanel = createLeaderboardEntry(position, currentUser, time, 
                                                        backgroundColor, true);
                leaderboardEntriesPanel.add(userPanel);
            }
        }
        
        leaderboardEntriesPanel.revalidate();
        leaderboardEntriesPanel.repaint();
    }
    
    private JPanel createLeaderboardEntry(int position, String username, double time, Color bgColor, boolean isCurrentUser) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setPreferredSize(new Dimension(470, 60));
        panel.setMaximumSize(new Dimension(470, 60));
        panel.setMinimumSize(new Dimension(470, 60));
        panel.setBackground(bgColor);
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Rank number
        JLabel rankLbl = new JLabel(String.valueOf(position));
        rankLbl.setFont(new Font("Arial", Font.BOLD, 32));
        rankLbl.setForeground(new Color(33, 37, 41));
        rankLbl.setPreferredSize(new Dimension(60, 60));
        rankLbl.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(rankLbl, BorderLayout.WEST);
        
        // Username
        JLabel userLbl = new JLabel(username);
        userLbl.setFont(new Font("Arial", Font.BOLD, 24));
        userLbl.setForeground(new Color(33, 37, 41));
        userLbl.setBorder(new EmptyBorder(0, 15, 0, 0));
        panel.add(userLbl, BorderLayout.CENTER);
        
        // Score with medal icon
        JPanel scorePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 20));
        scorePanel.setOpaque(false);
        
        ImageIcon icon = new ImageIcon("images/time.png");
        JLabel timeIcon = new JLabel();
        timeIcon.setPreferredSize(new Dimension(24, 24));
        timeIcon.setIcon(icon);
        timeIcon.setOpaque(false);
        
        // Time label - format to remove decimal if it's a whole number
        String timeDisplay = time == Math.floor(time) ? String.format("%.0f", time) : String.valueOf(time);
        JLabel timeLbl = new JLabel(timeDisplay);
        timeLbl.setFont(new Font("Arial", Font.BOLD, 18));
        timeLbl.setForeground(new Color(33, 37, 41));
        
        scorePanel.add(timeLbl);
        scorePanel.add(timeIcon);
        panel.add(scorePanel, BorderLayout.EAST);
        
        return panel;
    }
}