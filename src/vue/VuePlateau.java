package vue;

import javax.swing.*;
import java.awt.*;
import java.util.List;

import modele.Zone;
import modele.Plateau;
import modele.Joueur;

public class VuePlateau extends JPanel implements Observer {
    private Plateau plateau;
    private VueZone[][] casesGrille = new VueZone[5][5];

    private Image imageSable;
    private Image imageTempete;

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

    public JPanel getCase(int i, int j) {
        return (JPanel) casesGrille[i][j];
    }

    public void update() {
        mettreAJour();
        if (plateau.getEtat() != Plateau.EtatPartie.EN_COURS) {
            JOptionPane.showMessageDialog(this,
                    plateau.getRaison_fin(),
                    plateau.getEtat() == Plateau.EtatPartie.VICTOIRE ? "Victoire !" : "Game Over",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
