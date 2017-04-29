package Ghost;

import Map.*;
import Messages.Messages;
import PacMan.PacMan;

import java.awt.*;
import java.awt.event.ComponentEvent;
import java.lang.reflect.*;

/**
 * Created by Saeed on 4/26/2017.
 */

public class Ghost extends Component implements Runnable {
    private Map map;
    private Rectangle wallRecs[][] = new Rectangle[Map.height][Map.width];
    private PacMan pacMan;
    private int x, y;
    private int homeX, homeY;
    private int delayTime;
    private int direction; //0: left 1:right 2:up 3:down
    private boolean[] ableDirs = new boolean[4];
    private boolean mustChangeDir;
    public Rectangle rectangle;
    private boolean isEaten = false;

    private Method moveMode;

    public Ghost(int x, int y, int direction, PacMan pacMan, Map map) {
        this.map = map;
        this.pacMan = pacMan;
        this.x = x*Map.imageSize;
        this.y = y*Map.imageSize;
        homeX = x;
        homeY = y;
        setLocation(this.x, this.y);
        this.direction = direction;

        switch (Map.level) {
            case 0:
                delayTime = 6;
                break;
            case 1:
                delayTime = 7;
                break;
            case 2:
                delayTime = 8;
                break;
        }
        setMoveMode(Map.level);

        setWallRecs(homeX, homeY);

        rectangle = new Rectangle(this.x - 5, this.y - 5, Map.imageSize - 10, Map.imageSize - 10);

        enableEvents(ComponentEvent.COMPONENT_EVENT_MASK);
    }

    public int getDirection() {
        return direction;
    }

    public boolean isEaten() {
        return isEaten;
    }

    private void setWallRecs(int x, int y) {
        switch (direction) {
            case 0:
                x--;
                break;
            case 1:
                x++;
                break;
            case 2:
                y--;
                break;
            case 3:
                y++;
                break;
        }

        for (int i = 0; i < Map.height; i++) {
            for (int j = 0; j < Map.width; j++) {
                if (!(i == y && j == x))
                    wallRecs[i][j] = map.getWallRecs()[i][j];
            }
        }
    }

    @Override
    public void run() {
        while (Map.isPlaying) {
            if (x%40 == 0 && y%40 == 0) {
                setAbleDirs();
                mustChangeDir = !ableDirs[direction];
                try {
                    moveMode.invoke(this);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }

            move();

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

    private void setAbleDirs() {
        ableDirs[0] = (int) (rectangle.getCenterX() / Map.imageSize) == 0 || wallRecs[(int) (rectangle.getCenterY() / Map.imageSize)][(int) (rectangle.getCenterX() / Map.imageSize) - 1] == null;
        ableDirs[1] = (int) (rectangle.getCenterX() / Map.imageSize) == Map.width - 1 || wallRecs[(int) (rectangle.getCenterY() / Map.imageSize)][(int) (rectangle.getCenterX() / Map.imageSize) + 1] == null;
        ableDirs[2] = wallRecs[(int) (rectangle.getCenterY() / Map.imageSize) - 1][(int) (rectangle.getCenterX() / Map.imageSize)] == null;
        ableDirs[3] = wallRecs[(int) (rectangle.getCenterY() / Map.imageSize) + 1][(int) (rectangle.getCenterX() / Map.imageSize)] == null;
    }

    private void setMoveMode(int mode) {
        try {
            switch (mode) {
                case 0:
                    moveMode = this.getClass().getDeclaredMethod("find0");
                    break;
                case 1:
                    moveMode = this.getClass().getDeclaredMethod("find1");
                    break;
                case 2:
                    moveMode = this.getClass().getDeclaredMethod("find2");
                    break;
                case 3:
                    moveMode = this.getClass().getDeclaredMethod("escape");
                    break;
                case 4:
                    moveMode = this.getClass().getDeclaredMethod("goHome");
                    break;
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    private void find0() {
        int k = 0;
        for (int i = 0; i < 4; i++)
            if (ableDirs[i]) k++;
        int[] ables = new int[k];
        int kk = 0;
        for (int i = 0; i < 4; i++) {
            if (ableDirs[i]) {
                ables[kk] = i;
                kk++;
            }
        }

        if (mustChangeDir)
            direction = ables[(int) (Math.random() * ables.length)];
        else {
            int num = (int) (Math.random() * (ables.length + 2));
            if (num < ables.length)
                direction = ables[num];
        }
    }

    private void find1() {
        boolean pacManFound = false;
        for (int i = 0; i < 4; i++) {
            if (ableDirs[i]) {
                int x = 0, y = 0;
                switch (i) {
                    case 0:
                        x = -1;
                        break;
                    case 1:
                        x = 1;
                        break;
                    case 2:
                        y = -1;
                        break;
                    case 3:
                        y = 1;
                        break;
                }

                for (int j = 1; j < 6; j++) {
                    if ((int) (rectangle.getCenterX()/Map.imageSize) + x*j == -1 || (int) (rectangle.getCenterX()/Map.imageSize) + x*j == Map.width || wallRecs[(int) (rectangle.getCenterY()/Map.imageSize) + y*j][(int) (rectangle.getCenterX()/Map.imageSize) + x*j] != null)
                        break;
                    else {
                        if ((int) (pacMan.rectangle.getCenterX()/Map.imageSize) == (int) (rectangle.getCenterX()/Map.imageSize) + x*j && (int) (pacMan.rectangle.getCenterY()/Map.imageSize) == (int) (rectangle.getCenterY()/Map.imageSize) + y*j) {
                            direction = i;
                            pacManFound = true;
                            break;
                        }
                    }
                }
            }

            if (pacManFound) break;
        }
        if (!pacManFound) find0();
    }

    private void find2() {
        boolean pacManFound = false;
        for (int i = 0; i < 4; i++) {
            if (ableDirs[i]) {
                int x = 0, y = 0;
                switch (i) {
                    case 0:
                        x = -1;
                        break;
                    case 1:
                        x = 1;
                        break;
                    case 2:
                        y = -1;
                        break;
                    case 3:
                        y = 1;
                        break;
                }

                for (int j = 1; j < 11; j++) {
                    if ((int) (rectangle.getCenterX()/Map.imageSize) + x*j == -1 || (int) (rectangle.getCenterX()/Map.imageSize) + x*j == Map.width || wallRecs[(int) (rectangle.getCenterY()/Map.imageSize) + y*j][(int) (rectangle.getCenterX()/Map.imageSize) + x*j] != null)
                        break;
                    else {
                        if ((int) (pacMan.rectangle.getCenterX()/Map.imageSize) == (int) (rectangle.getCenterX()/Map.imageSize) + x*j && (int) (pacMan.rectangle.getCenterY()/Map.imageSize) == (int) (rectangle.getCenterY()/Map.imageSize) + y*j) {
                            direction = i;
                            pacManFound = true;
                            break;
                        }
                    }
                }
            }

            if (pacManFound) break;
        }
        if (!pacManFound) find0();
    }

    private void escape() {
        boolean pacManFound = false;
        int x = 0, y = 0;
        for (int i = 0; i < 4; i++) {
            if (ableDirs[i]) {
                switch (i) {
                    case 0:
                        x = -1;
                        break;
                    case 1:
                        x = 1;
                        break;
                    case 2:
                        y = -1;
                        break;
                    case 3:
                        y = 1;
                        break;
                }

                for (int j = 1; j < 6; j++) {
                    if ((int) (rectangle.getCenterX()/Map.imageSize) + x*j == -1 || (int) (rectangle.getCenterX()/Map.imageSize) + x*j == Map.width || wallRecs[(int) (rectangle.getCenterY()/Map.imageSize) + y*j][(int) (rectangle.getCenterX()/Map.imageSize) + x*j] != null)
                        break;
                    else {
                        if ((int) (pacMan.rectangle.getCenterX()/Map.imageSize) == (int) (rectangle.getCenterX()/Map.imageSize) + x*j && (int) (pacMan.rectangle.getCenterY()/Map.imageSize) == (int) (rectangle.getCenterY()/Map.imageSize) + y*j) {
                            for (int k = 0; k < 4; k++) {
                                if (ableDirs[k] && k != i)
                                    direction = k;
                            }
                            pacManFound = true;
                            break;
                        }
                    }
                }
            }

            if (pacManFound) break;
        }
        if (!pacManFound) find0();
    }

    private void goHome() {
        find0();
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
    protected void processComponentEvent(ComponentEvent e) {
        super.processComponentEvent(e);

        switch (e.getID()) {
            case Messages.EAT:
                isEaten = true;
                setMoveMode(4);
                break;
            case Messages.STRONG:
                setMoveMode(3);
                break;
            case Messages.WEAK:
                isEaten = false;
                setMoveMode(Map.level);
                break;
        }
    }
}
