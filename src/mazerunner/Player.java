package mazerunner;

import javax.swing.*;
import java.awt.*;

public class Player {
    private int x, y;
    private int direction;
    private boolean hasKey;
    private int bullets;
    private GamePanel gamePanel;
    private ImageIcon playerImage;
    
    public Player(int x, int y, GamePanel gamePanel) {
        this.x = x;
        this.y = y;
        this.direction = 1;
        this.gamePanel = gamePanel;
        this.bullets = 3;
        this.hasKey = false;
        
        try {
            playerImage = new ImageIcon("./images/player.png");
        } catch (Exception e) {
            System.out.println("Error loading player image: " + e.getMessage());
            playerImage = null;
        }
    }
    
    public void move(int dx, int dy) {
        int newX = x + dx;
        int newY = y + dy;
        
        if (gamePanel.canMoveTo(newX, newY)) {
            x = newX;
            y = newY;
        }
    }
    
    public void draw(Graphics g, int offsetX, int offsetY, int cellSize) {
        int screenX = x * cellSize + offsetX;
        int screenY = y * cellSize + offsetY;
        
        if (playerImage != null) {
            Graphics2D g2d = (Graphics2D) g;
            double angle = direction * Math.PI / 2;
            
            g2d.translate(screenX + cellSize/2, screenY + cellSize/2);
            g2d.rotate(angle);
            g2d.drawImage(playerImage.getImage(), -cellSize/2, -cellSize/2, cellSize, cellSize, null);
            g2d.rotate(-angle);
            g2d.translate(-(screenX + cellSize/2), -(screenY + cellSize/2));
        } else {
            g.setColor(Color.BLUE);
            g.fillOval(screenX + 2, screenY + 2, cellSize - 4, cellSize - 4);
            
            g.setColor(Color.WHITE);
            int indicatorSize = cellSize / 4;
            int centerX = screenX + cellSize / 2;
            int centerY = screenY + cellSize / 2;
            
            switch (direction) {
                case 0: // Up
                    g.fillOval(centerX - indicatorSize/2, screenY + 5, indicatorSize, indicatorSize);
                    break;
                case 1: // Right
                    g.fillOval(screenX + cellSize - 5 - indicatorSize, centerY - indicatorSize/2, indicatorSize, indicatorSize);
                    break;
                case 2: // Down
                    g.fillOval(centerX - indicatorSize/2, screenY + cellSize - 5 - indicatorSize, indicatorSize, indicatorSize);
                    break;
                case 3: // Left
                    g.fillOval(screenX + 5, centerY - indicatorSize/2, indicatorSize, indicatorSize);
                    break;
            }
        }
    }
    
    public GamePanel getGamePanel() {
        return gamePanel;
    }
    
    public int getX() {
        return x;
    }
    
    public int getY() {
        return y;
    }
    
    public int getDirection() {
        return direction;
    }
    
    public void setDirection(int direction) {
        this.direction = direction;
    }
    
    public boolean hasKey() {
        return hasKey;
    }
    
    public void setHasKey(boolean hasKey) {
        this.hasKey = hasKey;
    }
    
    public int getBullets() {
        return bullets;
    }
    
    public void addBullet() {
        if (bullets < 5) {
            bullets++;
        }
    }
    
    public void useBullet() {
        if (bullets > 0) {
            bullets--;
        }
    }
    
    public void teleport(int x, int y) {
        this.x = x;
        this.y = y;
    }
}