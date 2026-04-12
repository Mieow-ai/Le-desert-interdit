import controleur.Controleur;
import modele.Joueur;
import modele.Plateau;
import vue.FenetreJeu;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        // Permet au vrai style de s'afficher (sinon Mac OS n'applique pas les effets désirés comme la couleur, largeur des boutons etc)
        // Pris sur https://docs.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html#available
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception _){}

        Plateau p = new Plateau();
        System.out.println("--- État initial du désert ---");
        System.out.println(p.toString());
        FenetreJeu f = new FenetreJeu(p);
        Controleur controleur = new Controleur(p, f);
        f.setVisible(true);
        f.afficherEcran(true);
    }
}
