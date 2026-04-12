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

    // Bouton spécifique au rôle
    private JButton boutonPouvoir;


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
        this.barreEau.setPreferredSize(new Dimension(120, 20));
        this.barreEau.setStringPainted(true);
        this.barreEau.setForeground(Theme.EAU_BLEU);
        this.barreEau.setBackground(Color.GRAY);



        ligneEau.add(new JLabel("💧"));
        ligneEau.add(barreEau);

        this.add(ligneEau);
        this.add(Box.createVerticalStrut(5));

        // Ligne actions
        JPanel ligneActions = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        ligneActions.setOpaque(false);

        this.barreActions = new JProgressBar(0, 4);
        this.barreActions.setPreferredSize(new Dimension(120, 20));
        this.barreActions.setStringPainted(true);
        this.barreActions.setForeground(Theme.ENERGIE);
        this.barreActions.setBackground(Color.GRAY);

        ligneActions.add(new JLabel("⚡️"));
        ligneActions.add(barreActions);
        this.add(ligneActions);
        this.add(Box.createVerticalStrut(5));





        switch (joueur.getRole()){
            case METEOROLOGUE:
                boutonPouvoir = new JButton("Amortir Tempête");
                this.labelAmortissement = new JLabel("🌪️ Amortissement : 0");
                this.labelAmortissement.setFont(Theme.TEXTE);
                this.labelAmortissement.setForeground(Theme.TEXTE_CLAIR);

                break;
            case NAVIGATRICE:
                boutonPouvoir = new JButton("Déplacer Allié");
                break;
            case PORTEUSE_EAU:
                boutonPouvoir = new JButton("Donner eau");
                break;
            default: boutonPouvoir = null;
        }
        if (boutonPouvoir != null) {
            JPanel lignePouvoir = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
            lignePouvoir.setOpaque(false);
            lignePouvoir.add(boutonPouvoir);
            if(joueur.getRole() == Joueur.Role.METEOROLOGUE){lignePouvoir.add(labelAmortissement);}
            this.add(lignePouvoir);
            this.add(Box.createVerticalStrut(5));

        }

        this.labelEquipements = new JLabel("🎒 Objets : Aucun");
        this.labelEquipements.setFont(Theme.TEXTE);
        this.labelEquipements.setForeground(Theme.ACCENT_OR);
        JPanel ligneEquip = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        ligneEquip.setOpaque(false);
        ligneEquip.add(this.labelEquipements);

        this.add(ligneEquip);

        this.update();

    }
    public JButton getBoutonPouvoir() {
        return boutonPouvoir;
    }

    @Override
    public void update() {
        barreEau.setValue(joueur.getNvEau());
        barreEau.setString(joueur.getNvEau() + "/" + joueur.get_niveau_eau_max());

        barreActions.setValue(joueur.getNb_action_restante());
        barreActions.setString(joueur.getNb_action_restante() + "/4");

        if(joueur.getRole() == Joueur.Role.METEOROLOGUE) {
            this.labelAmortissement.setText("🌪️ " + joueur.getReductionTempete());
        }
        if(joueur.get_equipement().isEmpty()){
            this.labelEquipements.setText("🎒 Objets : Aucun");
        }
        else {
            String liste = joueur.get_equipement().toString();
            this.labelEquipements.setText("🎒 " + liste.substring(1, liste.length() - 1));
        }
        this.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(joueur.getCouleur(), 3),
                // Bordure intérieur (padding)
                BorderFactory.createEmptyBorder(12, 18, 12, 18)
        ));
        if (plateau.getJoueurActif() == this.joueur) {
            this.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Theme.BORDURE_ACTIVE, 10),
                    BorderFactory.createEmptyBorder(12, 18, 12, 18)
            ));
        }

        this.revalidate();
        this.repaint();
    }

    public Joueur getJoueur() { return joueur; }
}
