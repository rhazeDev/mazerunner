package mazerunner;

import javax.swing.*;
import java.awt.*;

public class Bomb extends GameElement {
    private ImageIcon bombImage;
    private boolean exploded;

    public Bomb(int x, int y) {
        super(x, y);
        this.exploded = false;
        
        try {
            bombImage = new ImageIcon(getClass().getResource("/images/bomb.png"));
            if (bombImage.getIconWidth() <= 0) {
                bombImage = new ImageIcon("images/bomb.png");
            }
        } catch (Exception e) {
            System.out.println("Error loading bomb image: " + e.getMessage());
            bombImage = null;
        }
    }

    public void draw(Graphics g, int offsetX, int offsetY, int cellSize) {
        if (exploded) {
            g.setColor(Color.RED);
            g.fillOval(x * cellSize + offsetX, y * cellSize + offsetY, cellSize, cellSize);
            g.setColor(Color.ORANGE);
            g.drawString("BOOM!", x * cellSize + offsetX + 5, y * cellSize + offsetY + cellSize / 2);
            return;
        }

        int screenX = x * cellSize + offsetX;
        int screenY = y * cellSize + offsetY;

        if (bombImage != null) {
            g.drawImage(bombImage.getImage(), screenX, screenY, cellSize, cellSize, null);
        } else {
            g.setColor(Color.BLACK);
            g.fillOval(screenX + 5, screenY + 5, cellSize - 10, cellSize - 10);
        }
    }

    // kitan na if masagid jay bomba
    public void onCollision(Player player) {
        GamePanel gamePanel = player.getGamePanel();
        if (gamePanel != null) {
            exploded = true;
            gamePanel.endGame(false);
        }
    }

    public void onShot() {
        exploded = true;
    }

    public boolean isExploded() {
        return exploded;
    }
}