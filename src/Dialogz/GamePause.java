package Dialogz;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;

import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class GamePause extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JButton resumeBtn;
	private JButton restartBtn;
	private JButton menuBtn;
	private ActionListener menuAction;

	/**
	 * Create the frame.
	 */
	public GamePause() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setTitle("Game Paused");
		setBounds(100, 100, 500, 250);
		setLocationRelativeTo(null);
		setResizable(false);
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if (menuAction != null) {
					menuAction.actionPerformed(null);
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
		
		JLabel pauseTitle = new JLabel("GAME PAUSED", SwingConstants.CENTER);
		pauseTitle.setForeground(Color.WHITE);
		pauseTitle.setFont(new Font("Tahoma", Font.BOLD, 32));
		pauseTitle.setAlignmentX(CENTER_ALIGNMENT);
		contentPane.add(pauseTitle);
		contentPane.add(Box.createVerticalStrut(50));
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setOpaque(false);
		buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 0));
		buttonPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
		
		resumeBtn = new JButton("RESUME");
		resumeBtn.setFont(new Font("Tahoma", Font.BOLD, 14));
		resumeBtn.setForeground(Color.WHITE);
		resumeBtn.setBackground(new Color(46, 125, 50));
		resumeBtn.setPreferredSize(new Dimension(110, 40));
		buttonPanel.add(resumeBtn);
		
		restartBtn = new JButton("RESTART");
		restartBtn.setFont(new Font("Tahoma", Font.BOLD, 14));
		restartBtn.setForeground(Color.WHITE);
		restartBtn.setBackground(new Color(255, 152, 0));
		restartBtn.setPreferredSize(new Dimension(110, 40));
		buttonPanel.add(restartBtn);
		
		menuBtn = new JButton("MENU");
		menuBtn.setFont(new Font("Tahoma", Font.BOLD, 14));
		menuBtn.setForeground(Color.WHITE);
		menuBtn.setBackground(new Color(21, 101, 192));
		menuBtn.setPreferredSize(new Dimension(110, 40));
		buttonPanel.add(menuBtn);
		
		contentPane.add(buttonPanel);
	}
	
	public void setButtonActions(ActionListener resumeAction, ActionListener restartAction, ActionListener menuAction) {
		for (ActionListener al : resumeBtn.getActionListeners()) {
			resumeBtn.removeActionListener(al);
		}
		
		for (ActionListener al : restartBtn.getActionListeners()) {
			restartBtn.removeActionListener(al);
		}
		
		for (ActionListener al : menuBtn.getActionListeners()) {
			menuBtn.removeActionListener(al);
		}
		
		resumeBtn.addActionListener(resumeAction);
		restartBtn.addActionListener(restartAction);
		menuBtn.addActionListener(menuAction);
		
		this.menuAction = menuAction;
	}
}
