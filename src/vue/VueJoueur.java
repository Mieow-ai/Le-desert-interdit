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
        this.setBackground(Theme.FOND_PANNEAU);
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(joueur.getCouleur(), 3),
                BorderFactory.createEmptyBorder(12, 18, 12, 18)
        ));
        this.labelNom = new JLabel(j.getNom());
        labelNom.setFont(Theme.SOUS_TITRE);
        labelNom.setForeground(joueur.getCouleur());
        this.labelActions = new JLabel("⚡ Actions : " + j.getNb_action_restante());
        labelActions.setFont(Theme.TEXTE);
        labelActions.setForeground(Theme.TEXTE_CLAIR);
        this.labelEau = new JLabel("💧 Eau : " + j.getNvEau() + "/4");
        labelEau.setFont(Theme.TEXTE);
        labelEau.setForeground(new Color(100, 180, 255));
        this.add(labelNom);
        this.add(Box.createVerticalStrut(5));
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
