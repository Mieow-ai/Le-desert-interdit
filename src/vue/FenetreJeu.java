package vue;

import javax.swing.*;
import java.awt.*;
import modele.Plateau;

public class FenetreJeu extends JFrame {
    private VuePlateau vp;
    private Plateau p;
    private VueCommande vc;

    public FenetreJeu(Plateau plateau){
        this.p = plateau;
        this.setTitle("Le Désert Interdit");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout());

        this.vp = new VuePlateau(plateau);
        this.vc = new VueCommande();
        this.add(vp, BorderLayout.CENTER);
        this.add(vc, BorderLayout.SOUTH);

        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        
    }

    public VuePlateau getVp(){
        return this.vp;
    }

    public VueCommande getVc(){
        return this.vc;
    }

}
