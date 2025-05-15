package mazerunner;

import javax.swing.*;
import java.awt.*;

public class Door extends GameElement {
    private ImageIcon doorImage;
    private ImageIcon openDoorImage;
    private boolean open;
    private boolean winDialogShown = false;
    
    public Door(int x, int y) {
        super(x, y);
        this.open = false;
        
        try {
            doorImage = new ImageIcon(getClass().getResource("/images/door.png"));
            openDoorImage = new ImageIcon(getClass().getResource("/images/opened_door.png"));
            
            if (doorImage == null || doorImage.getIconWidth() <= 0) {
                doorImage = new ImageIcon("images/door.png");
            }
            
            if (openDoorImage == null || openDoorImage.getIconWidth() <= 0) {
                openDoorImage = new ImageIcon("images/opened_door.png");
            }
        } catch (Exception e) {
            System.out.println("Error loading door images: " + e.getMessage());
            doorImage = null;
            openDoorImage = null;
        }
    }
    
    public void draw(Graphics g, int offsetX, int offsetY, int cellSize) {
        int screenX = x * cellSize + offsetX;
        int screenY = y * cellSize + offsetY;
        
        if (open && openDoorImage != null) {
            g.drawImage(openDoorImage.getImage(), screenX, screenY, cellSize, cellSize, null);
        } else if (doorImage != null) {
            g.drawImage(doorImage.getImage(), screenX, screenY, cellSize, cellSize, null);
        } else {
            g.setColor(open ? Color.GREEN : Color.ORANGE);
            g.fillRect(screenX, screenY, cellSize, cellSize);
        }
    }
    
    public void onCollision(Player player) {
        if (player.hasKey() && !winDialogShown) {
            open = true;
            winDialogShown = true;
            
            // Play door opening sound
            SoundManager.getInstance().playSound(SoundManager.DOOR_OPEN);
            
            GamePanel gamePanel = player.getGamePanel();
            if (gamePanel != null) {
                Timer endGameTimer = new Timer(1000, e -> gamePanel.endGame(true));
                endGameTimer.setRepeats(false);
                endGameTimer.start();
            }
        }
    }
    
    public void setOpen(boolean open) {
        if (!this.open && open) {
            // Play door opening sound when door state changes from closed to open
            SoundManager.getInstance().playSound(SoundManager.DOOR_OPEN);
        }
        this.open = open;
    }
}