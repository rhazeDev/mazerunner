package mazerunner;

import java.awt.Point;
import java.util.*;

public class MazeGenerator {
    public static final int WALL = 1;
    public static final int PATH = 0;
    
    private int width;
    private int height;
    private int[][] maze;
    private Random random;
    
    public MazeGenerator(int width, int height) {
        this.width = width;
        this.height = height;
        this.random = new Random();
        this.maze = new int[height][width];
    }
    
    public int[][] generateMaze() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                maze[y][x] = WALL;
            }
        }
        carvePathFrom(1, 1);
        maze[1][1] = PATH;
        
        return maze;
    }
    
    private void carvePathFrom(int x, int y) {
        int[] dirs = {0, 1, 2, 3};
        shuffleArray(dirs);
        
        maze[y][x] = PATH;
        
        for (int dir : dirs) {
            int nx = x, ny = y;
            switch (dir) {
                case 0: // North
                    ny -= 2;
                    break;
                case 1: // East
                    nx += 2;
                    break;
                case 2: // South
                    ny += 2;
                    break;
                case 3: // West
                    nx -= 2;
                    break;
            }
            
            if (nx > 0 && nx < width-1 && ny > 0 && ny < height-1 && maze[ny][nx] == WALL) {
                maze[(ny + y) / 2][(nx + x) / 2] = PATH;
                carvePathFrom(nx, ny);
            }
        }
    }
    
    private void shuffleArray(int[] array) {
        for (int i = array.length - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);
            int temp = array[index];
            array[index] = array[i];
            array[i] = temp;
        }
    }
    
    public Point getRandomEmptyCell() {
        ArrayList<Point> emptyCells = new ArrayList<>();
        
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (maze[y][x] == PATH) {
                    if (x == 1 && y == 1) continue;
                    emptyCells.add(new Point(x, y));
                }
            }
        }
        
        if (emptyCells.isEmpty()) {
            return new Point(1, 1);
        } else {
            return emptyCells.get(random.nextInt(emptyCells.size()));
        }
    }
    
    public int getWidth() {
        return width;
    }
    
    public int getHeight() {
        return height;
    }
}