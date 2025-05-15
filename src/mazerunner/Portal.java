package mazerunner;

import javax.swing.*;
import java.awt.*;

public class Portal extends GameElement {
    private ImageIcon portalImage;
    private Point targetPosition;
    private static final long COOLDOWN_TIME = 1000;
    private static Point lastPlayerPosition = null;
    private static Point lastTeleportDestination = null;
    private static long lastTeleportTime = 0;

    public Portal(int x, int y, Point targetPosition) {
        super(x, y);
        this.targetPosition = targetPosition;

        try {
            portalImage = new ImageIcon(getClass().getResource("/images/portal.png"));
            if (portalImage.getIconWidth() <= 0) {
                portalImage = new ImageIcon("images/portal.png");
            }
        } catch (Exception e) {
            System.out.println("Error loading portal image: " + e.getMessage());
            portalImage = null;
        }
    }

    public void draw(Graphics g, int offsetX, int offsetY, int cellSize) {
        int screenX = x * cellSize + offsetX;
        int screenY = y * cellSize + offsetY;

        if (portalImage != null) {
            g.drawImage(portalImage.getImage(), screenX, screenY, cellSize, cellSize, null);
        } else {
            g.setColor(Color.MAGENTA);
            g.fillRect(screenX, screenY, cellSize, cellSize);
        }
    }

    public void onCollision(Player player) {
        long currentTime = System.currentTimeMillis();
        
        if (currentTime - lastTeleportTime < COOLDOWN_TIME) {
            return;
        }
        
        if (lastTeleportDestination != null && 
            lastTeleportDestination.x == x && 
            lastTeleportDestination.y == y &&
            currentTime - lastTeleportTime < 2000) {
            return;
        }
        
        // Play portal sound
        SoundManager.getInstance().playSound(SoundManager.PORTAL_ENTER);
        
        lastPlayerPosition = new Point(player.getX(), player.getY());
        lastTeleportTime = currentTime;
        lastTeleportDestination = new Point(targetPosition);
        
        player.teleport(targetPosition.x, targetPosition.y);
    }
}