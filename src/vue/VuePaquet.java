package vue;

import modele.CarteTempete;
import modele.Paquet;
import modele.PaquetTempete;
import modele.Plateau;

import javax.swing.*;
import java.awt.*;

public abstract class VuePaquet<T> extends JPanel implements Observer {
    private final Plateau plateau;
    protected final Paquet<T> paquet;
    protected T lastCarte = null;

    public VuePaquet(Plateau plateau, Paquet<T> paquet, int width , int height) {
        plateau.addObserver(this);
        this.plateau = plateau;
        this.paquet = paquet;
        Dimension size = new Dimension(width, height);
        this.setPreferredSize(size);
        this.setMinimumSize(size);
    }

    @Override
    public void update() {
        lastCarte = this.paquet.regarderDefausse();
    };
}

class VuePaquetTempete extends VuePaquet<CarteTempete> {
    private static Image dosCarte = new ImageIcon("dosCarte.png").getImage();
    private JLabel titreCarte;
    private JLabel description;

    public VuePaquetTempete(Plateau plateau, PaquetTempete paquet ) {
        super(plateau, paquet, 155,220);
        // Layout vertical pour les que informations soit écrites lignes à lignes
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBorder(
                // Bordure intérieur (padding)
                BorderFactory.createEmptyBorder(12, 18, 12, 18)
        );
        this.setOpaque(false); // Transparent
        this.titreCarte = new JLabel("");
        this.titreCarte.setFont(Theme.SOUS_TITRE);
        this.titreCarte.setForeground(Theme.TEXTE_FONCE);
        this.titreCarte.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.add(titreCarte);
        this.add(Box.createVerticalStrut(10));

        this.description = new JLabel("");
        this.description.setFont(Theme.TEXTE);
        this.description.setForeground(Theme.TEXTE_FONCE);
        this.description.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.add(description);
        this.add(Box.createVerticalStrut(10));

    }

    @Override
    public void update() {
        super.update();
        if(lastCarte != null) {
            switch (lastCarte.getType()) {
                case VENT:
                    this.titreCarte.setText("Le Vent Souffle");
                    this.description.setText("Tous les joueurs perdent 1 unité d'eau");
                    break;
                case CHALEUR:
                    this.titreCarte.setText("La Vague de Chaleur");
                    this.description.setText("Tous les joueurs perdent 1 unité d'eau");
                    break;
                case DECHAINE:
                    this.titreCarte.setText("La Tempête se Déchaine");
                    this.description.setText("Tous les joueurs perdent 1 unité d'eau");
                    break;
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
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }

}
