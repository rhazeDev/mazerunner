package mazerunner;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class Monster {
    private int x, y;
    private boolean dead;
    private GamePanel gamePanel;
    private ImageIcon monsterImage;
    private Random random;
    private boolean verticalMovement;
    private int direction;
    private int moveCounter = 0;
    private static final int MOVE_DELAY = 3;

    public Monster(int x, int y, GamePanel gamePanel) {
        this.x = x;
        this.y = y;
        this.dead = false;
        this.gamePanel = gamePanel;
        this.random = new Random();
        this.verticalMovement = random.nextBoolean();
        this.direction = verticalMovement ? (random.nextBoolean() ? 0 : 2) : (random.nextBoolean() ? 1 : 3);

        try {
            monsterImage = new ImageIcon(getClass().getResource("/images/monster.png"));
            if (monsterImage.getIconWidth() <= 0) {
                monsterImage = new ImageIcon("images/monster.png");
            }
        } catch (Exception e) {
            System.out.println("Error loading monster image: " + e.getMessage());
            monsterImage = null;
        }
    }

    public void draw(Graphics g, int offsetX, int offsetY, int cellSize) {
        int screenX = x * cellSize + offsetX;
        int screenY = y * cellSize + offsetY;

        if (monsterImage != null) {
            g.drawImage(monsterImage.getImage(), screenX, screenY, cellSize, cellSize, null);
        } else {
            g.setColor(Color.RED);
            g.fillOval(screenX + 5, screenY + 5, cellSize - 10, cellSize - 10);
        }
    }

    public void update() {
        if (dead) return;
        moveCounter++;
        if (moveCounter < MOVE_DELAY) {
            return;
        }
        moveCounter = 0;
        
        int dx = 0, dy = 0;

        if (verticalMovement) {
            if (direction == 0) {
                dy = -1;
            } else {
                dy = 1;
            }
        } else {
            if (direction == 1) {
                dx = 1;
            } else {
                dx = -1;
            }
        }

        if (gamePanel.canMoveTo(x + dx, y + dy)) {
            x += dx;
            y += dy;
        } else {
            if (verticalMovement) {
                direction = (direction == 0) ? 2 : 0;
            } else {
                direction = (direction == 1) ? 3 : 1;
            }
        }
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isDead() {
        return dead;
    }

    public void setDead(boolean dead) {
        this.dead = dead;
    }
}