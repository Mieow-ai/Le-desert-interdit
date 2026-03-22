package vue;

import javax.swing.*;
import java.awt.*;
import modele.Plateau;

public class FenetreJeu extends JFrame {
    private VuePlateau vp;
    private Plateau p;

    public FenetreJeu(Plateau plateau){
        this.p = plateau;
        this.setTitle("Le Désert Interdit");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout());

        this.vp = new VuePlateau(plateau);
        this.add(vp, BorderLayout.CENTER);

        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }
}
