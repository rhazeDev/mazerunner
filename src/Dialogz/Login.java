package Dialogz;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import mazerunner.MazeRunner;

import java.awt.*;
import java.awt.event.*;

public class Login extends JFrame {
    private static final long serialVersionUID = 1L;
    private static final Color APP_BG = new Color(15, 23, 42);
    private static final Color CARD_BG = new Color(15, 23, 42);
    private static final Color INPUT_BG = new Color(248, 250, 252);
    private static final Color TEXT_PRIMARY = new Color(248, 250, 252);
    private static final Color TEXT_MUTED = new Color(203, 213, 225);
    private static final Color ACCENT = new Color(59, 130, 246);
    private static final Color ACCENT_HOVER = new Color(37, 99, 235);
    private static final Color DANGER = new Color(239, 68, 68);
    private static final Color BORDER = new Color(71, 85, 105);

    private JPanel contentPane;
    private JTextField usernameField;
    private JButton btnLogin;
    private JLabel lblStatus;
    private String loggedInUsername = null;

    /**
     * Create the frame.
     */
    public Login() {
        setTitle("Maze Runner - Login");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 500, 600);
        setLocationRelativeTo(null);
        setResizable(false);
        
        contentPane = new JPanel();
        setContentPane(contentPane);
        contentPane.setLayout(new BorderLayout(0, 0));
        contentPane.setBackground(APP_BG);
        
        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(CARD_BG);
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setPreferredSize(new Dimension(420, 430));
        mainPanel.setMaximumSize(new Dimension(420, 430));

        JPanel wrapperPanel = new JPanel();
        wrapperPanel.setLayout(new BoxLayout(wrapperPanel, BoxLayout.Y_AXIS));
        wrapperPanel.setBackground(APP_BG);
        wrapperPanel.add(Box.createVerticalGlue());
        wrapperPanel.add(mainPanel);
        wrapperPanel.add(Box.createVerticalGlue());

        contentPane.add(wrapperPanel, BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 25, 15, 25);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        
        JLabel titleLabel = new JLabel("Who's playing");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(30, 25, 50, 25);
        mainPanel.add(titleLabel, gbc);
        
        JLabel usernameLabel = new JLabel("Username");
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        usernameLabel.setForeground(TEXT_MUTED);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.insets = new Insets(10, 25, 5, 25);
        mainPanel.add(usernameLabel, gbc);
        
        usernameField = new JTextField();
        usernameField.setFont(new Font("Arial", Font.PLAIN, 22));
        usernameField.setBackground(INPUT_BG);
        usernameField.setForeground(new Color(15, 23, 42));
        usernameField.setCaretColor(new Color(15, 23, 42));
        usernameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)));
        usernameField.setPreferredSize(new Dimension(100, 50));
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.insets = new Insets(5, 25, 40, 25);
        gbc.ipady = 15;
        mainPanel.add(usernameField, gbc);
        gbc.ipady = 0;
        
        btnLogin = new JButton("ENTER GAME");
        btnLogin.setFont(new Font("Arial", Font.BOLD, 18));
        btnLogin.setBackground(ACCENT);
        btnLogin.setForeground(TEXT_PRIMARY);
        btnLogin.setFocusPainted(false);
        btnLogin.setBorder(BorderFactory.createLineBorder(ACCENT_HOVER, 1));
        btnLogin.setPreferredSize(new Dimension(180, 50));
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(CARD_BG);
        buttonPanel.add(btnLogin);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.insets = new Insets(20, 25, 30, 25);
        mainPanel.add(buttonPanel, gbc);
        
        lblStatus = new JLabel("");
        lblStatus.setFont(new Font("Arial", Font.BOLD, 14));
        lblStatus.setForeground(DANGER);
        lblStatus.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.insets = new Insets(0, 25, 20, 25);
        mainPanel.add(lblStatus, gbc);
        
        btnLogin.addActionListener(e -> login());
        btnLogin.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnLogin.setBackground(ACCENT_HOVER);
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                btnLogin.setBackground(ACCENT);
            }
        });
        
        usernameField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    login();
                }
            }
        });
    }
    
    private void login() {
        String username = usernameField.getText().trim();
        
        if (username.isEmpty()) {
            lblStatus.setText("Please enter a username");
            return;
        }
        
        if (username.length() < 3) {
            lblStatus.setText("Username must be at least 3 characters");
            return;
        }
        
        loggedInUsername = username;
        lblStatus.setText("");
        
        launchGame();
    }
    
    private void launchGame() {
        dispose();
        
        SwingUtilities.invokeLater(() -> {
            MazeRunner.setCurrentUser(loggedInUsername);
            MazeRunner.refreshUser();
        });
    }
}