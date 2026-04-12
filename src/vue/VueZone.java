package vue;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.Border;

import modele.Joueur;
import modele.Plateau;
import modele.Zone;

/**
 * La classe VueZone est le composant graphique représentant une tuile
 * individuelle du plateau.
 * Elle superpose deux couches d'affichage :
 * 1. L'arrière-plan
 * 2. Les éléments de l'interface posés par-dessus
 */
public class VueZone extends JPanel {

    private Zone zone_actuelle;

    private static Image imageSable = new ImageIcon("desert.png").getImage();
    private static Image imageTempete = new ImageIcon("tempete.png").getImage();
    private static Image imageOasis = new ImageIcon("oasis.png").getImage();
    private static Image imageOasisRev = new ImageIcon("oasis_rev.png").getImage();
    private static Image imageCrash = new ImageIcon("crash.png").getImage();
    private static Image imageMirage = new ImageIcon("mirage.png").getImage();
    private static Image imageEngrenage = new ImageIcon("engrenage.png").getImage();
    private static Image imageDecollage = new ImageIcon("decollage.png").getImage();
    private static Image imageTunnel= new ImageIcon("tunnel.png").getImage();

    private static Image imageHorizontaleBoite = new ImageIcon("boiteHorizontale.png").getImage();
    private static Image imageVerticaleBoite = new ImageIcon("boiteVerticale.png").getImage();
    private static Image imageHorizontaleCristal = new ImageIcon("cristalHorizontale.png").getImage();
    private static Image imageVerticaleCristal = new ImageIcon("cristalVerticale.png").getImage();
    private static Image imageHorizontaleHelice = new ImageIcon("heliceHorizontale.png").getImage();
    private static Image imageVerticaleHelice = new ImageIcon("heliceVerticale.png").getImage();
    private static Image imageHorizontaleNavigation = new ImageIcon("navigationHorizontale.png").getImage();
    private static Image imageVerticaleNavigation = new ImageIcon("navigationVerticale.png").getImage();

    /**
     * Constructeur
     */
    public VueZone() {
        this.setPreferredSize(new Dimension(100, 100));

        this.setLayout(new GridBagLayout());
    }

    /**
     * Construit et positionne les composants textuels et les pions sur la tuile.
     * Appelée par VuePlateau à chaque mise à jour du Modèle.
     * @param z La zone du modèle associée à cette tuile
     * @param estAccessible Détermine si la tuile doit être mise en surbrillance
     */
    public void dessiner(Zone z, boolean estAccessible) {
        this.zone_actuelle = z;
        this.removeAll();
        VuePiece imgPiece = null;
        if (z == null) { // Oeil du cyclone
            this.setBackground(Color.BLUE);
            this.setBorder(null);
        } else { // Tuile normale
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.insets = new Insets(1, 0, 1, 0);
            gbc.weighty = 1;
            this.add(Box.createGlue(), gbc);
            gbc.weighty = 0;
            gbc.gridy = 1;
            if (z.get_objet_pose() != null) {
                switch (z.get_objet_pose()) {
                    case INDICE_BOITE:
                        imgPiece = new VuePiece(Plateau.PIECE.BOITE.name() + ".png", 35, 35);
                        break;
                    case INDICE_CRISTAL:
                        imgPiece= new VuePiece(Plateau.PIECE.CRISTAL.name() + ".png", 35, 35);
                        break;
                    case INDICE_HELICE:
                        imgPiece= new VuePiece(Plateau.PIECE.HELICE.name() + ".png", 35, 35);
                        break;
                    case INDICE_NAVIGATION:
                        imgPiece= new VuePiece(Plateau.PIECE.NAVIGATION.name() + ".png", 35, 35);
                        break;
                }
                this.add(imgPiece,gbc);
            }

            // Affichage des joueurs

            List<Joueur> joueurs = z.getJoueurs();
            JPanel panelJoueurs1 = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0)); // 3 pions max par ligne car 5 pions débordent
            JPanel panelJoueurs2 = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
            panelJoueurs1.setOpaque(false);
            panelJoueurs2.setOpaque(false);
            if (!joueurs.isEmpty()) {

                int count = 0;
                for (Joueur joueur : joueurs) {
                    JPanel pion = new JPanel();
                    pion.setPreferredSize(new Dimension(15, 15));
                    pion.setBackground(joueur.getCouleur());
                    pion.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
                    if (count < 3) {
                        panelJoueurs1.add(pion);
                    } else {
                        panelJoueurs2.add(pion);
                    }
                    count++;
                }

            }
            // Si pas de components, on ajoute du vide pour garder la structure visuelle identique
            if (panelJoueurs1.getComponents().length == 0) {
                panelJoueurs2.add(Box.createVerticalStrut(16));
            }
            if (panelJoueurs2.getComponents().length == 0) {
                panelJoueurs2.add(Box.createVerticalStrut(16));
            }
            gbc.gridy = 2; // On les place en dessous du texte
            this.add(panelJoueurs1, gbc);
            gbc.gridy = 3;
            this.add(panelJoueurs2, gbc);

            JLabel sableLabel = new JLabel(z.getNbSable() + " sable");
            sableLabel.setFont(Theme.TEXTE);
            sableLabel.setForeground(z.getNbSable() >= 2 ? Theme.ACCENT_DANGER : Theme.TEXTE_FONCE);
            if(zone_actuelle.estRevelee()){
                sableLabel.setForeground(Color.black); // Si la case est révelée on met du noir pour éviter que ce soit illisible

            }
            gbc.gridy = 4;
            this.add(sableLabel, gbc);
            Border borderAccessible;
            // Mise en valeur des zones accessibles
            if (estAccessible) {
                borderAccessible = BorderFactory.createLineBorder(Theme.BORDURE_ACTIVE, 2);
            } else {
                borderAccessible = BorderFactory.createLineBorder(new Color(0, 0, 0, 30));
            }
            this.setBorder(BorderFactory.createCompoundBorder(borderAccessible,
                    BorderFactory.createEmptyBorder(10, 10, 5, 10)));

        }

        this.revalidate();
        this.repaint();
    }

    /**
     * Gère le dessin de l'image de fond de la tuile.
     * Cette méthode est appelée avant l'affichage des composants Swing
     * @param g L'outil de dessin de Java
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (zone_actuelle == null) { // L'Oeil
            if (imageTempete != null) {
                g.drawImage(imageTempete, 0, 0, getWidth(), getHeight(), this);
            }
            return;
        } else {
            if (!zone_actuelle.estRevelee()) {
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
            } else {
                switch (zone_actuelle.getType()) {
                    case CRASH:
                        if (imageCrash != null) {
                            g.drawImage(imageCrash, 0, 0, getWidth(), getHeight(), this);
                        }
                        break;
                    case EAU:
                        if (imageOasisRev != null) {
                            g.drawImage(imageOasisRev, 0, 0, getWidth(), getHeight(), this);
                        }
                        break;
                    case MIRAGE:
                        if (imageMirage != null) {
                            g.drawImage(imageMirage, 0, 0, getWidth(), getHeight(), this);
                        }
                        break;
                    case ENGRENAGE:
                        if (imageEngrenage != null) {
                            g.drawImage(imageEngrenage, 0, 0, getWidth(), getHeight(), this);
                        }
                        break;
                    case DECOLLAGE:
                        if (imageDecollage != null) {
                            g.drawImage(imageDecollage, 0, 0, getWidth(), getHeight(), this);
                        }
                        break;
                    case TUNNEL:
                        if (imageTunnel != null) {
                            g.drawImage(imageTunnel, 0, 0, getWidth(), getHeight(), this);
                        }
                        break;

                    case INDICE_BOITE:
                        if (zone_actuelle.getAxe() == 'i') {
                            if (imageHorizontaleBoite != null) {
                                g.drawImage(imageHorizontaleBoite, 0, 0, getWidth(), getHeight(), this);
                            }
                        } else {
                            if (imageVerticaleBoite != null) {
                                g.drawImage(imageVerticaleBoite, 0, 0, getWidth(), getHeight(), this);
                            }
                        }
                        break;
                    case INDICE_HELICE:
                        if (zone_actuelle.getAxe() == 'i') {
                            if (imageHorizontaleHelice != null) {
                                g.drawImage(imageHorizontaleHelice, 0, 0, getWidth(), getHeight(), this);
                            }
                        } else {
                            if (imageVerticaleHelice != null) {
                                g.drawImage(imageVerticaleHelice, 0, 0, getWidth(), getHeight(), this);
                            }
                        }
                        break;
                    case INDICE_CRISTAL:
                        if (zone_actuelle.getAxe() == 'i') {
                            if (imageHorizontaleCristal != null) {
                                g.drawImage(imageHorizontaleCristal, 0, 0, getWidth(), getHeight(), this);
                            }
                        } else {
                            if (imageVerticaleCristal != null) {
                                g.drawImage(imageVerticaleCristal, 0, 0, getWidth(), getHeight(), this);
                            }
                        }
                        break;
                    case INDICE_NAVIGATION:
                        if (zone_actuelle.getAxe() == 'i') {
                            if (imageHorizontaleNavigation != null) {
                                g.drawImage(imageHorizontaleNavigation, 0, 0, getWidth(), getHeight(), this);
                            }
                        } else {
                            if (imageVerticaleNavigation != null) {
                                g.drawImage(imageVerticaleNavigation, 0, 0, getWidth(), getHeight(), this);
                            }
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
}