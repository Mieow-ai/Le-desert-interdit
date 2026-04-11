package vue;

import javax.swing.*;
import java.awt.*;
import modele.Joueur;
import modele.Plateau;

public class VueJoueur extends JPanel implements Observer {

    // La VueJoueur observe le plateau plutôt que son joueur car les actions du plateau et des
    // autres joueurs peuvent influencer la vue
    private final Joueur joueur;
    private final Plateau plateau;
    private JLabel labelNom;
    private JProgressBar barreEau;
    private JButton btnDonnerEau;
    private JProgressBar barreActions;

    private JLabel labelEquipements;

    // Spécifique au météorologue
    private JLabel labelAmortissement;
    private JButton btnAmortir;


    public VueJoueur(Joueur joueur, Plateau p) {
        this.joueur = joueur;
        p.addObserver(this); // Comme ça les blocs sont redessinés à chaque modification du plateau
        this.plateau = p;
        this.setBackground(Theme.FOND_PANNEAU);
        // Layout vertical pour les que informations soit écrites lignes à lignes
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        // Border de la couleur du joueur
        this.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(joueur.getCouleur(), 3),
                // Bordure intérieur (padding)
                BorderFactory.createEmptyBorder(12, 18, 12, 18)
        ));

        // Nom
        this.labelNom = new JLabel(this.joueur.getNom());
        this.labelNom.setFont(Theme.SOUS_TITRE);
        this.labelNom.setForeground(joueur.getCouleur());
        this.labelNom.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.add(labelNom);
        this.add(Box.createVerticalStrut(10));

        // Ligne eau (progressbar + bouton don)
        JPanel ligneEau = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        ligneEau.setOpaque(false);

        this.barreEau = new JProgressBar(0, this.joueur.get_niveau_eau_max());
        this.barreEau.setPreferredSize(new Dimension(80, 30));
        this.barreEau.setStringPainted(true);
        this.barreEau.setForeground(Theme.EAU_BLEU);
        this.barreEau.setBackground(Color.GRAY);
        btnDonnerEau = new JButton("Donner");
        btnDonnerEau.setFont(Theme.TEXTE);
        btnDonnerEau.setMargin(new Insets(1, 4, 1, 4));


        ligneEau.add(new JLabel("💧"));
        ligneEau.add(barreEau);
        ligneEau.add(btnDonnerEau);

        this.add(ligneEau);
        this.add(Box.createVerticalStrut(5));

        // Ligne actions
        JPanel ligneActions = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        ligneActions.setOpaque(false);

        this.barreActions = new JProgressBar(0, 4);
        this.barreActions.setPreferredSize(new Dimension(80, 30));
        this.barreActions.setStringPainted(true);
        this.barreActions.setForeground(Theme.ENERGIE);
        this.barreActions.setBackground(Color.GRAY);

        ligneActions.add(new JLabel("⚡️"));
        ligneActions.add(barreActions);
        this.add(ligneActions);
        this.add(Box.createVerticalStrut(5));



        // Amortissement météorologue
        if(this.joueur.peut_amortir_tempete()){
            this.labelAmortissement = new JLabel("🌪️ Amortissement : 0");
            this.labelAmortissement.setFont(Theme.TEXTE);
            this.labelAmortissement.setForeground(Theme.TEXTE_CLAIR);
            this.add(labelAmortissement);
        }

        this.labelEquipements = new JLabel("🎒 Objets : Aucun");
        this.labelEquipements.setFont(Theme.TEXTE);
        this.labelEquipements.setForeground(Theme.ACCENT_OR);
        this.add(this.labelEquipements);

        this.update();

    }

    @Override
    public void update() {
        barreEau.setValue(joueur.getNvEau());
        barreEau.setString(joueur.getNvEau() + "/" + joueur.get_niveau_eau_max());

        barreActions.setValue(joueur.getNb_action_restante());
        barreActions.setString(joueur.getNb_action_restante() + "/4");

        if(joueur.peut_amortir_tempete()) {
            this.labelAmortissement.setText("🌪️ " + joueur.getReductionTempete());
        }
        if(joueur.get_equipement().isEmpty()){
            this.labelEquipements.setText("🎒 Objets : Aucun");
        }
        else {
            String liste = joueur.get_equipement().toString();
            this.labelEquipements.setText("🎒 " + liste.substring(1, liste.length() - 1));
        }

        if (plateau.getJoueurActif() == this.joueur) {
            this.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Theme.BORDURE_ACTIVE, 4),
                    BorderFactory.createEmptyBorder(12, 18, 12, 18)
            ));
        } else {
            this.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(joueur.getCouleur(), 3),
                    // Bordure intérieur (padding)
                    BorderFactory.createEmptyBorder(12, 18, 12, 18)
            ));
        }



        this.revalidate();
        this.repaint();
    }

    public JButton getBoutonDonnerEau() { return btnDonnerEau; }
    public JButton getBoutonAmortir() { return btnAmortir; }
    public Joueur getJoueur() { return joueur; }
}
