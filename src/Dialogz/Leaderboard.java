package Dialogz;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
    private static final Color APP_BG = new Color(15, 23, 42);
    private static final Color SURFACE = new Color(30, 41, 59);
    private static final Color SURFACE_ALT = new Color(51, 65, 85);
    private static final Color TEXT_PRIMARY = new Color(248, 250, 252);
    private static final Color TEXT_MUTED = new Color(203, 213, 225);
    private static final Color ACCENT = new Color(59, 130, 246);
    private static final Color ACCENT_DARK = new Color(37, 99, 235);
    private static final Color BORDER = new Color(71, 85, 105);
    
    private Database database;
    private String currentUser;
    
    private static final Color[] ROW_COLORS = {
            new Color(245, 158, 11),
            new Color(148, 163, 184),
            new Color(180, 83, 9),
            SURFACE_ALT,
            SURFACE_ALT,
            SURFACE_ALT,
            SURFACE_ALT,
            SURFACE_ALT,
            SURFACE_ALT,
            SURFACE_ALT
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
        mainPanel.setBackground(APP_BG);
        contentPane.add(mainPanel, BorderLayout.CENTER);
        
        JLabel titleLabel = new JLabel("Leaderboard", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 40));
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setBounds(0, 10, 490, 50);
        mainPanel.add(titleLabel);
        
        difficultyPanel = new JPanel();
        difficultyPanel.setLayout(new GridLayout(1, 3, 10, 0));
        difficultyPanel.setBounds(10, 70, 470, 40);
        difficultyPanel.setOpaque(false);
        mainPanel.add(difficultyPanel);
        
        easyBtn = createDifficultyButton("Easy", "EASY");
        mediumBtn = createDifficultyButton("Medium", "MEDIUM");
        hardBtn = createDifficultyButton("Hard", "HARD");
        
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
        backBtn.setForeground(TEXT_PRIMARY);
        backBtn.setBackground(ACCENT);
        backBtn.setFocusPainted(false);
        backBtn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER),
                new EmptyBorder(10, 0, 10, 0)));
        backBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        contentPane.add(backBtn, BorderLayout.SOUTH);
        
        loadLeaderboardData();
    }
    
    private JButton createDifficultyButton(String text, String difficulty) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 16));
        btn.setFocusPainted(false);
        btn.setForeground(TEXT_PRIMARY);
        btn.setContentAreaFilled(true);
        btn.setBorder(BorderFactory.createLineBorder(BORDER, 1));
        
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
        easyBtn.setBackground(currentDifficulty.equals("EASY") ? ACCENT : SURFACE);
        mediumBtn.setBackground(currentDifficulty.equals("MEDIUM") ? ACCENT : SURFACE);
        hardBtn.setBackground(currentDifficulty.equals("HARD") ? ACCENT : SURFACE);

        easyBtn.setBorder(BorderFactory.createLineBorder(currentDifficulty.equals("EASY") ? ACCENT_DARK : BORDER, 1));
        mediumBtn.setBorder(BorderFactory.createLineBorder(currentDifficulty.equals("MEDIUM") ? ACCENT_DARK : BORDER, 1));
        hardBtn.setBorder(BorderFactory.createLineBorder(currentDifficulty.equals("HARD") ? ACCENT_DARK : BORDER, 1));

        easyBtn.setForeground(TEXT_PRIMARY);
        mediumBtn.setForeground(TEXT_PRIMARY);
        hardBtn.setForeground(TEXT_PRIMARY);
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
                separator.setForeground(BORDER);
                separator.setBackground(BORDER);
                separator.setMaximumSize(new Dimension(470, 2));
                leaderboardEntriesPanel.add(Box.createRigidArea(new Dimension(0, 10)));
                leaderboardEntriesPanel.add(separator);
                leaderboardEntriesPanel.add(Box.createRigidArea(new Dimension(0, 10)));
                JPanel userPanel = createLeaderboardEntry(position, currentUser, time, 
                                                        ACCENT_DARK, true);
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
        panel.setBorder(BorderFactory.createLineBorder(BORDER, 1));
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel rankLbl = new JLabel(String.valueOf(position));
        rankLbl.setFont(new Font("Arial", Font.BOLD, 32));
        rankLbl.setForeground(TEXT_PRIMARY);
        rankLbl.setPreferredSize(new Dimension(60, 60));
        rankLbl.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(rankLbl, BorderLayout.WEST);
        
        JLabel userLbl = new JLabel(username);
        userLbl.setFont(new Font("Arial", Font.BOLD, 24));
        userLbl.setForeground(TEXT_PRIMARY);
        userLbl.setBorder(new EmptyBorder(0, 15, 0, 0));
        panel.add(userLbl, BorderLayout.CENTER);
        
        JPanel scorePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 20));
        scorePanel.setOpaque(false);
        
        ImageIcon icon = MazeRunner.ImageAssets.loadIcon(Leaderboard.class, "/images/time.png");
        JLabel timeIcon = new JLabel();
        timeIcon.setPreferredSize(new Dimension(24, 24));
        timeIcon.setIcon(icon);
        timeIcon.setOpaque(false);
        
        String timeDisplay = String.format(Locale.US, "%.2f", time);
        JLabel timeLbl = new JLabel(timeDisplay);
        timeLbl.setFont(new Font("Arial", Font.BOLD, 18));
        timeLbl.setForeground(TEXT_MUTED);
        
        if (username.equalsIgnoreCase(MazeRunner.currentUser)) {
        	rankLbl.setForeground(TEXT_PRIMARY);
        	userLbl.setForeground(TEXT_PRIMARY);
        	timeLbl.setForeground(TEXT_PRIMARY);
        }
        
        scorePanel.add(timeLbl);
        scorePanel.add(timeIcon);
        panel.add(scorePanel, BorderLayout.EAST);
        
        return panel;
    }
}