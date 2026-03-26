package vue;

import javax.swing.*;
import java.awt.*;
import java.util.List;

import modele.Zone;
import vue.Observer;
import modele.Plateau;
import modele.Joueur;

public class VuePlateau extends JPanel implements Observer {
    private Plateau plateau;
    private JPanel[][] casesGrille = new JPanel[5][5];

    public VuePlateau(Plateau plateau) {
        this.plateau = plateau;
        this.setLayout(new GridLayout(5, 5, 5, 5));
        this.setBackground(Color.GRAY);

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                casesGrille[i][j] = new JPanel();
                casesGrille[i][j].setPreferredSize(new Dimension(100, 100));
                casesGrille[i][j].setLayout(new GridBagLayout());
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
                JPanel cp = casesGrille[i][j];
                cp.removeAll();
                Zone zone = plateau.getZone(i, j);

                if (zone == null) {
                    cp.setBackground(Color.BLUE);
                } else {
                    // Sable
                    GridBagConstraints gbc = new GridBagConstraints();
                    gbc.gridy = 0;
                    JLabel sableLabel = new JLabel(zone.getNbSable() + " sable(s)");
                    sableLabel.setFont(new Font("Arial", Font.BOLD, 14));
                    sableLabel.setForeground(Color.BLACK);
                    cp.add(sableLabel, gbc);

                    // Pions joueurs
                    List<Joueur> joueurs = zone.getJoueurs();
                    if (!joueurs.isEmpty()) {
                        JPanel panelJoueurs = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 0));
                        panelJoueurs.setOpaque(false);
                        for (Joueur joueur : joueurs) {
                            JPanel pion = new JPanel();
                            pion.setPreferredSize(new Dimension(15, 15));
                            pion.setBackground(joueur.getCouleur());
                            panelJoueurs.add(pion);
                        }
                        gbc.gridy = 1;
                        cp.add(panelJoueurs, gbc);
                    }

                    // Accessible
                    if (accessibles.contains(zone)) {
                        cp.setBackground(new Color(144, 238, 144));
                        cp.setBorder(BorderFactory.createLineBorder(Color.GREEN, 3));
                        cp.setCursor(new Cursor(Cursor.HAND_CURSOR));
                    } else {
                        cp.setBackground(new Color(177, 141, 54));
                        cp.setBorder(null);
                        cp.setCursor(Cursor.getDefaultCursor());
                    }
                }

                cp.revalidate();
                cp.repaint();
            }
        }
    }

    public JPanel getCase(int i, int j) {
        return casesGrille[i][j];
    }

    public void update() {
        mettreAJour();
    }
}
