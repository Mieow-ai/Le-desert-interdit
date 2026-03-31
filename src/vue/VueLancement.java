package vue;

import javax.swing.*;
import java.awt.*;

public class VueLancement extends JPanel {
    private JButton Bouton_ajouter_joueur;
    private JButton Bouton_play;
    private JLabel labelMessage;

    public VueLancement() {
        this.setLayout(new BorderLayout());
        this.setBackground(Color.LIGHT_GRAY);
        JPanel Boutons = new JPanel();
        Boutons.setBackground(Color.LIGHT_GRAY);
        this.Bouton_ajouter_joueur = new JButton("Ajouter un joueur");
        this.Bouton_play = new JButton("Jouer");
        Boutons.add(this.Bouton_ajouter_joueur);
        Boutons.add(this.Bouton_play);
        this.add(Boutons, BorderLayout.NORTH);
        this.labelMessage = new JLabel(" ", SwingConstants.CENTER);
        this.labelMessage.setForeground(Color.RED);
        this.add(this.labelMessage, BorderLayout.SOUTH);
    }

    public JButton getBouton_ajouter_joueur() {return this.Bouton_ajouter_joueur;};
    public JButton getBouton_play() {return this.Bouton_play;}
    public void set_texte_erreur(String message) {
        this.labelMessage.setText(message);
    }

    public void reset_texte_erreur() {
        this.labelMessage.setText("");
    }

}