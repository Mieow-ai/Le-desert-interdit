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
                final int fi = i;
                final int fj = j;
                casesGrille[i][j] = new JPanel() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        super.paintComponent(g);
                        Zone z = plateau.getZone(fi, fj);
                        if (z != null) {
                            if (imageSable != null) {
                                g.drawImage(imageSable, 0, 0, getWidth(), getHeight(), this);
                            }
                        } else {
                            g.drawImage(imageTempete, 0, 0, getWidth(), getHeight(), this);
                        }
                    }
                };

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
                    JLabel oeilLabel = new JLabel(" L'OEIL");
                    oeilLabel.setFont(new Font("Arial", Font.BOLD, 15));
                    oeilLabel.setForeground(Color.WHITE);
                    cp.add(oeilLabel);
                } else {
                    // --------- AFFICHAGE DU SABLE OU DU NOM DE LA TUILE ---------
                    GridBagConstraints gbc = new GridBagConstraints();
                    gbc.gridx = 0;
                    gbc.gridy = 0;
                    gbc.insets = new Insets(1, 0, 1, 0); // Léger espacement vertical

                    if (!zone.estRevelee()) {
                        JLabel sableLabel = new JLabel(zone.getNbSable() + " sable(s)");
                        sableLabel.setFont(new Font("Arial", Font.BOLD, 14));
                        sableLabel.setForeground(Color.BLACK);
                        cp.add(sableLabel, gbc);
                    } else {
                        String nomTuile = zone.getType().name().replace("INDICE_", "");
                        JLabel reveleeLabel = new JLabel(nomTuile);
                        reveleeLabel.setFont(new Font("Arial", Font.BOLD, 14));
                        reveleeLabel.setForeground(Color.BLACK);
                        cp.add(reveleeLabel, gbc);
                    }

                    // --------- AFFICHAGE DE LA PIECE A RAMASSER ---------
                    if (zone.get_objet_pose() != null) {
                        String nomAbrege = zone.get_objet_pose().name().replace("INDICE_", "");
                        JLabel objet_label = new JLabel("🏆 " + nomAbrege);
                        objet_label.setFont(new Font("Arial", Font.BOLD, 11));
                        objet_label.setForeground(Color.ORANGE);
                        gbc.gridy = 1;
                        cp.add(objet_label, gbc);
                    }

                    // --------- AFFICHAGE DES JOUEURS ---------
                    List<Joueur> joueurs = zone.getJoueurs();
                    if (!joueurs.isEmpty()) {
                        JPanel panelJoueurs = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
                        panelJoueurs.setOpaque(false);
                        for (Joueur joueur : joueurs) {
                            JPanel pion = new JPanel();
                            pion.setPreferredSize(new Dimension(15, 15));
                            pion.setBackground(joueur.getCouleur());
                            // Bordure noire pour rendre le joueur visible
                            pion.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
                            panelJoueurs.add(pion);
                        }
                        gbc.gridy = 2;
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
        this.revalidate();
        this.repaint();
    }

    public JPanel getCase(int i, int j) {
        return casesGrille[i][j];
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
