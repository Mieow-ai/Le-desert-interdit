package vue;
import modele.Plateau;
import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;


public class VueInfoGlobales  extends JPanel implements Observer {

    private Plateau plateau;
    private JLabel labelTempete;
    private JLabel labelSable;
    private JLabel labelJoueurCourant;
    // Le dictionnaire asssocie directement la piece à son image
    private Map<Plateau.PIECE, VuePiece> dictPieceVue = new HashMap<>();

    public VueInfoGlobales(Plateau plateau) {
        this.plateau = plateau;
        plateau.addObserver(this);

        this.setBackground(Theme.FOND_PANNEAU);
        // Les composants sont centrés et sont espacés de 60px
        this.setLayout(new FlowLayout(FlowLayout.CENTER, 60, 10));

        labelTempete = new JLabel("️🌪 Niveau de la Tempête : ");
        labelTempete.setForeground(Theme.TEXTE_CLAIR);
        labelTempete.setFont(Theme.SOUS_TITRE);

        // On défini un layout imbriqué pour afficher au centre
        // Le nom du joueur courant et les pièces ramassées (grisées si non ramassées)

        JPanel layoutCentral = new JPanel();
        layoutCentral.setLayout(new BoxLayout(layoutCentral, BoxLayout.Y_AXIS));
        layoutCentral.setOpaque(false);

        labelJoueurCourant = new JLabel("Au tour de ");
        labelJoueurCourant.setForeground(Theme.TEXTE_CLAIR);
        labelJoueurCourant.setFont(Theme.TITRE);

        JPanel conteneurPieces = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        conteneurPieces.setOpaque(false);

        for (Plateau.PIECE piece : Plateau.PIECE.values()) {
            VuePiece imgPiece = new VuePiece(piece.name() + "GRISEE.png", 30, 30);
            // On lie la piece et sa vue
            dictPieceVue.put(piece, imgPiece);
            // On ajoute la vue dans le conteneur
            conteneurPieces.add(imgPiece);
        }


        layoutCentral.add(labelJoueurCourant);
        layoutCentral.add(Box.createVerticalStrut(5)); // Petit espace
        layoutCentral.add(conteneurPieces);

        labelSable = new JLabel("⏳ Tonnes de sable : ");
        labelSable.setForeground(Theme.TEXTE_CLAIR);
        labelSable.setFont(Theme.SOUS_TITRE);

        this.add(labelTempete);
        this.add(layoutCentral);
        this.add(labelSable);
        this.update();
    }
    @Override
    public void update() {
        labelTempete.setText("🌪️ Niveau de la Tempête : " + plateau.getNiveauTempete() + "/ 5");
        labelSable.setText("⏳ Tonnes de sable : " + plateau.getTotalSable() + " / 48");
        String nom = "";
        if(!this.plateau.getJoueurs().isEmpty()) {
            nom = this.plateau.getJoueurs().get(this.plateau.getIndiceJoueurActif()).getNom();
        }
        labelJoueurCourant.setText("Au tour de : " + nom);

        // Mise a jour des VuePiece
        Map<Plateau.PIECE, Boolean> etatPieces = plateau.getEtatPieces();
        for (Plateau.PIECE piece : Plateau.PIECE.values()) {
            if (etatPieces.get(piece) == true) {
                //Pièce trouvée (en couleur)
                dictPieceVue.get(piece).setImage(piece.name() + ".png");
            } else {
                // Piece non trouvée (grisée)
                dictPieceVue.get(piece).setImage(piece.name() + "GRISEE.png");
            }
        }


    }
}

class VuePiece extends JPanel {
    private Image image;

    public VuePiece(String path, int width, int height) {
        this.image = new ImageIcon(path).getImage();
        this.setOpaque(false); // Transparent !
        this.setPreferredSize(new Dimension(width, height));
    }
    public void setImage (String path) {
        this.image = new ImageIcon(path).getImage();
        this.repaint();
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image != null) {
            g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
        }
    }
}

