package vue;

import modele.*;

import javax.swing.*;
import java.awt.*;

public abstract class VuePaquet<T> extends JPanel implements Observer {
    private final Plateau plateau;
    protected final Paquet<T> paquet;
    protected T lastCarte = null;

    protected JLabel titreCarte;
    protected JLabel description;

    public VuePaquet(Plateau plateau, Paquet<T> paquet, int width, int height) {
        plateau.addObserver(this);
        this.plateau = plateau;
        this.paquet = paquet;
        Dimension size = new Dimension(width, height);
        this.setPreferredSize(size);
        this.setMinimumSize(size);

        // Layout vertical pour les que informations soit écrites lignes à lignes
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBorder(
                // Bordure intérieur (padding)
                BorderFactory.createEmptyBorder(60, 37, 12, 35));
        this.setOpaque(false); // Transparent
        this.titreCarte = new JLabel("");
        this.titreCarte.setFont(Theme.TITRE_CARTE);
        this.titreCarte.setForeground(Theme.TEXTE_FONCE);
        this.titreCarte.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.add(titreCarte);
        this.add(Box.createVerticalStrut(20));

        this.description = new JLabel("");
        this.description.setFont(Theme.TEXTE_CARTE);
        this.description.setForeground(Theme.TEXTE_FONCE);
        this.description.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.description.setHorizontalAlignment(SwingConstants.CENTER);
        this.add(description);
        this.add(Box.createVerticalStrut(10));
    }

    @Override
    public void update() {
        lastCarte = this.paquet.regarderDefausse();
    };
}

class VuePaquetTempete extends VuePaquet<CarteTempete> {
    private static Image dosCarte = new ImageIcon(FenetreJeu.PathImages + "dos_carte_tempete.png").getImage();
    private static Image faceCarte = new ImageIcon(FenetreJeu.PathImages + "face_carte_tempete.png").getImage();

    public VuePaquetTempete(Plateau plateau, PaquetTempete paquet) {
        super(plateau, paquet, 220, 240);
    }

    public void update() {
        super.update();
        if (lastCarte != null) {
            this.titreCarte.setText(lastCarte.toString());
            if (lastCarte.getType() == CarteTempete.Type.VENT) {
                this.description.setText("<html><center>Direction : " + lastCarte.getDirection() + "<br>Force : " + lastCarte.getForce() + "</center></html>");
            } else {
                this.description.setText("<html><center>" + lastCarte.getDescription() + "</center></html>");
            }
        }

        this.revalidate();
        this.repaint();

    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (lastCarte == null) { // Aucune carte piochée pour le moment
            if (dosCarte != null) {
                g.drawImage(dosCarte, 0, 0, getWidth(), getHeight(), this);
            }
        } else {
            if (faceCarte != null) {
                g.drawImage(faceCarte, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }

}


class VuePaquetEquipement extends VuePaquet<CarteEquipement> {
    private static Image dosCarte = new ImageIcon(FenetreJeu.PathImages + "dos_carte_equipement.png").getImage();
    private static Image faceCarte = new ImageIcon(FenetreJeu.PathImages + "face_carte_equipement.png").getImage();


    public VuePaquetEquipement(Plateau plateau, PaquetEquipement paquet) {
        super(plateau, paquet, 220, 240);
    }

    @Override
    public void update() {
        super.update();
        if(lastCarte != null) {
            this.titreCarte.setText(lastCarte.toString());
            this.description.setText("<html><center>" + lastCarte.getDescription() + "</center></html>");
        }

        this.revalidate();
        this.repaint();

    }

@Override
protected void paintComponent(Graphics g) {
    super.paintComponent(g);

    if (lastCarte == null) { // Aucune carte piochée pour le moment
        if (dosCarte != null) {
            g.drawImage(dosCarte, 0, 0, getWidth(), getHeight(), this);
        }
    } else {
        if (faceCarte != null) {
            g.drawImage(faceCarte, 0, 0, getWidth(), getHeight(), this);
        }
    }
}

}
