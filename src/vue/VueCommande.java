package vue;

import javax.swing.*;
import java.awt.*;

public class VueCommande extends JPanel {
    private JButton Bouton_fin_tour;
    private JButton Bouton_se_deplacer;
    private JButton Bouton_creuser;
    private JLabel labelMessage;
    private JButton Bouton_ramasser;

    public VueCommande() {
        this.setLayout(new BorderLayout());
        this.setBackground(Color.LIGHT_GRAY);
        JPanel Boutons = new JPanel();
        Boutons.setBackground(Color.LIGHT_GRAY);
        this.Bouton_fin_tour = new JButton("Fin de tour");
        this.Bouton_se_deplacer = new JButton("Se déplacer");
        this.Bouton_creuser = new JButton("Creuser");
        this.Bouton_ramasser = new JButton("Ramasser");
        Boutons.add(this.Bouton_fin_tour);
        Boutons.add(this.Bouton_creuser);
        Boutons.add(this.Bouton_se_deplacer);
        Boutons.add(this.Bouton_ramasser);
        this.add(Boutons, BorderLayout.NORTH);

        this.labelMessage = new JLabel(" ", SwingConstants.CENTER);
        this.labelMessage.setForeground(Color.RED);
        this.add(this.labelMessage, BorderLayout.SOUTH);
    }

    public JButton getBouton_fin_tour() {
        return this.Bouton_fin_tour;
    }

    public JButton getBouton_se_deplacer() {
        return this.Bouton_se_deplacer;
    }

    public JButton getBouton_creuser() {
        return this.Bouton_creuser;
    }

    public JButton getBouton_ramasser() { return this.Bouton_ramasser; }

    public void set_texte_erreur(String message) {
        this.labelMessage.setText(message);
    }

    public void reset_texte_erreur() {
        this.labelMessage.setText("");
    }

}
