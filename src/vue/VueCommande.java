package vue;

import javax.swing.*;
import java.awt.*;

public class VueCommande extends JPanel {
    private JButton Bouton_fin_tour;
    private JButton Bouton_se_deplacer;
    private JButton Bouton_creuser;
    private JButton Bouton_ramasser;
    private JButton Bouton_donner_eau;
    private JButton Bouton_amortir_tempete;
    private JButton Bouton_deplacer_allie;
    private JLabel labelMessage;
    private JButton Bouton_utiliser_equipement;

    public VueCommande() {
        this.setLayout(new BorderLayout());
        this.setBackground(Color.LIGHT_GRAY);

        JPanel Boutons = new JPanel();
        Boutons.setBackground(Color.LIGHT_GRAY);

        // Boutons d'actions de base
        this.Bouton_fin_tour = new JButton("Fin de tour");
        this.Bouton_se_deplacer = new JButton("Se déplacer");
        this.Bouton_creuser = new JButton("Creuser");
        this.Bouton_ramasser = new JButton("Ramasser");
        this.Bouton_utiliser_equipement = new JButton("Équipement");

        // Boutons de pouvoirs spéciaux
        this.Bouton_donner_eau = new JButton("Donner eau");
        this.Bouton_amortir_tempete = new JButton("Amortir tempête");
        this.Bouton_deplacer_allie = new JButton("Déplacer allié");

        Boutons.add(this.Bouton_fin_tour);
        Boutons.add(this.Bouton_creuser);
        Boutons.add(this.Bouton_se_deplacer);
        Boutons.add(this.Bouton_ramasser);
        Boutons.add(this.Bouton_donner_eau);
        Boutons.add(this.Bouton_amortir_tempete);
        Boutons.add(this.Bouton_deplacer_allie);
        Boutons.add(this.Bouton_utiliser_equipement);

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

    public JButton getBoutonDonnerEau() { return this.Bouton_donner_eau; }

    public JButton getBoutonAmortirTempete() { return this.Bouton_amortir_tempete; }

    public JButton getBoutonDeplacerAllie() { return this.Bouton_deplacer_allie; }

    public JButton getBouton_utiliser_equipement() { return this.Bouton_utiliser_equipement; }

    public void set_texte_erreur(String message) {
        this.labelMessage.setText(message);
    }

    public void reset_texte_erreur() {
        this.labelMessage.setText(" ");
    }


}
