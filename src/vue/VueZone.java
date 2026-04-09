package vue;

import java.awt.*;
import java.util.List;
import javax.swing.*;

import modele.Joueur;
import modele.Zone;

public class VueZone extends JPanel {

    private Zone zone_actuelle;

    private static Image imageSable = new ImageIcon("desert.png").getImage();
    private static Image imageTempete = new ImageIcon("tempete.png").getImage();
    private static Image imageOasis = new ImageIcon("oasis.png").getImage();

    public VueZone() {
        this.setPreferredSize(new Dimension(100, 100));
        this.setLayout(new GridBagLayout());
    }

    public void dessiner(Zone z, boolean estAccessible) {
        this.zone_actuelle = z;
        this.removeAll();

        if (z == null) { // Oeil du cyclone
            this.setBackground(Color.BLUE);
            this.setBorder(null);
        } else { // Tuile normale
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.insets = new Insets(1, 0, 1, 0);
            if (!z.estRevelee()) {
                JLabel sableLabel = new JLabel(z.getNbSable() + " sable(s)");
                sableLabel.setFont(new Font("Arial", Font.BOLD, 14));
                sableLabel.setForeground(Color.BLACK);
                this.add(sableLabel, gbc);
            } else {
                String nomTuile = z.getType().name().replace("INDICE_", "");
                JLabel reveleeLabel = new JLabel(nomTuile);
                reveleeLabel.setFont(new Font("Arial", Font.BOLD, 14));
                reveleeLabel.setForeground(Color.BLACK);
                this.add(reveleeLabel, gbc);
            }

            if (z.get_objet_pose() != null) {
                String nomAbrege = z.get_objet_pose().name().replace("INDICE_", "");
                JLabel objet_label = new JLabel("🏆 " + nomAbrege);
                objet_label.setFont(new Font("Arial", Font.BOLD, 11));
                objet_label.setForeground(Color.ORANGE);
                gbc.gridy = 1;
                this.add(objet_label, gbc);
            }

            // Affichage des joueurs

            List<Joueur> joueurs = z.getJoueurs();
            if (!joueurs.isEmpty()) {
                JPanel panelJoueurs = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
                panelJoueurs.setOpaque(false);
                for (Joueur joueur : joueurs) {
                    JPanel pion = new JPanel();
                    pion.setPreferredSize(new Dimension(15, 15));
                    pion.setBackground(joueur.getCouleur());
                    pion.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
                    panelJoueurs.add(pion);
                }
                gbc.gridy = 2; // On les place en dessous du texte
                this.add(panelJoueurs, gbc);
            }

            // Mise en valeur des zones accessibles
            if (estAccessible) {
                this.setBackground(new Color(144, 238, 144));
                this.setBorder(BorderFactory.createLineBorder(Color.GREEN, 3));
                this.setCursor(new Cursor(Cursor.HAND_CURSOR));
            } else {
                this.setBackground(new Color(177, 141, 54));
                this.setBorder(null);
                this.setCursor(Cursor.getDefaultCursor());
            }
        }

        this.revalidate();
        this.repaint();
    }

    // Le moteur de dessin des images de fond
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (zone_actuelle == null) { // L'Oeil
            if (imageTempete != null) {
                g.drawImage(imageTempete, 0, 0, getWidth(), getHeight(), this);
            }
        } else {
            switch (zone_actuelle.getType()) {
                case EAU:
                case MIRAGE:
                    if (imageOasis != null) {
                        g.drawImage(imageOasis, 0, 0, getWidth(), getHeight(), this);
                    }
                    break;
                default:
                    if (imageSable != null) {
                        g.drawImage(imageSable, 0, 0, getWidth(), getHeight(), this);
                    }
                    break;
            }

        }
    }
}