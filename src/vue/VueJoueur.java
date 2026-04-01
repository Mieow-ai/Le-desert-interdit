package vue;

import javax.swing.*;
import java.awt.*;
import modele.Joueur;

public class VueJoueur extends JPanel implements Observer {
    // La VueJoueur observe le plateau plutôt que son joueur car les actions des
    // autres joueurs peuvent influencer les champs du joueur
    private Joueur j;
    private JLabel labelNom;
    private JLabel labelActions;
    private JLabel labelEau;

    public VueJoueur(Joueur joueur) {
        this.j = joueur;
        this.setBackground(Color.WHITE);

        javax.swing.border.Border ligne = BorderFactory.createLineBorder(joueur.getCouleur(), 3);
        javax.swing.border.Border padding = BorderFactory.createEmptyBorder(10, 15, 10, 15);
        this.setBorder(BorderFactory.createCompoundBorder(ligne, padding));

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // Initialisation des textes
        this.labelNom = new JLabel(j.getNom());
        this.labelActions = new JLabel("Actions restantes : " + j.getNb_action_restante());
        this.labelEau = new JLabel("Niveau d'eau : " + j.getNvEau());

        this.add(labelNom);
        this.add(labelActions);
        this.add(labelEau);
    }

    @Override
    public void update() {
        this.labelActions.setText("Actions restantes : " + j.getNb_action_restante());
        this.labelEau.setText("Niveau d'eau : " + j.getNvEau());
        this.revalidate();
        this.repaint();
    }
}
