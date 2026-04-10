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
    private JProgressBar barreEau;
    private JProgressBar barreActions;

    public VueJoueur(Joueur joueur, Plateau p) {
        this.j = joueur;
        p.addObserver(this); // Comme ça les blocs sont redessinés à chaque modification du plateau
        this.setBackground(Theme.FOND_PANNEAU);
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(joueur.getCouleur(), 3),
                BorderFactory.createEmptyBorder(12, 18, 12, 18)
        ));
        this.labelNom = new JLabel(j.getNom());
        this.labelNom.setFont(Theme.SOUS_TITRE);
        this.labelNom.setForeground(joueur.getCouleur());
        this.labelNom.setAlignmentX(Component.CENTER_ALIGNMENT);

        this.barreEau = new JProgressBar(0, j.get_niveau_eau_max());
        this.barreEau.setStringPainted(true);
        this.barreEau.setForeground(Theme.EAU_BLEU);
        this.barreEau.setBackground(Color.BLACK);

        this.barreActions = new JProgressBar(0, 4);
        this.barreActions.setStringPainted(true);
        this.barreActions.setForeground(Theme.ACTION_ORANGE);
        this.barreActions.setBackground(Color.BLACK);

        this.labelEquipements = new JLabel("🎒 Objets : Aucun");
        this.labelEquipements.setFont(Theme.TEXTE);
        this.labelEquipements.setForeground(Theme.ACCENT_OR);
        this.add(this.labelEquipements);

        this.add(labelNom);
        this.add(Box.createVerticalStrut(10));
        this.add(new JLabel("💧 Eau"));
        this.add(barreEau);
        this.add(Box.createVerticalStrut(5));
        this.add(new JLabel("⚡ Actions"));
        this.add(barreActions);
        if(j.peut_amortir_tempete()){
            this.labelAmortissment = new JLabel("🌪️ Amortissement : 0");
            this.labelAmortissment.setFont(Theme.TEXTE);
            this.labelAmortissment.setForeground(Theme.TEXTE_CLAIR);
            this.add(labelAmortissment);
        }

    }

    @Override
    public void update() {
        this.barreEau.setValue(j.getNvEau());
        this.barreEau.setString(j.getNvEau() + " / " + j.get_niveau_eau_max());
        this.barreActions.setValue(j.getNb_action_restante());
        this.barreActions.setString(j.getNb_action_restante() + " / 4");
        if (j.get_plateau().getJoueurs().get(j.get_plateau().getIndiceJoueurActif()) == j) {
            this.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Theme.BORDURE_ACTIVE, 4),
                    BorderFactory.createEmptyBorder(10, 15, 10, 15)
            ));
        } else {
            this.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(j.getCouleur(), 1),
                    BorderFactory.createEmptyBorder(10, 15, 10, 15)
            ));
        }
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
