package vue;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import vue.Observer;
import modele.Plateau;
import modele.Joueur;

public class VuePlateau extends JPanel implements Observer {
    private Plateau plateau;

    public VuePlateau(Plateau plateau) {
        this.plateau = plateau;
        this.setLayout(new GridLayout(5, 5, 5, 5));
        this.setBackground(Color.GRAY);
        dessinerPlateau();
        this.plateau.addObserver(this);
    }

    public void dessinerPlateau() {
        this.removeAll();
        int oeilI = this.plateau.getOeilI();
        int oeilJ = this.plateau.getOeilJ();
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                JPanel cp = new JPanel();
                cp.setPreferredSize(new Dimension(100, 100));
                cp.setLayout(new GridBagLayout());

                if (i == oeilI && j == oeilJ) {
                    cp.setBackground(Color.BLUE);
                } else {
                    int quantite_sable = this.plateau.getZone(i, j).getNbSable();
                    JLabel sable_label = new JLabel(quantite_sable + "sable(s)");
                    sable_label.setFont(new Font("Arial", Font.BOLD, 14));
                    cp.setBackground(new Color(177, 141, 54));
                    sable_label.setForeground(Color.BLACK);
                    cp.add(sable_label);

                    // Ajout des joueurs

                    List<Joueur> joueurs = this.plateau.getZone(i, j).getJoueurs();
                    if (!joueurs.isEmpty()) {
                        JPanel panelJoueurs = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 0));
                        panelJoueurs.setOpaque(false);
                        for (Joueur joueur : joueurs) {
                            JPanel pion = new JPanel();
                            pion.setPreferredSize(new Dimension(15, 15));
                            pion.setBackground(joueur.getCouleur());
                            panelJoueurs.add(pion);
                        }
                        // Ajouter sous le label sable
                        GridBagConstraints gbc = new GridBagConstraints();
                        gbc.gridy = 1;
                        cp.add(panelJoueurs, gbc);
                    }
                }

                this.add(cp);
            }
        }
        this.revalidate();
        this.repaint();
    }

    public void update() {
        dessinerPlateau();
    }
}
