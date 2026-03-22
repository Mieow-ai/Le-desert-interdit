package vue;

import javax.swing.*;
import java.awt.*;
import modele.Plateau;

public class VuePlateau extends JPanel {
    public VuePlateau(Plateau plateau){
        this.setLayout(new GridLayout(5, 5, 5, 5));
        this.setBackground(Color.GRAY);

        for(int i = 0;i<5;i++){
            for(int j = 0;j<5;j++){
                JPanel cp = new JPanel();
                cp.setPreferredSize(new Dimension(100, 100));
                cp.setBackground(Color.ORANGE);

                this.add(cp);
            }
        }
    }
}
