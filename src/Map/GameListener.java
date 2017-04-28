package Map;

import Messages.GameEvent;
import Messages.Messages;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * Created by Saeed on 4/27/2017.
 */

public class GameListener implements KeyEventDispatcher {
    private Map map;
    private KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
    public static boolean isPaused = false;

    private Timer strongTimer = new Timer(5000, e -> {
        map.pacMan.dispatchEvent(new GameEvent(map, Messages.WEAK));
        for (int i = 0; i < 4; i++)
            map.ghosts[i].dispatchEvent(new GameEvent(map, Messages.WEAK));
    });

    GameListener(Map map) {
        this.map = map;
        strongTimer.setRepeats(false);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_UP:
            case KeyEvent.VK_DOWN:
                manager.redispatchEvent(map.pacMan, e);
                return true;
            case KeyEvent.VK_ESCAPE:
                manager.redispatchEvent(map, e);
                return true;
            case KeyEvent.VK_SPACE:
                if (e.getID() == KeyEvent.KEY_PRESSED) {
                    isPaused = !isPaused;
                    manager.redispatchEvent(map, e);
                    map.pacManThread.interrupt();
                    for (int i = 0; i < 4; i++) {
                        map.ghostThreads[i].interrupt();
                    }
                }
                return true;
        }
        return false;
    } //TODO: chetori vaghti true mikoni, alt tab kar mikone? khob nabayad dge befrestatesh.

    public void eating() {
        if (map.map[(int) (map.pacMan.rectangle.getCenterY()/Map.imageSize)][(int) (map.pacMan.rectangle.getCenterX()/Map.imageSize)] == 1) {
            switch (map.pacMan.getDirection()) {
                case 0:
                case 1:
                    if (map.pacMan.rectangle.getCenterX() - (int) (map.pacMan.rectangle.getCenterX()/Map.imageSize)*Map.imageSize == Map.imageSize/2)
                        map.map[(int) (map.pacMan.rectangle.getCenterY()/Map.imageSize)][(int) (map.pacMan.rectangle.getCenterX()/Map.imageSize)] = 0;
                    break;
                case 2:
                case 3:
                    if (map.pacMan.rectangle.getCenterY() - (int) (map.pacMan.rectangle.getCenterY()/Map.imageSize)*Map.imageSize == Map.imageSize/2)
                        map.map[(int) (map.pacMan.rectangle.getCenterY()/Map.imageSize)][(int) (map.pacMan.rectangle.getCenterX()/Map.imageSize)] = 0;
                    break;
            }
        } else if (map.map[(int) (map.pacMan.rectangle.getCenterY()/Map.imageSize)][(int) (map.pacMan.rectangle.getCenterX()/Map.imageSize)] == 2) {
            switch (map.pacMan.getDirection()) {
                case 0:
                case 1:
                    if (map.pacMan.rectangle.getCenterX() - (int) (map.pacMan.rectangle.getCenterX()/Map.imageSize)*Map.imageSize == Map.imageSize/2) {
                        map.map[(int) (map.pacMan.rectangle.getCenterY()/Map.imageSize)][(int) (map.pacMan.rectangle.getCenterX()/Map.imageSize)] = 0;
                        map.pacMan.dispatchEvent(new GameEvent(map, Messages.STRONG));
                        for (int i = 0; i < 4; i++)
                            map.ghosts[i].dispatchEvent(new GameEvent(map, Messages.STRONG));
                        strongTimer.start();
                    }
                    break;
                case 2:
                case 3:
                    if (map.pacMan.rectangle.getCenterY() - (int) (map.pacMan.rectangle.getCenterY()/Map.imageSize)*Map.imageSize == Map.imageSize/2) {
                        map.map[(int) (map.pacMan.rectangle.getCenterY() / Map.imageSize)][(int) (map.pacMan.rectangle.getCenterX() / Map.imageSize)] = 0;
                        map.pacMan.dispatchEvent(new GameEvent(map, Messages.STRONG));
                        for (int i = 0; i < 4; i++)
                            map.ghosts[i].dispatchEvent(new GameEvent(map, Messages.STRONG));
                        strongTimer.start();
                    }
                    break;
            }
        }
    }

    void checkEnd() {
        int k = 0;
        for (int i = 0; i < Map.height; i++) {
            for (int j = 0; j < Map.width; j++) {
                if (map.map[i][j] == 1 || map.map[i][j] == 2) k++;
            }
        }
        if (k == 0)
            map.dispatchEvent(new GameEvent(map, Messages.END));
    }

    void checkIntersect() {
        for (int i = 0; i < 4; i++) {
            if (map.pacMan.rectangle.intersects(map.ghosts[i].rectangle)) {
                if (map.pacMan.isStrong()) {
                    if (!map.ghosts[i].isEaten())
                        map.ghosts[i].dispatchEvent(new GameEvent(map, Messages.EAT));
                } else
                    map.dispatchEvent(new GameEvent(map, Messages.INTERSECT));
            }
        }
    }
}
