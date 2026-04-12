package vue;

import javax.swing.*;
import java.awt.*;

/**
 * VuePiece est un composant graphique personnalisé
 * Créer pour afficher une image redimensionnable.
 * On l'utilise pour afficher les icônes des pièces et des engrenages.
 */
public class VuePiece extends JPanel {
    private Image image;

    /**
     * Constructeur
     * @param path Le chemin d'accès vers le fichier image
     * @param width La largeur
     * @param height La hauteur
     */
    public VuePiece(String path, int width, int height) {
        this.image = new ImageIcon(path).getImage();
        this.setOpaque(false); // Transparent
        this.setPreferredSize(new Dimension(width, height));
    }

    /**
     * Modifie l'image affichée
     * @param path Le chemin vers la nouvelle image
     */
    public void setImage(String path) {
        this.image = new ImageIcon(path).getImage();
        this.repaint();
    }

    /**
     * Surcharge de la méthode standard de dessin de Swing.
     * @param g L'outil de dessin fourni par Java
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image != null) {
            g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
