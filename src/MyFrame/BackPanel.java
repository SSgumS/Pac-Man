package MyFrame;

import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;

/**
 * Created by Saeed on 4/25/2017.
 */
public class BackPanel extends JPanel {
    private File src = new File("resources" + File.separator + "menu background.jpg");
    private BufferedImage image;

    BackPanel(LayoutManager layout) {
        super(layout);

        try {
            image = ImageIO.read(src);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void switchBackImage() {
        try {
            if (("resources" + File.separator + "menu background.jpg").equals(src.getPath())) {
                src = new File("resources" + File.separator + "game background.jpg");
                image = ImageIO.read(src);
            } else {
                src = new File("resources" + File.separator + "menu background.jpg");
                image = ImageIO.read(src);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        for (int i = 0; i < getWidth()/image.getWidth() + 1; i++) {
            for (int j = 0; j < getHeight()/image.getHeight() + 1; j++) {
                g.drawImage(image, i * image.getWidth(), j * image.getHeight(), null);
            }
        }
    }
}
