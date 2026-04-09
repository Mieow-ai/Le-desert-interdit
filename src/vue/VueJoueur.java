package vue;

import javax.swing.*;
import java.awt.*;
import modele.Joueur;
import modele.Plateau;

public class VueJoueur extends JPanel implements Observer {
    // La VueJoueur observe le plateau plutôt que son joueur car les actions des
    // autres joueurs peuvent influencer les champs du joueur
    private Joueur j;
    private JLabel labelNom;
    private JLabel labelActions;
    private JLabel labelEau;
    private JLabel labelAmortissment;
    private JLabel labelEquipements;

    public VueJoueur(Joueur joueur, Plateau p) {
        this.j = joueur;
        p.addObserver(this); // Comme ça les blocs sont redessinés à chaque modification du plateau
        this.setBackground(Theme.FOND_PANNEAU);
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(joueur.getCouleur(), 3),
                BorderFactory.createEmptyBorder(12, 18, 12, 18)
        ));
        labelNom = new JLabel(j.getNom());
        labelNom.setFont(Theme.SOUS_TITRE);
        this.labelNom.setForeground(joueur.getCouleur());
        this.labelActions = new JLabel("⚡ Actions restantes : " + j.getNb_action_restante());
        this.labelActions.setFont(Theme.TEXTE);
        this.labelActions.setForeground(Theme.TEXTE_CLAIR);
        this.labelEau = new JLabel("💧 Eau : " + j.getNvEau() + "/" + j.get_niveau_eau_max());
        this.labelEau.setFont(Theme.TEXTE);
        this.labelEau.setForeground(new Color(100, 180, 255));

        this.labelEquipements = new JLabel("🎒 Objets : Aucun");
        this.labelEquipements.setFont(Theme.TEXTE);
        this.labelEquipements.setForeground(Theme.ACCENT_OR);
        this.add(this.labelEquipements);

        this.add(labelNom);
        this.add(Box.createVerticalStrut(5));
        this.add(labelActions);
        this.add(labelEau);
        if(j.peut_amortir_tempete()){
            this.labelAmortissment = new JLabel("🌪️ Amortissement : 0");
            this.labelAmortissment.setFont(Theme.TEXTE);
            this.labelAmortissment.setForeground(Theme.TEXTE_CLAIR);
            this.add(labelAmortissment);
        }

    }

    @Override
    public void update() {
        this.labelActions.setText("⚡ Actions restantes : " + j.getNb_action_restante());
        this.labelEau.setText("💧 Eau : " + j.getNvEau() + "/" + j.get_niveau_eau_max());
        if(j.peut_amortir_tempete()) {
            this.labelAmortissment.setText("🌪️ Amortissement : " + j.getReductionTempete());
        }

        if(j.get_equipement().isEmpty()){
            this.labelEquipements.setText("🎒 Objets : Aucun");
        }
        else {
            String liste = j.get_equipement().toString();
            this.labelEquipements.setText("🎒 " + liste.substring(1, liste.length() - 1));
        }
        this.revalidate();
        this.repaint();
    }
}
