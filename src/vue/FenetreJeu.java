package vue;

import javax.swing.*;
import java.awt.*;
import modele.Plateau;

public class FenetreJeu extends JFrame {
    private VuePlateau vp;
    private Plateau p;
    private VueCommande vc;
    private VueLancement vueLancement;
    private boolean estSurMenu;

    public FenetreJeu(Plateau plateau) {
        this.p = plateau;
        this.setTitle("Le Désert Interdit");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout());

        this.vp = new VuePlateau(plateau);
        this.vc = new VueCommande();
        this.vueLancement = new VueLancement();

    }

    public void afficherEcran(boolean afficherMenu) {
        this.estSurMenu = afficherMenu;
        this.getContentPane().removeAll();
        if (estSurMenu) {
            this.add(vueLancement); // On met le menu
        } else {
            this.add(vp, BorderLayout.CENTER);
            this.add(vc, BorderLayout.SOUTH);

            JPanel panneauGauche = new JPanel(new GridLayout(3, 1, 10, 10));
            panneauGauche.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            JPanel panneauDroite = new JPanel(new GridLayout(3, 1, 10, 10));
            panneauDroite.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            java.util.List<modele.Joueur> Joueurs = p.getJoueurs();

            // On boucle sur tous les joueurs créés dans le menu pour créer leur bloc
            for (int i = 0; i < Joueurs.size(); i++) {
                VueJoueur blocJoueur = new VueJoueur(Joueurs.get(i));
                p.addObserver(blocJoueur); // Comme ça les blocs sont redessinés à chaque modification du plateau


                if (i %2 == 0) { // Un joueur sur 2 de chaque coté
                    panneauGauche.add(blocJoueur);
                } else {
                    panneauDroite.add(blocJoueur);
                }
            }

            this.add(panneauGauche, BorderLayout.WEST);
            this.add(panneauDroite, BorderLayout.EAST);
        }
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);

        this.revalidate();
        this.repaint();
    }

    public VuePlateau getVp() {
        return this.vp;
    }

    public VueCommande getVc() {
        return this.vc;
    }

    public VueLancement getVl() {
        return this.vueLancement;
    }
}
