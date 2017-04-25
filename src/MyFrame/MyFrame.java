package MyFrame;

import Map.Map;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * Created by Saeed on 4/24/2017.
 */

public class MyFrame extends JFrame implements MouseListener {
    Map Map;
    private BackPanel backPanel = new BackPanel(new BorderLayout());
    private JPanel mainPanel = new JPanel(null);
    private JLabel logoLabel, nameLabel, diffLabel;
    private JTextField nameField;
    private MyButton diffBut, exit, play;

    String name;

    private Font font;
    private Font logoFont;
    private Font labelFont;

    public static void main(String[] args) {
        new MyFrame("Pac-Man");
    }

    public MyFrame(String title) throws HeadlessException {
        super(title);

        setLookAndFeel();

        setExtendedState(MAXIMIZED_BOTH);
        setResizable(false);
        setUndecorated(true);
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        gd.setFullScreenWindow(this);

        labelFont = new Font("CrackMan", Font.PLAIN, getHeight()/12);
        logoFont = new Font("CrackMan", Font.BOLD, getHeight()/5);
        setLogoLabel();
        setNameLabel();

        font = new Font("CrackMan", Font.PLAIN, getHeight()/10);
        setNameField();

        setDiffLabel();

        setDiffBut();

        setExit();

        setPlay();

        mainPanel.setOpaque(false);
        setContentPane(backPanel);

        backPanel.add(mainPanel);
        setVisible(true);
    }

    private void setLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | UnsupportedLookAndFeelException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void setLogoLabel() {
        logoLabel = new JLabel("Pac-Man");

        logoLabel.setFont(logoFont);
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);

        logoLabel.setSize(getWidth(), getHeight()/4);

        mainPanel.add(logoLabel);
    }

    private void setNameLabel() {
        nameLabel = new JLabel("Player Name:");

        nameLabel.setFont(labelFont);
        nameLabel.setHorizontalAlignment(SwingConstants.CENTER);

        nameLabel.setSize(getWidth(), getHeight()/8);
        nameLabel.setLocation(0, logoLabel.getY() + logoLabel.getHeight());

        mainPanel.add(nameLabel);
    }

    private void setNameField() {
        nameField = new JTextField();

        nameField.setFont(font);
        nameField.setHorizontalAlignment(SwingConstants.CENTER);

        nameField.setSize(getWidth()*3/4, getHeight()/8);
        nameField.setLocation(getWidth()/2 - nameField.getWidth()/2, nameLabel.getY() + nameLabel.getHeight());

        mainPanel.add(nameField);
    }

    private void setDiffLabel() {
        diffLabel = new JLabel("Difficulty:");

        diffLabel.setFont(labelFont);
        diffLabel.setHorizontalAlignment(SwingConstants.CENTER);

        diffLabel.setSize(getWidth(), getHeight()/8);
        diffLabel.setLocation(0, nameField.getY() + nameField.getHeight());

        mainPanel.add(diffLabel);
    }

    private void setDiffBut() {
        diffBut = new MyButton("Easy", this);

        diffBut.setFont(font);

        diffBut.setSize(getWidth()*3/4, getHeight()/8);
        diffBut.setLocation(getWidth()/2 - diffBut.getWidth()/2, diffLabel.getY() + diffLabel.getHeight());

        mainPanel.add(diffBut);
    }

    private void setExit() {
        exit = new MyButton("Exit", this);

        exit.setFont(font);

        exit.setSize(getWidth()/4, getHeight()/8);
        exit.setLocation(exit.getWidth()/2, diffBut.getY() + diffBut.getHeight() + exit.getHeight()/2);

        mainPanel.add(exit);
    }

    private void setPlay() {
        play = new MyButton("Play", this);

        play.setFont(font);

        play.setSize(getWidth()/4, getHeight()/8);
        play.setLocation(getWidth()/2 + play.getWidth()/2, diffBut.getY() + diffBut.getHeight() + play.getHeight()/2);

        mainPanel.add(play);
    }

    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1){
            if (e.getSource().equals(diffBut)) {
                switch (MyButton.Difficulty.valueOf(((MyButton) e.getSource()).getText())) {
                    case Easy:
                        diffBut.setText(MyButton.Difficulty.Medium.name());
                        break;
                    case Medium:
                        diffBut.setText(MyButton.Difficulty.Hard.name());
                        break;
                    case Hard:
                        diffBut.setText(MyButton.Difficulty.Easy.name());
                        break;
                }
            } else if (e.getSource().equals(play)) {
            } else if (e.getSource().equals(exit)) {
                System.exit(0);
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}
}
