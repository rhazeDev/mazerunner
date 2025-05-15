package mazerunner;

import javax.swing.*;
import java.awt.*;

public class Key extends GameElement {
    private ImageIcon keyImage;
    private boolean collected;
    
    public Key(int x, int y) {
        super(x, y);
        this.collected = false;
        
        try {
            keyImage = new ImageIcon(getClass().getResource("/images/key.png"));
            if (keyImage.getIconWidth() <= 0) {
                keyImage = new ImageIcon("images/key.png");
            }
        } catch (Exception e) {
            System.out.println("Error loading key image: " + e.getMessage());
            keyImage = null;
        }
    }
    
    public void draw(Graphics g, int offsetX, int offsetY, int cellSize) {
        if (collected) return;
        
        int screenX = x * cellSize + offsetX;
        int screenY = y * cellSize + offsetY;
        
        if (keyImage != null) {
            g.drawImage(keyImage.getImage(), screenX, screenY, cellSize, cellSize, null);
        } else {
            g.setColor(Color.YELLOW);
            g.fillRect(screenX + 10, screenY + 10, cellSize - 20, cellSize - 20);
            g.fillOval(screenX + cellSize - 15, screenY + 5, 10, 10);
        }
    }
    
    public void onCollision(Player player) {
        if (!collected) {
            player.setHasKey(true);
            collected = true;
            
            // Play key collection sound (similar to door opening)
            SoundManager.getInstance().playSound(SoundManager.DOOR_OPEN);
            
            GamePanel gamePanel = player.getGamePanel();
            if (gamePanel != null) {
                gamePanel.getGameElements().remove(this);
            }
        }
    }
}