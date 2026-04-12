package vue;
import javax.swing.*;
import java.awt.*;

/**
 * La classe Theme centralise l'ensemble de la charte graphique du jeu
 */
public class Theme {
    public static final Color SABLE_CLAIR   = new Color(237, 201, 141);
    public static final Color SABLE_FONCE   = new Color(177, 141, 54);
    public static final Color FOND_PLATEAU  = new Color(60, 45, 30);
    public static final Color FOND_PANNEAU  = new Color(45, 35, 25);
    public static final Color FOND_PANNEAU_CLAIR  = new Color(237, 201, 141);
    public static final Color TEXTE_CLAIR   = new Color(255, 240, 210);
    public static final Color TEXTE_FONCE   = new Color(80, 60, 30);
    public static final Color ACCENT_OR     = new Color(218, 165, 32);
    public static final Color ACCENT_DANGER = new Color(180, 50, 50);
    public static final Color ENERGIE = new Color(255, 255, 0, 255);

    public static final Font TITRE    = new Font("SansSerif", Font.BOLD, 18);
    public static final Font SOUS_TITRE = new Font("SansSerif", Font.BOLD, 14);
    public static final Font TEXTE    = new Font("SansSerif", Font.PLAIN, 12);
    public static final Color EAU_BLEU = new Color(30, 144, 255);
    public static final Color ACTION_ORANGE = new Color(255, 140, 0);
    public static final Color SELECTION = new Color(46, 204, 113, 150);
    public static final Color BORDURE_ACTIVE = new Color(255, 215, 0);
    public static final Font TITRE_CARTE = new Font("Monospaced", Font.BOLD, 16);
    public static final Font TEXTE_CARTE = new Font("Monospaced", Font.ITALIC, 10);


    /**
     * Applique le style standard du jeu à un bouton
     * @param b Le JButton à styliser.
     */
    public static void styliser_bouton(JButton b) {
        b.setBackground(Theme.SABLE_FONCE);
        b.setForeground(Theme.TEXTE_CLAIR);
        b.setFocusPainted(false);

        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createRaisedBevelBorder(),
                BorderFactory.createEmptyBorder(5, 12, 5, 12)));
    }
}
