package Map;

import Ghost.Ghost;
import PacMan.PacMan;
import Messages.Messages;
import MyFrame.MyFrame;

import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

/**
 * Created by Saeed on 4/24/2017.
 */

public class Map extends JPanel implements KeyListener {
    private MyFrame myFrame;
    private GameListener gameListener;
    PacMan pacMan;
    Thread pacManThread;
    Ghost[] ghosts = new Ghost[4];
    Thread[] ghostThreads = new Thread[4];
    private int[][] initMap;
    int[][] map;
    public static int height, width;
    public static int imageSize = MyFrame.height/27;
    public static boolean isPlaying;
    public static int level;
    private int pacPhase = 0;
    private JLabel healthLabel;
    private JLabel diffLabel;

    public Rectangle[][] wallRecs;

    private Font labelFont;

    private Timer paintTimer = new Timer(16, e -> {
        gameListener.checkEnd();

        gameListener.checkIntersect();

        if (pacPhase == 3)
            pacPhase = 0;
        else
            pacPhase++;

        repaint();
    });
    private Timer startTimer = new Timer(1000, e -> {
        pacManThread = new Thread(pacMan);
        pacManThread.start();
        for (int i = 0; i < 4; i++) {
            ghostThreads[i] = new Thread(ghosts[i]);
            ghostThreads[i].start();
        }
    });

    private Scanner mapScanner;

    private BufferedImage image;
    private BufferedImage[] rightPac = new BufferedImage[4];
    private BufferedImage[] leftPac = new BufferedImage[4];
    private BufferedImage[] upPac = new BufferedImage[4];
    private BufferedImage[] downPac = new BufferedImage[4];
    private BufferedImage[] rightGhosts = new BufferedImage[4];
    private BufferedImage[] leftGhosts = new BufferedImage[4];
    private BufferedImage[] downGhosts = new BufferedImage[4];
    private BufferedImage[] upGhosts = new BufferedImage[4];
    private BufferedImage[] drunkGhost = new BufferedImage[2];
    private BufferedImage[] food = new BufferedImage[2];
    private BufferedImage[] walls = new BufferedImage[16];

    public Map(String pacManName, LayoutManager layout, int level, MyFrame myFrame) {
        super(layout);

        Map.level = level;

        gameListener = new GameListener(this);
        this.myFrame = myFrame;

        imageSeparator();

        try {
            File file = new File("resources" + File.separator + "map.txt");
            mapScanner = new Scanner(file);
            createMap();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        pacMan = new PacMan(pacManName, gameListener, this, 3);

        ghosts[0] = new Ghost(1, 1, 1, pacMan, this);
        ghosts[1] = new Ghost(21, 1, 3, pacMan, this);
        ghosts[2] = new Ghost(1, 23, 2, pacMan, this);
        ghosts[3] = new Ghost(21, 23, 0, pacMan, this);

        setSize(width*imageSize, MyFrame.height);
        setLocation(MyFrame.width/2 - getWidth()/2, 0);

        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(gameListener);
        addKeyListener(this);

        labelFont = new Font("CrackMan", Font.PLAIN, getHeight()/25);
        setLabels();

        isPlaying = true;
        paintTimer.start();
        startTimer.setRepeats(false);
        startTimer.start();
    }

    private void imageSeparator() {
        try {
            image = ImageIO.read(new File("resources" + File.separator + "images.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (imageSize != 40) {
            BufferedImage bufferedImage = new BufferedImage(4*imageSize, 13*imageSize, image.getType());
            bufferedImage.getGraphics().drawImage(image, 0, 0, bufferedImage.getWidth(), bufferedImage.getHeight(), null);
            image = bufferedImage;
        }

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                walls[i*4+j] = image.getSubimage(j*imageSize, i*imageSize, imageSize, imageSize);
            }
            rightGhosts[i] = image.getSubimage(i*imageSize, 5*imageSize, imageSize, imageSize);
            leftGhosts[i] = image.getSubimage(i*imageSize, 6*imageSize, imageSize, imageSize);
            downGhosts[i] = image.getSubimage(i*imageSize, 7*imageSize, imageSize, imageSize);
            upGhosts[i] = image.getSubimage(i*imageSize, 8*imageSize, imageSize, imageSize);
            rightPac[i] = image.getSubimage(i*imageSize, 9*imageSize, imageSize, imageSize);
            leftPac[i] = image.getSubimage(i*imageSize, 10*imageSize, imageSize, imageSize);
            upPac[i] = image.getSubimage(i*imageSize, 11*imageSize, imageSize, imageSize);
            downPac[i] = image.getSubimage(i*imageSize, 12*imageSize, imageSize, imageSize);
        }
        for (int i = 0; i < 2; i++) {
            food[i] = image.getSubimage(i*imageSize, 4*imageSize, imageSize, imageSize);
            drunkGhost[i] = image.getSubimage((i+2)*imageSize, 4*imageSize, imageSize, imageSize);
        }
    }

    private void createMap() {
        height = mapScanner.nextInt();
        width = mapScanner.nextInt();
        map = new int[height][width];
        initMap = new int[height][width];
        wallRecs = new Rectangle[height][width];

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                map[i][j] = initMap[i][j] = mapScanner.nextInt();
                setWallRecs(map[i][j], i, j);
            }
        }
    }

    private void setWallRecs(int point, int y, int x) {
        switch (point) {
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
            case 16:
            case 17:
            case 18:
                wallRecs[y][x] = new Rectangle(x*imageSize, y*imageSize, imageSize, imageSize);
        }
    }

    private void setLabels() {
        healthLabel = new JLabel(pacMan.getName() + "\'s Health: " + pacMan.getHealth());
        diffLabel = new JLabel("Level: " + (level + 1));

        healthLabel.setFont(labelFont);
        healthLabel.setHorizontalAlignment(SwingConstants.LEFT);
        diffLabel.setFont(labelFont);
        diffLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        healthLabel.setForeground(Color.WHITE);
        diffLabel.setForeground(Color.WHITE);

        healthLabel.setSize(getWidth()*3/4, MyFrame.height - height*imageSize);
        healthLabel.setLocation(0, getHeight() - healthLabel.getHeight());
        diffLabel.setSize(getWidth()/4, MyFrame.height - height*imageSize);
        diffLabel.setLocation(healthLabel.getWidth(), getHeight() - diffLabel.getHeight());

        add(healthLabel);
        add(diffLabel);
    }

    private void exit() {
        isPlaying = false;
        paintTimer.stop();
        KeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventDispatcher(gameListener);
        myFrame.gameExit();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                switch (map[i][j]) {
                    case 1:
                        g.drawImage(food[1], j*imageSize, i*imageSize, null);
                        break;
                    case 2:
                        g.drawImage(food[0], j*imageSize, i*imageSize, null);
                        break;
                    case 3:
                        g.drawImage(walls[9], j*imageSize, i*imageSize, null);
                        break;
                    case 4:
                        g.drawImage(walls[10], j*imageSize, i*imageSize, null);
                        break;
                    case 5:
                        g.drawImage(walls[8], j*imageSize, i*imageSize, null);
                        break;
                    case 6:
                        g.drawImage(walls[11], j*imageSize, i*imageSize, null);
                        break;
                    case 7:
                        g.drawImage(walls[0], j*imageSize, i*imageSize, null);
                        break;
                    case 8:
                        g.drawImage(walls[1], j*imageSize, i*imageSize, null);
                        break;
                    case 9:
                        g.drawImage(walls[13], j*imageSize, i*imageSize, null);
                        break;
                    case 10:
                        g.drawImage(walls[15], j*imageSize, i*imageSize, null);
                        break;
                    case 11:
                        g.drawImage(walls[12], j*imageSize, i*imageSize, null);
                        break;
                    case 12:
                        g.drawImage(walls[14], j*imageSize, i*imageSize, null);
                        break;
                    case 13:
                        g.drawImage(walls[6], j*imageSize, i*imageSize, null);
                        break;
                    case 14:
                        g.drawImage(walls[4], j*imageSize, i*imageSize, null);
                        break;
                    case 15:
                        g.drawImage(walls[5], j*imageSize, i*imageSize, null);
                        break;
                    case 16:
                        g.drawImage(walls[7], j*imageSize, i*imageSize, null);
                        break;
                    case 17:
                        g.drawImage(walls[2], j*imageSize, i*imageSize, null);
                        break;
                    case 18:
                        g.drawImage(walls[3], j*imageSize, i*imageSize, null);
                        break;
                }
            }
        }

        if (pacMan.isStrong()) {
            for (int i = 0; i < 4; i++) {
                if (ghosts[i].isEaten())
                    g.drawImage(drunkGhost[1], ghosts[i].getX(), ghosts[i].getY(), null);
                else
                    g.drawImage(drunkGhost[0], ghosts[i].getX(), ghosts[i].getY(), null);
            }
        } else {
            for (int i = 0; i < 4; i++) {
                if (ghosts[i].isEaten())
                    g.drawImage(drunkGhost[1], ghosts[i].getX(), ghosts[i].getY(), null);
                else {
                    switch (ghosts[i].getDirection()) {
                        case 0:
                            g.drawImage(leftGhosts[i], ghosts[i].getX(), ghosts[i].getY(), null);
                            break;
                        case 1:
                            g.drawImage(rightGhosts[i], ghosts[i].getX(), ghosts[i].getY(), null);
                            break;
                        case 2:
                            g.drawImage(upGhosts[i], ghosts[i].getX(), ghosts[i].getY(), null);
                            break;
                        case 3:
                            g.drawImage(downGhosts[i], ghosts[i].getX(), ghosts[i].getY(), null);
                            break;
                    }
                }
            }
        }

        switch (pacMan.getDirection()) {
            case 0:
                g.drawImage(leftPac[pacPhase], pacMan.getX(), pacMan.getY(), null);
                break;
            case 1:
                g.drawImage(rightPac[pacPhase], pacMan.getX(), pacMan.getY(), null);
                break;
            case 2:
                g.drawImage(upPac[pacPhase], pacMan.getX(), pacMan.getY(), null);
                break;
            case 3:
                g.drawImage(downPac[pacPhase], pacMan.getX(), pacMan.getY(), null);
                break;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_ESCAPE:
                exit();
                break;
            case KeyEvent.VK_SPACE:
                if (GameListener.isPaused)
                    paintTimer.stop();
                else
                    paintTimer.start();
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    protected void processComponentEvent(ComponentEvent e) {
        super.processComponentEvent(e);

        switch (e.getID()) {
            case Messages.INTERSECT:
                pacManThread.interrupt();
                for (int i = 0; i < 4; i++) {
                    ghostThreads[i].interrupt();
                }

                if (pacMan.getHealth() == 1) {
                    try {
                        new Robot().keyPress(KeyEvent.VK_ESCAPE); // TODO: read about robot
                    } catch (AWTException e1) {
                        e1.printStackTrace();
                    }
                } else {
                    pacMan = new PacMan(pacMan.getName(), gameListener, this, pacMan.getHealth() - 1);

                    ghosts[0] = new Ghost(1, 1, 1, pacMan, this);
                    ghosts[1] = new Ghost(21, 1, 3, pacMan, this);
                    ghosts[2] = new Ghost(1, 23, 2, pacMan, this);
                    ghosts[3] = new Ghost(21, 23, 0, pacMan, this);

                    healthLabel.setText(pacMan.getName() + "\'s Health: " + pacMan.getHealth());

                    startTimer.start();
                }
                break;
            case Messages.END:
                pacManThread.interrupt();
                for (int i = 0; i < 4; i++) {
                    ghostThreads[i].interrupt();
                }

                if (level == 2)
                    level = 0;
                else
                    level++;

                pacMan = new PacMan(pacMan.getName(), gameListener, this, pacMan.getHealth());

                ghosts[0] = new Ghost(1, 1, 1, pacMan, this);
                ghosts[1] = new Ghost(21, 1, 3, pacMan, this);
                ghosts[2] = new Ghost(1, 23, 2, pacMan, this);
                ghosts[3] = new Ghost(21, 23, 0, pacMan, this);

                for (int i = 0; i < height; i++) {
                    System.arraycopy(initMap[i], 0, map[i], 0, width);
                }

                diffLabel.setText("Level: " + (level + 1));

                startTimer.start();
                break;
        }
    }
}
