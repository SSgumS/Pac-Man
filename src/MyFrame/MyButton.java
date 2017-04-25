package MyFrame;

import javax.swing.*;

/**
 * Created by Saeed on 4/24/2017.
 */

public class MyButton extends JButton {
    enum Difficulty {
        Easy, Medium, Hard
    }

    public MyButton(String text, MyFrame myListener) {
        super(text);

        addMouseListener(myListener);
    }
}
