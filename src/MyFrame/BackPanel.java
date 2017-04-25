package MyFrame;

import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * Created by Saeed on 4/25/2017.
 */
public class BackPanel extends JPanel {
    private Image backImage = (new ImageIcon("resources" + File.separator + "menu background.jpg")).getImage();

    public BackPanel(LayoutManager layout) {
        super(layout);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        for (int i = 0; i < getWidth()/backImage.getWidth(null) + 1; i++) {
            for (int j = 0; j < getHeight()/backImage.getHeight(null) + 1; j++) {
                g.drawImage(backImage, i * backImage.getWidth(null), j * backImage.getHeight(null), null);
            }
        }
    }
}
