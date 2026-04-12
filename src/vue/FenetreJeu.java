package vue;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import modele.Joueur;
import modele.Plateau;

/**
 * La FenêtreJeu c'est la fenêtre principale de l'application
 * C'est la fenêtre principale de la Vue dans le MVC.
 * Elle assemble les différentes composantes de la Vue.
 */
public class FenetreJeu extends JFrame {
    private VuePlateau vp;
    private Plateau p;
    private VueCommande vc;
    private VueLancement vueLancement;
    private VueInfoGlobales vIG;
    private boolean estSurMenu;
    private List<VueJoueur> VuesJoueurs = new ArrayList<>();
    private VuePaquetTempete vuePaquetTempete;

    /**
     * Constructeur
     * @param plateau Le modèle Plateau nécessaire pour lier les vues aux données.
     */
    public FenetreJeu(Plateau plateau) {
        this.p = plateau;
        this.setTitle("Le Désert Interdit");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout());

        this.vp = new VuePlateau(plateau);
        this.vIG = new VueInfoGlobales(plateau);
        this.vc = new VueCommande();
        this.vueLancement = new VueLancement();

    }

    /**
     * Alterne l'affichage entre le Menu de lancement et la Zone de jeu.
     * @param afficherMenu Si vrai, affiche VueLancement. Si faux, assemble la scène de jeu.
     */
    public void afficherEcran(boolean afficherMenu) {
        this.estSurMenu = afficherMenu;
        this.getContentPane().removeAll();
        if (estSurMenu) {
            this.add(vueLancement); // On met le menu
        } else {
            JPanel zoneJeu = new JPanel(new BorderLayout());

            zoneJeu.add(vIG, BorderLayout.NORTH);
            zoneJeu.add(vp, BorderLayout.CENTER);
            zoneJeu.add(vc, BorderLayout.SOUTH);
            JPanel panneauJoueursGauche = new JPanel(new GridLayout(3, 1, 10, 10));
            panneauJoueursGauche.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            JPanel panneauJoueursDroite = new JPanel(new GridLayout(3, 1, 10, 10));
            panneauJoueursDroite.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            java.util.List<modele.Joueur> Joueurs = p.getJoueurs();

            // On boucle sur tous les joueurs créés dans le menu pour créer leur bloc
            for (int i = 0; i < Joueurs.size(); i++) {
                VueJoueur blocJoueur = new VueJoueur(Joueurs.get(i), p);
                VuesJoueurs.add(blocJoueur);
                boutonsSpeciaux.put(Joueurs.get(i).getRole(), blocJoueur.getBoutonPouvoir());
                if (i %2 == 0) { // Un joueur sur 2 de chaque coté
                    panneauJoueursGauche.add(blocJoueur);
                } else {
                    panneauJoueursDroite.add(blocJoueur);
                }
            }
            panneauJoueursGauche.setBackground(Theme.FOND_PANNEAU_CLAIR);
            panneauJoueursDroite.setBackground(Theme.FOND_PANNEAU_CLAIR);

            zoneJeu.add(panneauJoueursGauche, BorderLayout.WEST);
            zoneJeu.add(panneauJoueursDroite, BorderLayout.EAST);

            this.vuePaquetTempete = new VuePaquetTempete(p,p.getPaquetTempete());

            JPanel zonePaquets = new JPanel(new GridLayout(2, 1, 20, 20));
            zonePaquets.setBackground(Theme.FOND_PANNEAU);
            zonePaquets.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            zonePaquets.add(vuePaquetTempete);

            this.add(zonePaquets,BorderLayout.WEST);
            this.add(zoneJeu, BorderLayout.CENTER);
            this.setBackground(Theme.FOND_PANNEAU);

        }
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);

        this.revalidate();
        this.repaint();
    }

    // Getters

    public VuePlateau getVp() {
        return this.vp;
    }
    public VueCommande getVc() {
        return this.vc;
    }
    public VueLancement getVl() {
        return this.vueLancement;
    }
    public VuePaquetTempete getVuePaquetTempete(){return this.vuePaquetTempete;}
    public Map<Joueur.Role, JButton> getBoutonsSpeciaux() {
        return boutonsSpeciaux;
    }

    private Map<Joueur.Role, JButton> boutonsSpeciaux = new HashMap<>();
    public List<VueJoueur> getVuesJoueurs() {
        return VuesJoueurs;
    }

}
