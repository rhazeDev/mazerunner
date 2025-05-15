package mazerunner;

import javax.swing.*;
import java.awt.*;

public class BulletPickup extends GameElement {
    private ImageIcon bulletPickupImage;
    private boolean collected;

    public BulletPickup(int x, int y) {
        super(x, y);
        this.collected = false;
        try {
            bulletPickupImage = new ImageIcon(getClass().getResource("/images/bullet_pickup.png"));
            if (bulletPickupImage.getIconWidth() <= 0) {
                bulletPickupImage = new ImageIcon("images/bullet_pickup.png");
            }
        } catch (Exception e) {
            System.out.println("Error loading bullet pickup image: " + e.getMessage());
            bulletPickupImage = null;
        }
    }

    public void draw(Graphics g, int offsetX, int offsetY, int cellSize) {
        if (collected) return;

        int screenX = x * cellSize + offsetX;
        int screenY = y * cellSize + offsetY;

        if (bulletPickupImage != null) {
            g.drawImage(bulletPickupImage.getImage(), screenX, screenY, cellSize, cellSize, null);
        } else {
            g.setColor(Color.YELLOW);
            g.fillOval(screenX + 5, screenY + 5, cellSize - 10, cellSize - 10);
        }
    }

    @Override
    public void onCollision(Player player) {
        if (!collected) {
            if (player.getBullets() < 5) {
                player.addBullet();
                collected = true;
                
                // Play bullet pickup sound
                SoundManager.getInstance().playSound(SoundManager.BULLET_PICKUP);
                
                GamePanel gamePanel = player.getGamePanel();
                if (gamePanel != null) {
                    gamePanel.getGameElements().remove(this);
                }
            }
        }
    }
}