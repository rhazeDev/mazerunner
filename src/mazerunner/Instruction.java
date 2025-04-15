package mazerunner;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.Color;
import java.awt.GridLayout;
import javax.swing.SwingConstants;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.Cursor;
import javax.swing.ScrollPaneConstants;
import javax.swing.BorderFactory;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.awt.Dimension;

public class Instruction extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JScrollPane scrollPane;
	private JPanel instructionPanel;

	/**
	 * Launch the application.
	 */
	/**
	 * Create the frame.
	 */
	public Instruction() {
		setTitle("Maze Runner - Instructions");
		setResizable(false);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 703, 800);
		
		// Custom content pane with gradient background
		contentPane = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2d = (Graphics2D) g.create();
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				
				GradientPaint gp = new GradientPaint(0, 0, new Color(220, 230, 240), 
						0, getHeight(), new Color(240, 240, 240));
				g2d.setPaint(gp);
				g2d.fillRect(0, 0, getWidth(), getHeight());
				g2d.dispose();
			}
		};
		
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("GAME INSTRUCTION");
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 32));
		lblNewLabel.setBounds(164, 11, 363, 57);
		contentPane.add(lblNewLabel);
		
		// Create a stylish navigation panel
		JPanel navPanel = new JPanel();
		navPanel.setBounds(10, 70, 667, 30);
		navPanel.setOpaque(false);
		navPanel.setLayout(null);
		contentPane.add(navPanel);
		
		// Add navigation buttons with improved styling
		JButton btnJumpToElements = createStyledButton("Game Elements", 10, 0, 140, 30);
		btnJumpToElements.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				scrollPane.getVerticalScrollBar().setValue(0);
			}
		});
		navPanel.add(btnJumpToElements);
		
		JButton btnJumpToHowToPlay = createStyledButton("How To Play", 260, 0, 140, 30);
		btnJumpToHowToPlay.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				scrollPane.getVerticalScrollBar().setValue(460);
			}
		});
		navPanel.add(btnJumpToHowToPlay);
		
		JButton btnJumpToControls = createStyledButton("Controls", 510, 0, 140, 30);
		btnJumpToControls.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				scrollPane.getVerticalScrollBar().setValue(830);
			}
		});
		navPanel.add(btnJumpToControls);
		
		// Adjusted scroll pane with improved styling
		scrollPane = new JScrollPane();
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setBounds(10, 105, 667, 585);
		scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 220), 1));
		contentPane.add(scrollPane);
		
		// Create a panel to hold the instructions
		instructionPanel = new JPanel();
		instructionPanel.setBackground(Color.WHITE);
		instructionPanel.setLayout(null);
		scrollPane.setViewportView(instructionPanel);
		
		// Set the preferred size for the instruction panel
		instructionPanel.setPreferredSize(new Dimension(650, 1160));
		
		// Create game elements section with improved styling
		JLabel lblGameElements = createSectionHeader("Game Elements", 20, 20);
		instructionPanel.add(lblGameElements);
		
		// Create a panel for game elements with icons
		JPanel elementsPanel = new JPanel();
		elementsPanel.setBounds(20, 60, 610, 400);
		elementsPanel.setLayout(new GridLayout(0, 2, 10, 10));
		elementsPanel.setBackground(Color.WHITE);
		instructionPanel.add(elementsPanel);
		
		// Add game elements with icons
		addElementWithIcon(elementsPanel, "Player", "images/player.png", 
				"The character you control to navigate through the maze.");
		
		addElementWithIcon(elementsPanel, "Wall", "images/wall.png", 
				"Obstacles that block your path. You cannot move through walls.");
		
		addElementWithIcon(elementsPanel, "Floor", "images/floor.png", 
				"Empty spaces where you can move freely.");
		
		addElementWithIcon(elementsPanel, "Door", "images/door.png", 
				"Doors that is locked and required key to open.");
		
		addElementWithIcon(elementsPanel, "Opened Door", "images/opened_door.png", 
				"A door that has been unlocked, enter to finish the maze.");
		
		addElementWithIcon(elementsPanel, "Key", "images/key.png", 
				"Collect to unlock the door in the maze.");
		
		addElementWithIcon(elementsPanel, "Monster", "images/monster.png", 
				"Enemies that move through the maze. Avoid them or shoot them to eliminate.");
		
		addElementWithIcon(elementsPanel, "Bomb", "images/bomb.png", 
				"Explosive that will destroy you if you touch it. Shoot to destroy.");
		
		addElementWithIcon(elementsPanel, "Bullet Pickup", "images/bullet_pickup.png", 
				"Collect to gain bullets that can be used against monsters.");
		
		addElementWithIcon(elementsPanel, "Portal", "images/portal.png", 
				"Teleports you to another location in the maze.");
		
		// Game instructions section with improved styling
		JLabel lblHowToPlay = createSectionHeader("How To Play", 20, 480);
		instructionPanel.add(lblHowToPlay);
		
		JTextArea txtInstructions = createStyledTextArea(20, 520, 610, 330);
		txtInstructions.setText(
				"Welcome to Maze Runner!\n\n" +
				"OBJECTIVE:\n" +
				"Navigate through the maze, collect keys to open doors, avoid or defeat monsters, and find the exit.\n\n" +
				"CONTROLS:\n" +
				"- Use the ARROW KEYS or WASD key to move your character in four directions.\n" +
				"- Press SPACEBAR to shoot bullets at monsters and bombs.\n\n" +
				"GAMEPLAY TIPS:\n" +
				"1. Collect keys to unlock door.\n" +
				"2. Avoid monsters or shoot them with bullets.\n" +
				"3. Avoid bomb or shoot them with bullets.\n" +
				"4. Use portal to teleport to another location\n" +
				"5. Collect bullet to increase your ammo. (5 max).\n\n" +
				"Complete the maze as quickly as possible to earn a high score!"
		);
		instructionPanel.add(txtInstructions);
		
		// Controls section with improved styling
		JLabel lblControls = createSectionHeader("Game Controls", 20, 870);
		instructionPanel.add(lblControls);
		
		JTextArea txtControls = createStyledTextArea(20, 910, 610, 220);
		txtControls.setText(
				"MOVEMENT:\n" +
				"- Up Arrow or W: Move up\n" +
				"- Down Arrow or S: Move down\n" +
				"- Left Arrow or F: Move left\n" +
				"- Right Arrow or D: Move right\n\n" +
				"ACTIONS:\n" +
				"- Spacebar: Shoot bullet\n" +
				"- ESC: Pause game\n\n" +
				"Automatically collect items by moving over them."
		);
		instructionPanel.add(txtControls);
		
		// Add close button at the bottom with improved styling
		JButton btnClose = new JButton("Close") {
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2d = (Graphics2D) g.create();
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				
				GradientPaint gp = new GradientPaint(0, 0, new Color(70, 130, 180), 
						0, getHeight(), new Color(50, 100, 150));
				g2d.setPaint(gp);
				g2d.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 10, 10));
				
				super.paintComponent(g2d);
				g2d.dispose();
			}
		};
		
		btnClose.setFont(new Font("Tahoma", Font.BOLD, 15));
		btnClose.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		btnClose.setFocusPainted(false);
		btnClose.setContentAreaFilled(false);
		btnClose.setBorderPainted(false);
		btnClose.setOpaque(false);
		btnClose.setForeground(Color.WHITE);
		btnClose.setBounds(290, 710, 120, 40);
		btnClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose(); // Close the frame
			}
		});
		contentPane.add(btnClose);
		
		// Add hover effect to the close button
		btnClose.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseEntered(java.awt.event.MouseEvent evt) {
				btnClose.setForeground(new Color(255, 255, 200));
			}
			
			public void mouseExited(java.awt.event.MouseEvent evt) {
				btnClose.setForeground(Color.WHITE);
			}
		});
		
		// Make sure to reset scroll position to the top when the frame is shown
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
				// Make sure scroll starts at the top
				scrollPane.getVerticalScrollBar().setValue(0);
				// Small delay to ensure proper scrolling
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						scrollPane.getVerticalScrollBar().setValue(0);
					}
				});
			}
			
			@Override
			public void windowClosing(WindowEvent e) {
				dispose(); // Properly dispose of the frame when closed
			}
		});
		
		// Center the frame on the screen
		setLocationRelativeTo(null);
	}
	
	/**
	 * Creates a styled section header
	 */
	private JLabel createSectionHeader(String text, int x, int y) {
		JLabel label = new JLabel(text);
		label.setFont(new Font("Tahoma", Font.BOLD, 18));
		label.setForeground(new Color(50, 50, 120));
		label.setBounds(x, y, 200, 30);
		return label;
	}
	
	/**
	 * Creates a styled text area
	 */
	private JTextArea createStyledTextArea(int x, int y, int width, int height) {
		JTextArea textArea = new JTextArea();
		textArea.setEditable(false);
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		textArea.setFont(new Font("Tahoma", Font.PLAIN, 14));
		textArea.setBounds(x, y, width, height);
		textArea.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(new Color(220, 220, 240), 1),
				BorderFactory.createEmptyBorder(10, 10, 10, 10)));
		return textArea;
	}
	
	/**
	 * Creates a styled navigation button
	 */
	private JButton createStyledButton(String text, int x, int y, int width, int height) {
		JButton button = new JButton(text) {
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2d = (Graphics2D) g.create();
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				
				if (getModel().isPressed()) {
					g2d.setColor(new Color(60, 100, 160));
				} else if (getModel().isRollover()) {
					g2d.setColor(new Color(80, 120, 180));
				} else {
					g2d.setColor(new Color(100, 150, 200));
				}
				
				g2d.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 10, 10));
				super.paintComponent(g2d);
				g2d.dispose();
			}
		};
		
		button.setFont(new Font("Tahoma", Font.BOLD, 12));
		button.setForeground(Color.WHITE);
		button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		button.setFocusPainted(false);
		button.setBorderPainted(false);
		button.setContentAreaFilled(false);
		button.setOpaque(false);
		button.setBounds(x, y, width, height);
		
		return button;
	}
	
	/**
	 * Helper method to add a game element with its icon to the panel
	 * @param panel The panel to add the element to
	 * @param name The name of the element
	 * @param iconPath The path to the icon image
	 * @param description A brief description of the element
	 */
	private void addElementWithIcon(JPanel panel, String name, String iconPath, String description) {
		// Create a panel for this element with rounded corners and better styling
		JPanel elementPanel = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2d = (Graphics2D) g.create();
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2d.setColor(getBackground());
				g2d.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 10, 10));
				g2d.dispose();
			}
		};
		
		elementPanel.setLayout(null);
		elementPanel.setBackground(Color.WHITE);
		elementPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(new Color(230, 230, 240), 1),
				BorderFactory.createEmptyBorder(0, 0, 0, 0)));
		
		// Add the icon
		JLabel iconLabel = new JLabel();
		iconLabel.setBounds(10, 5, 40, 40);
		try {
			ImageIcon icon = new ImageIcon(iconPath);
			iconLabel.setIcon(icon);
		} catch (Exception e) {
			iconLabel.setText("Icon");
		}
		elementPanel.add(iconLabel);
		
		// Add the name with bold font
		JLabel nameLabel = new JLabel(name);
		nameLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
		nameLabel.setForeground(new Color(60, 80, 120));
		nameLabel.setBounds(60, 5, 200, 20);
		elementPanel.add(nameLabel);
		
		// Add the description
		JTextArea descLabel = new JTextArea(description);
		descLabel.setEditable(false);
		descLabel.setLineWrap(true);
		descLabel.setWrapStyleWord(true);
		descLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
		descLabel.setBackground(Color.WHITE);
		descLabel.setBounds(60, 25, 230, 40);
		elementPanel.add(descLabel);
		
		// Add hover effect to make the UI more interactive
		elementPanel.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseEntered(java.awt.event.MouseEvent evt) {
				elementPanel.setBackground(new Color(245, 245, 255));
				elementPanel.setBorder(BorderFactory.createCompoundBorder(
						BorderFactory.createLineBorder(new Color(100, 150, 200), 1),
						BorderFactory.createEmptyBorder(0, 0, 0, 0)));
				descLabel.setBackground(new Color(245, 245, 255));
				nameLabel.setForeground(new Color(40, 60, 140));
			}
			
			public void mouseExited(java.awt.event.MouseEvent evt) {
				elementPanel.setBackground(Color.WHITE);
				elementPanel.setBorder(BorderFactory.createCompoundBorder(
						BorderFactory.createLineBorder(new Color(230, 230, 240), 1),
						BorderFactory.createEmptyBorder(0, 0, 0, 0)));
				descLabel.setBackground(Color.WHITE);
				nameLabel.setForeground(new Color(60, 80, 120));
			}
		});
		
		// Add the element panel to the main panel
		panel.add(elementPanel);
	}
}