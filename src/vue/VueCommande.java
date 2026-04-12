package vue;

import javax.swing.*;
import java.awt.*;

import static vue.Theme.styliser_bouton;

/**
 * La classe VueCommande représente le panneau de contrôle principal situé en bas de l'écran.
 * Elle contient tous les boutons permettant au joueur actif d'effectuer ses actions de base
 * (Se déplacer, Creuser, Ramasser, etc.) ainsi qu'une zone de texte pour afficher les messages d'erreur ou d'information.
 */
public class VueCommande extends JPanel {
    /** Bouton pour terminer son tour et déclencher la tempête. */
    private JButton Bouton_fin_tour;
    /** Bouton pour se déplacer vers une case adjacente ou via un tunnel. */
    private JButton Bouton_se_deplacer;
    /** Bouton pour retirer du sable ou retourner une tuile dégagée. */
    private JButton Bouton_creuser;
    /** Bouton pour récupérer une pièce de la machine volante. */
    private JButton Bouton_ramasser;
    /** Bouton pour partager son eau avec un joueur sur la même case. */
    private JButton Bouton_donner_eau;
    private JLabel labelMessage;
    /** Bouton pour ouvrir l'inventaire et utiliser une carte équipement. */
    private JButton Bouton_utiliser_equipement;

    /**
     * Constructeur de la VueCommande.
     * Initialise la disposition, crée les boutons, leur applique le style du Thème
     * et les organise dans le panneau.
     */
    public VueCommande() {
        this.setLayout(new BorderLayout());
        this.setBackground(Theme.FOND_PANNEAU);

        JPanel conteneurBoutons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        conteneurBoutons.setOpaque(false);

        styliser_bouton(Bouton_fin_tour = new JButton("Fin de Tour"));
        styliser_bouton(Bouton_se_deplacer = new JButton("Marcher"));
        styliser_bouton(Bouton_creuser = new JButton("Creuser/Révéler"));
        styliser_bouton(Bouton_ramasser = new JButton("Ramasser"));
        styliser_bouton(Bouton_utiliser_equipement = new JButton("Équipement"));
        styliser_bouton(Bouton_donner_eau = new JButton("Donner eau"));

        conteneurBoutons.add(this.Bouton_creuser);
        conteneurBoutons.add(this.Bouton_se_deplacer);
        conteneurBoutons.add(this.Bouton_ramasser);
        conteneurBoutons.add(this.Bouton_donner_eau);
        conteneurBoutons.add(this.Bouton_fin_tour);
        conteneurBoutons.add(this.Bouton_utiliser_equipement);

        this.add(conteneurBoutons, BorderLayout.NORTH);

        this.labelMessage = new JLabel(" ", SwingConstants.CENTER);
        this.labelMessage.setForeground(Color.RED);
        this.add(this.labelMessage, BorderLayout.SOUTH);
        this.setBorder(

                BorderFactory.createEmptyBorder(12, 18, 12, 18)
        );
    }

    // Getters

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
    public JButton getBouton_utiliser_equipement() { return this.Bouton_utiliser_equipement; }

    // Gestion des messages d'alerte

    public void set_texte_erreur(String message) {
        this.labelMessage.setText(message);
    }
    public void reset_texte_erreur() {
        this.labelMessage.setText(" ");
    }

}
