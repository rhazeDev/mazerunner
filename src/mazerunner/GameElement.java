package mazerunner;

import java.awt.*;

public abstract class GameElement {
    protected int x, y;
    
    public GameElement(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public abstract void draw(Graphics g, int offsetX, int offsetY, int cellSize);
    
    public abstract void onCollision(Player player);
    
    public int getX() {
        return x;
    }
    
    public int getY() {
        return y;
    }
}