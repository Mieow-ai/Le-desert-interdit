package vue;

import javax.swing.*;
import java.awt.*;
import java.util.List;

import modele.Zone;
import modele.Plateau;
import modele.Joueur;

/**
 * La classe VuePlateau représente la grille principale du jeu (5x5).
 * C'est un conteneur qui gère l'affichage et la disposition
 * des 25 tuiles individuelles qui composent le désert.
 * Elle observe le Plateau pour savoir quelles cases doivent être mises
 * en surbrillance (accessibles) au cours du tour d'un joueur.
 */
public class VuePlateau extends JPanel implements Observer {
    private Plateau plateau;
    private VueZone[][] casesGrille = new VueZone[5][5];

    private Image imageSable;
    private Image imageTempete;

    /**
     * Initialise la grille visuelle du jeu.
     * @param plateau Le modèle (Plateau) à observer et à représenter.
     */
    public VuePlateau(Plateau plateau) {
        this.plateau = plateau;
        this.setLayout(new GridLayout(5, 5, 5, 5));
        this.setBackground(Color.GRAY);

        this.imageSable = new ImageIcon("desert.png").getImage();
        this.imageTempete = new ImageIcon("tempete.png").getImage();

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                casesGrille[i][j] = new VueZone();
                this.add(casesGrille[i][j]);
            }
        }

        mettreAJour();
        this.plateau.addObserver(this);
    }

    /**
     * Parcourt l'ensemble des 25 tuiles de la grille et demande à chacune
     * de se redessiner en fonction de son état actuel dans le Modèle.
     * Gère également la mise en surbrillance des cases accessibles par le joueur courant.
     */
    private void mettreAJour() {
        List<Zone> accessibles = plateau.getZonesAccessibles();

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                Zone zone = plateau.getZone(i, j);
                boolean accessible = accessibles.contains(zone);
                casesGrille[i][j].dessiner(zone, accessible);
            }
        }
        this.revalidate();
        this.repaint();
    }

    // Getters et méthodes

    /**
     * Permet d'accéder au composant graphique d'une case spécifique.
     * @param i L'indice de la ligne
     * @param j L'indice de la colonne
     * @return Le JPanel correspondant à ces coordonnées
     */
    public JPanel getCase(int i, int j) {
        return (JPanel) casesGrille[i][j];
    }

    /**
     * Méthode déclenchée automatiquement par le Plateau (Observable) lorsqu'un élément
     * du jeu a changé
     */
    public void update() {
        mettreAJour();
    }
}
