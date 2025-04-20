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
import mazerunner.MazeRunner;

public class Leaderboard extends JFrame {
    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JPanel leaderboardEntriesPanel;
    private JPanel difficultyPanel;
    private JScrollPane scrollPane;
    private String currentDifficulty = "EASY";
    private JButton easyBtn, mediumBtn, hardBtn;
    private Color backgroundColor = new Color(65, 105, 255);
    
    private Database database;
    private String currentUser;
    
    private static final Color[] ROW_COLORS = {
            new Color(255, 215, 0),
            new Color(192, 192, 192),
            new Color(205, 127, 50),
            new Color(0, 102, 204),
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
        setSize(507, 700);
        setLocationRelativeTo(null);
        setResizable(false);
        
        contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout(0, 0));
        setContentPane(contentPane);
        
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(null);
        mainPanel.setBackground(new Color(0, 0, 128));
        contentPane.add(mainPanel, BorderLayout.CENTER);
        
        JLabel titleLabel = new JLabel("Leaderboard", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 40));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(0, 10, 490, 50);
        mainPanel.add(titleLabel);
        
        difficultyPanel = new JPanel();
        difficultyPanel.setLayout(new GridLayout(1, 3, 10, 0));
        difficultyPanel.setBounds(10, 70, 470, 40);
        difficultyPanel.setOpaque(false);
        mainPanel.add(difficultyPanel);
        
        easyBtn = createDifficultyButton("Easy", "EASY", new Color(65, 105, 225));  // Blue
        mediumBtn = createDifficultyButton("Medium", "MEDIUM", new Color(33, 37, 41));
        hardBtn = createDifficultyButton("Hard", "HARD", new Color(33, 37, 41));
        
        difficultyPanel.add(easyBtn);
        difficultyPanel.add(mediumBtn);
        difficultyPanel.add(hardBtn);
        
        updateDifficultyButtonsState();
        
        leaderboardEntriesPanel = new JPanel();
        leaderboardEntriesPanel.setLayout(new BoxLayout(leaderboardEntriesPanel, BoxLayout.Y_AXIS));
        leaderboardEntriesPanel.setOpaque(false);
        
        scrollPane = new JScrollPane(leaderboardEntriesPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setBounds(10, 130, 470, 480);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        
        mainPanel.add(scrollPane);
        
        JButton backBtn = new JButton("Back to Menu");
        backBtn.setFont(new Font("Arial", Font.BOLD, 16));
        backBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        contentPane.add(backBtn, BorderLayout.SOUTH);
        
        loadLeaderboardData();
    }
    
    private JButton createDifficultyButton(String text, String difficulty, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 16));
        btn.setFocusPainted(false);
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        
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
        easyBtn.setBackground(currentDifficulty.equals("EASY") ? new Color(65, 105, 225) : new Color(33, 37, 41));
        mediumBtn.setBackground(currentDifficulty.equals("MEDIUM") ? new Color(65, 105, 255) : new Color(33, 37, 41));
        hardBtn.setBackground(currentDifficulty.equals("HARD") ? new Color(65, 105, 225) : new Color(33, 37, 41));
        
        easyBtn.setForeground(Color.WHITE);
        mediumBtn.setForeground(Color.WHITE);
        hardBtn.setForeground(Color.WHITE);
    }
    
    private void loadLeaderboardData() {
        leaderboardEntriesPanel.removeAll();
        List<UserData> scores = database.leaderboardData(currentDifficulty);
        
        if (scores == null) {
            scores = new ArrayList<>();
        }
        
        if (scores.size() < 7) {
            difficultyPanel.setVisible(true);
        }
        
        for (int i = 0; i < Math.min(scores.size(), 10); i++) {
            UserData score = scores.get(i);
            JPanel entryPanel = createLeaderboardEntry(i + 1, score.getUsername(), score.getTime(), 
                                                     ROW_COLORS[i % ROW_COLORS.length], score.getUsername().equals(currentUser));
            leaderboardEntriesPanel.add(entryPanel);
            
            if (i < Math.min(scores.size(), 10) - 1) {
                leaderboardEntriesPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            }
        }
        
        boolean currentUserInTop10 = false;
        for (int i = 0; i < Math.min(scores.size(), 10); i++) {
            if (scores.get(i).getUsername().equals(currentUser)) {
                currentUserInTop10 = true;
                break;
            }
        }
        
        if (!currentUserInTop10) {
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
                JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
                separator.setForeground(Color.WHITE);
                separator.setBackground(Color.BLACK);
                separator.setMaximumSize(new Dimension(470, 2));
                leaderboardEntriesPanel.add(Box.createRigidArea(new Dimension(0, 10)));
                leaderboardEntriesPanel.add(separator);
                leaderboardEntriesPanel.add(Box.createRigidArea(new Dimension(0, 10)));
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
        
        JLabel rankLbl = new JLabel(String.valueOf(position));
        rankLbl.setFont(new Font("Arial", Font.BOLD, 32));
        rankLbl.setForeground(new Color(33, 37, 41));
        rankLbl.setPreferredSize(new Dimension(60, 60));
        rankLbl.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(rankLbl, BorderLayout.WEST);
        
        JLabel userLbl = new JLabel(username);
        userLbl.setFont(new Font("Arial", Font.BOLD, 24));
        userLbl.setForeground(new Color(33, 37, 41));
        userLbl.setBorder(new EmptyBorder(0, 15, 0, 0));
        panel.add(userLbl, BorderLayout.CENTER);
        
        JPanel scorePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 20));
        scorePanel.setOpaque(false);
        
        ImageIcon icon = new ImageIcon("images/time.png");
        JLabel timeIcon = new JLabel();
        timeIcon.setPreferredSize(new Dimension(24, 24));
        timeIcon.setIcon(icon);
        timeIcon.setOpaque(false);
        
        String timeDisplay = time == Math.floor(time) ? String.format("%.0f", time) : String.valueOf(time);
        JLabel timeLbl = new JLabel(timeDisplay);
        timeLbl.setFont(new Font("Arial", Font.BOLD, 18));
        timeLbl.setForeground(new Color(33, 37, 41));
        
        if (username.equalsIgnoreCase(MazeRunner.currentUser)) {
        	rankLbl.setForeground(new Color(255, 255, 255));
        	userLbl.setForeground(new Color(255, 255, 255));
        	timeLbl.setForeground(new Color(255, 255, 255));
        }
        
        scorePanel.add(timeLbl);
        scorePanel.add(timeIcon);
        panel.add(scorePanel, BorderLayout.EAST);
        
        return panel;
    }
}