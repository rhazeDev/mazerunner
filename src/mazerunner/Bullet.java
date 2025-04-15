package mazerunner;

import javax.swing.*;
import java.awt.*;

public class Bullet extends GameElement {
    private static final int BULLET_SPEED = 1;
    
    private int direction;
    private ImageIcon bulletImage;
    
    public Bullet(int x, int y, int direction) {
        super(x, y);
        this.direction = direction;
        
        try {
            bulletImage = new ImageIcon("./images/bullet.png");
        } catch (Exception e) {
            System.out.println("Error loading bullet image: " + e.getMessage());
            bulletImage = null;
        }
    }
    
    public void update() {
        switch (direction) {
            case 0:
                y -= BULLET_SPEED;
                break;
            case 1:
                x += BULLET_SPEED;
                break;
            case 2:
                y += BULLET_SPEED;
                break;
            case 3:
                x -= BULLET_SPEED;
                break;
        }
    }
    
    @Override
    public void draw(Graphics g, int offsetX, int offsetY, int cellSize) {
        int screenX = x * cellSize + offsetX;
        int screenY = y * cellSize + offsetY;
        
        if (bulletImage != null) {
            Graphics2D g2d = (Graphics2D) g;
            double angle = direction * Math.PI / 2;
            
            g2d.translate(screenX + cellSize/2, screenY + cellSize/2);
            g2d.rotate(angle);
            g2d.drawImage(bulletImage.getImage(), -cellSize/4, -cellSize/4, cellSize/2, cellSize/2, null);
            g2d.rotate(-angle);
            g2d.translate(-(screenX + cellSize/2), -(screenY + cellSize/2));
        } else {
            g.setColor(Color.YELLOW);
            g.fillOval(screenX + cellSize/4, screenY + cellSize/4, cellSize/2, cellSize/2);
        }
    }
    
    public void onCollision(Player player) {}
}