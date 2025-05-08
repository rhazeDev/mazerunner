package Dialogz;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;

import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GameResult extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JLabel gameTimeLabel;
	private JLabel bestTimeLabel;
	private JLabel difficultyLabel;
	private JLabel rankLabel;
	private JLabel resultLabel;
	private JButton playAgainBtn;
	private JButton mainMenuBtn;
	private static final Color GOLD_COLOR = new Color(255, 215, 0);
	private ActionListener mainMenuAction;
	
	/**
	 * Create the frame.
	 */
	public GameResult() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 500);
		setLocationRelativeTo(null);
		setResizable(false);
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if (mainMenuAction != null) {
					mainMenuAction.actionPerformed(null);
				}
			}
		});
		
		contentPane = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2d = (Graphics2D) g;
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				
				GradientPaint gp = new GradientPaint(
					0, 0, new Color(41, 65, 115), 
					0, getHeight(), new Color(70, 130, 180)
				);
				g2d.setPaint(gp);
				g2d.fillRect(0, 0, getWidth(), getHeight());
			}
		};
		contentPane.setBorder(new EmptyBorder(15, 15, 15, 15));
		setContentPane(contentPane);
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
		
		JPanel difficultyPanel = new JPanel();
		difficultyPanel.setOpaque(false);
		difficultyPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
		difficultyPanel.setLayout(new BorderLayout());
		
		difficultyLabel = new JLabel("MEDIUM", SwingConstants.CENTER);
		difficultyLabel.setOpaque(true);
		difficultyLabel.setBackground(new Color(0, 191, 255));
		difficultyLabel.setForeground(new Color(0, 0, 102));
		difficultyLabel.setFont(new Font("Tahoma", Font.BOLD, 16));
		difficultyLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
		difficultyPanel.add(difficultyLabel, BorderLayout.CENTER);
		
		contentPane.add(difficultyPanel);
		contentPane.add(Box.createVerticalStrut(20));
		
		resultLabel = new JLabel("GAME FINISHED", SwingConstants.CENTER);
		resultLabel.setForeground(Color.WHITE);
		resultLabel.setFont(new Font("Tahoma", Font.BOLD, 32));
		resultLabel.setAlignmentX(CENTER_ALIGNMENT);
		contentPane.add(resultLabel);
		contentPane.add(Box.createVerticalStrut(30));
		
		JLabel bestLabel = new JLabel("BEST TIME", SwingConstants.CENTER);
		bestLabel.setForeground(GOLD_COLOR);
		bestLabel.setFont(new Font("Tahoma", Font.BOLD, 28));
		bestLabel.setAlignmentX(CENTER_ALIGNMENT);
		contentPane.add(bestLabel);
		contentPane.add(Box.createVerticalStrut(5));
		
		JPanel bestTimePanel = new JPanel();
		bestTimePanel.setOpaque(false);
		bestTimePanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 0));
		
		try {
			ImageIcon timeIcon = new ImageIcon(getClass().getResource("/images/time.png"));
			if (timeIcon.getIconWidth() <= 0) {
				timeIcon = new ImageIcon("images/time.png");
			}
			JLabel bestTimeIcon = new JLabel(timeIcon);
			bestTimePanel.add(bestTimeIcon);
		} catch (Exception e) {
			System.out.println("Could not load time icon: " + e.getMessage());
		}
		
		bestTimeLabel = new JLabel("00.00", SwingConstants.CENTER);
		bestTimeLabel.setForeground(GOLD_COLOR);
		bestTimeLabel.setFont(new Font("Tahoma", Font.BOLD, 24));
		bestTimePanel.add(bestTimeLabel);
		
		bestTimePanel.setAlignmentX(CENTER_ALIGNMENT);
		contentPane.add(bestTimePanel);
		contentPane.add(Box.createVerticalStrut(20));
		
		JLabel gameLabel = new JLabel("GAME TIME", SwingConstants.CENTER);
		gameLabel.setForeground(Color.WHITE);
		gameLabel.setFont(new Font("Tahoma", Font.BOLD, 28));
		gameLabel.setAlignmentX(CENTER_ALIGNMENT);
		contentPane.add(gameLabel);
		contentPane.add(Box.createVerticalStrut(5));
		
		JPanel gameTimePanel = new JPanel();
		gameTimePanel.setOpaque(false);
		gameTimePanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 0));
		
		try {
			ImageIcon timeIcon = new ImageIcon(getClass().getResource("/images/time.png"));
			if (timeIcon.getIconWidth() <= 0) {
				timeIcon = new ImageIcon("images/time.png");
			}
			JLabel gameTimeIcon = new JLabel(timeIcon);
			gameTimePanel.add(gameTimeIcon);
		} catch (Exception e) {
			System.out.println("Could not load time icon: " + e.getMessage());
		}
		
		gameTimeLabel = new JLabel("00.00", SwingConstants.CENTER);
		gameTimeLabel.setForeground(Color.WHITE);
		gameTimeLabel.setFont(new Font("Tahoma", Font.BOLD, 24));
		gameTimePanel.add(gameTimeLabel);
		
		gameTimePanel.setAlignmentX(CENTER_ALIGNMENT);
		contentPane.add(gameTimePanel);
		contentPane.add(Box.createVerticalStrut(30));
		
		JPanel buttonsPanel = new JPanel();
		buttonsPanel.setOpaque(false);
		buttonsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 0));
		buttonsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
		
		playAgainBtn = new JButton("PLAY AGAIN");
		playAgainBtn.setFont(new Font("Tahoma", Font.BOLD, 14));
		playAgainBtn.setForeground(Color.WHITE);
		playAgainBtn.setBackground(new Color(46, 125, 50));
		playAgainBtn.setPreferredSize(new Dimension(180, 50));
		buttonsPanel.add(playAgainBtn);
		
		mainMenuBtn = new JButton("MENU");
		mainMenuBtn.setFont(new Font("Tahoma", Font.BOLD, 14));
		mainMenuBtn.setForeground(Color.WHITE);
		mainMenuBtn.setBackground(new Color(21, 101, 192));
		mainMenuBtn.setPreferredSize(new Dimension(180, 50));
		buttonsPanel.add(mainMenuBtn);
		
		buttonsPanel.setAlignmentX(CENTER_ALIGNMENT);
		contentPane.add(buttonsPanel);
		contentPane.add(Box.createVerticalStrut(20));
		
		rankLabel = new JLabel("Rank #1", SwingConstants.CENTER);
		rankLabel.setForeground(GOLD_COLOR);
		rankLabel.setFont(new Font("Tahoma", Font.BOLD, 20));
		rankLabel.setAlignmentX(CENTER_ALIGNMENT);
		contentPane.add(rankLabel);
	}
	
	public void setTimes(String gameTime, String bestTime, boolean isNewBest, boolean isGameWon) {
		gameTimeLabel.setText(gameTime);
		bestTimeLabel.setText(bestTime);
		
		resultLabel.setText(isGameWon ? "GAME FINISHED" : "GAME OVER");
		
		if (isNewBest && isGameWon) {
			gameTimeLabel.setForeground(GOLD_COLOR);
		} else {
			gameTimeLabel.setForeground(Color.WHITE);
		}
	}
	
	public void setDifficulty(String difficulty) {
		difficultyLabel.setText(difficulty);
		
		if (difficulty.equalsIgnoreCase("EASY")) {
			difficultyLabel.setBackground(new Color(76, 175, 80));
		} else if (difficulty.equalsIgnoreCase("MEDIUM")) {
			difficultyLabel.setBackground(new Color(0, 191, 255));
		} else if (difficulty.equalsIgnoreCase("HARD")) {
			difficultyLabel.setBackground(new Color(244, 67, 54));
		}
	}
	
	public void setRank(int rank, boolean isNewBest) {
		if (rank <= 0) {
			rankLabel.setText("No Rank");
		} else if (isNewBest) {
			rankLabel.setText("New Rank #" + rank);
		} else {
			rankLabel.setText("Rank #" + rank);
		}
	}
	
	public void setButtonActions(ActionListener playAgainAction, ActionListener mainMenuAction) {
		for (ActionListener al : playAgainBtn.getActionListeners()) {
			playAgainBtn.removeActionListener(al);
		}
		
		for (ActionListener al : mainMenuBtn.getActionListeners()) {
			mainMenuBtn.removeActionListener(al);
		}
		
		playAgainBtn.addActionListener(playAgainAction);
		mainMenuBtn.addActionListener(mainMenuAction);
		
		this.mainMenuAction = mainMenuAction;
	}
}
