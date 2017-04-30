package PacMan;

import Map.*;
import Messages.*;

import java.awt.*;
import java.awt.event.*;

/**
 * Created by Saeed on 4/26/2017.
 */

public class PacMan extends Component implements Runnable, KeyListener {
    private Map map;
    private GameListener gameListener;
    private String name;
    private int health;
    private int x = 11*Map.imageSize, y = 15*Map.imageSize;
    private int delayTime = 7;
    private int direction = 0; //0:left 1:right 2:up 3:down
    private int newDirection = 0;
    public Rectangle rectangle;
    private boolean isStrong = false;

    public PacMan(String name, GameListener gameListener, Map map, int health) {
        this.map = map;
        this.gameListener = gameListener;
        this.name = name;
        setLocation(x, y);

        this.health = health;

        rectangle = new Rectangle(x - 5, y - 5, Map.imageSize - 10, Map.imageSize - 10);

        addKeyListener(this);
    }

    public int getDirection() {
        return direction;
    }

    @Override
    public String getName() {
        return name;
    }

    public int getHealth() {
        return health;
    }

    public boolean isStrong() {
        return isStrong;
    }

    @Override
    public void run() {
        while (Map.isPlaying) {
            changeDirection();

            if (checkMove()) {
                move();
                gameListener.eating();
            }

            try {
                Thread.sleep(delayTime);
            } catch (InterruptedException e) {
                if (GameListener.isPaused) {
                    try {
                        synchronized (this) {
                            wait();
                        }
                    } catch (InterruptedException ignored) {}
                } else
                    break;
            }
        }
    }

    private void changeDirection() {
        if (newDirection != direction) {
            if (direction == 0 || direction == 1) {
                switch (newDirection) {
                    case 0:
                        direction = newDirection;
                        break;
                    case 1:
                        direction = newDirection;
                        break;
                    case 2:
                        if (x%40 == 0 && y%40 == 0)
                            direction = newDirection;
                        break;
                    case 3:
                        if (x%40 == 0 && y%40 == 0)
                            direction = newDirection;
                        break;
                }
            }
            else {
                switch (newDirection) {
                    case 2:
                        direction = newDirection;
                        break;
                    case 3:
                        direction = newDirection;
                        break;
                    case 0:
                        if (x%40 == 0 && y%40 == 0)
                            direction = newDirection;
                        break;
                    case 1:
                        if (x%40 == 0 && y%40 == 0)
                            direction = newDirection;
                        break;
                }
            }
        }
    }

    private boolean checkMove() {
        switch (direction) {
            case 0:
                Rectangle rectangle = new Rectangle(x-1, y, Map.imageSize, Map.imageSize);
                if (map.getWallRecs()[y/Map.imageSize][(x-1)/Map.imageSize] != null)
                    return !map.getWallRecs()[y / Map.imageSize][(x - 1) / Map.imageSize].intersects(rectangle);
                break;
            case 1:
                rectangle = new Rectangle(x+1, y, Map.imageSize, Map.imageSize);
                if (map.getWallRecs()[y/Map.imageSize][(x+40+1)/Map.imageSize] != null)
                    return !map.getWallRecs()[y/Map.imageSize][(x+40+1)/Map.imageSize].intersects(rectangle);
                break;
            case 2:
                rectangle = new Rectangle(x, y-1, Map.imageSize, Map.imageSize);
                if (map.getWallRecs()[(y-1)/Map.imageSize][x/Map.imageSize] != null)
                    return !map.getWallRecs()[(y - 1) / Map.imageSize][x / Map.imageSize].intersects(rectangle);
                break;
            case 3:
                rectangle = new Rectangle(x, y+1, Map.imageSize, Map.imageSize);
                if (map.getWallRecs()[(y+40+1)/Map.imageSize][x/Map.imageSize] != null)
                    return !map.getWallRecs()[(y+40+1)/Map.imageSize][x/Map.imageSize].intersects(rectangle);
                break;
        }
        return true;
    }

    private void move() {
        switch (direction) {
            case 0:
                if (x > 0) x--;
                else x = Map.width*Map.imageSize - Map.imageSize;
                setLocation(x, y);
                rectangle = new Rectangle(x - 5, y - 5, Map.imageSize - 10, Map.imageSize - 10);
                break;
            case 1:
                if (x < Map.width*Map.imageSize - Map.imageSize - 2) x++;
                else x = 0;
                setLocation(x, y);
                rectangle = new Rectangle(x - 5, y - 5, Map.imageSize - 10, Map.imageSize - 10);
                break;
            case 2:
                y--;
                setLocation(x, y);
                rectangle = new Rectangle(x - 5, y - 5, Map.imageSize - 10, Map.imageSize - 10);
                break;
            case 3:
                y++;
                setLocation(x, y);
                rectangle = new Rectangle(x - 5, y - 5, Map.imageSize - 10, Map.imageSize - 10);
                break;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                newDirection = 0;
                break;
            case KeyEvent.VK_RIGHT:
                newDirection = 1;
                break;
            case KeyEvent.VK_UP:
                newDirection = 2;
                break;
            case KeyEvent.VK_DOWN:
                newDirection = 3;
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    protected void processComponentEvent(ComponentEvent e) {
        super.processComponentEvent(e);

        switch (e.getID()) {
            case Messages.STRONG:
                isStrong = true;
                delayTime -= 2;
                break;
            case Messages.WEAK:
                isStrong = false;
                delayTime += 2;
                break;
        }
    }
}
