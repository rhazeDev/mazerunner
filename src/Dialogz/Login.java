package Dialogz;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import mazerunner.MazeRunner;

import java.awt.*;
import java.awt.event.*;

public class Login extends JFrame {
    private static final long serialVersionUID = 1L;
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
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 400, 500);
        setLocationRelativeTo(null);
        setResizable(true);
        
        contentPane = new JPanel();
        setContentPane(contentPane);
        contentPane.setLayout(new BorderLayout(0, 0));
        
        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(new Color(70, 130, 180));
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setPreferredSize(new Dimension(390, 500));
        mainPanel.setMaximumSize(new Dimension(390, 500));

        // Wrap it with a container panel to center and control layout
        JPanel wrapperPanel = new JPanel();
        wrapperPanel.setLayout(new BoxLayout(wrapperPanel, BoxLayout.Y_AXIS));
        wrapperPanel.setBackground(new Color(200, 218, 233)); // same as mainPanel
        wrapperPanel.add(Box.createVerticalGlue()); // push it to center vertically
        wrapperPanel.add(mainPanel);
        wrapperPanel.add(Box.createVerticalGlue());

        contentPane.add(wrapperPanel, BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 25, 15, 25);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        
        JLabel titleLabel = new JLabel("Who's playing");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(30, 25, 50, 25);
        mainPanel.add(titleLabel, gbc);
        
        JLabel usernameLabel = new JLabel("Username");
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        usernameLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.insets = new Insets(10, 25, 5, 25);
        mainPanel.add(usernameLabel, gbc);
        
        usernameField = new JTextField();
        usernameField.setFont(new Font("Arial", Font.PLAIN, 24));
        usernameField.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        usernameField.setPreferredSize(new Dimension(100, 50));
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.insets = new Insets(5, 25, 40, 25);
        gbc.ipady = 15;
        mainPanel.add(usernameField, gbc);
        gbc.ipady = 0;
        
        // Login button
        btnLogin = new JButton("ENTER GAME");
        btnLogin.setFont(new Font("Arial", Font.BOLD, 18));
        btnLogin.setBackground(new Color(255, 255, 249));
        btnLogin.setForeground(Color.BLACK);
        btnLogin.setFocusPainted(false);
        btnLogin.setBorderPainted(false);
        btnLogin.setPreferredSize(new Dimension(180, 50));
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(new Color(70, 130, 180));
        buttonPanel.add(btnLogin);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.insets = new Insets(20, 25, 30, 25);
        mainPanel.add(buttonPanel, gbc);
        
        // Status label for error messages
        lblStatus = new JLabel("");
        lblStatus.setFont(new Font("Arial", Font.BOLD, 14));
        lblStatus.setForeground(Color.YELLOW);
        lblStatus.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.insets = new Insets(0, 25, 20, 25);
        mainPanel.add(lblStatus, gbc);
        
        btnLogin.addActionListener(e -> login());
        btnLogin.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnLogin.setBackground(new Color(250, 250, 250));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                btnLogin.setBackground(new Color(255, 255, 249));
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
            MazeRunner game = new MazeRunner();
        });
    }
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            Login frame = new Login();
            frame.setVisible(true);
        });
    }
}