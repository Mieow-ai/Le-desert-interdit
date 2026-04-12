package vue;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * La classe VueLancement c'est l'écran d'accueil du jeu.
 * C'est la première interface que l'utilisateur voit. Elle permet de saisir
 * le nom des joueurs, de les ajouter à la partie, et de lancer le jeu
 * une fois le nombre minimum de participants atteint.
 */
public class VueLancement extends JPanel {
    private JButton Bouton_ajouter_joueur;
    private JButton Bouton_play;
    private JLabel labelMessage;
    private JTextField champNom;

    /**
     * Constructeur de l'écran de lancement.
     * Met en place un layout centré contenant un panneau vertical
     * pour aligner proprement le titre, le champ de saisie et les boutons.
     */
    public VueLancement() {
        // 1. Fond et Layout global
        this.setBackground(Theme.FOND_PANNEAU);
        this.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // --- PANNEAU CENTRAL (Contient tous les éléments) ---
        JPanel panel_central = new JPanel();
        panel_central.setOpaque(false);
        panel_central.setLayout(new BoxLayout(panel_central, BoxLayout.Y_AXIS));
        panel_central.setBorder(new EmptyBorder(50, 50, 50, 50));

        // 2. TITRE DU JEU
        JLabel titre = new JLabel("LE DÉSERT INTERDIT");
        titre.setFont(new Font("Serif", Font.BOLD, 48));
        titre.setForeground(Theme.COULEUR_OR);
        titre.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel_central.add(titre);

        panel_central.add(Box.createVerticalStrut(40));

        // 3. ZONE DE SAISIE DU NOM
        JLabel labelNom = new JLabel("NOM DE L'AVENTURIER :");
        labelNom.setFont(Theme.SOUS_TITRE);
        labelNom.setForeground(Theme.TEXTE_CLAIR);
        labelNom.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel_central.add(labelNom);

        panel_central.add(Box.createVerticalStrut(10));

        this.champNom = new JTextField(15);
        this.champNom.setMaximumSize(new Dimension(300, 40));
        this.champNom.setFont(Theme.TITRE);
        this.champNom.setBackground(Theme.SABLE_CLAIR);
        this.champNom.setForeground(Theme.TEXTE_FONCE);
        this.champNom.setCaretColor(Theme.TEXTE_FONCE);
        this.champNom.setHorizontalAlignment(JTextField.CENTER);
        panel_central.add(this.champNom);

        panel_central.add(Box.createVerticalStrut(30));

        JPanel panel_boutons = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        panel_boutons.setOpaque(false);

        this.Bouton_ajouter_joueur = new JButton("REJOINDRE L'EXPÉDITION");
        styliser_bouton(this.Bouton_ajouter_joueur, Theme.SABLE_FONCE);

        this.Bouton_play = new JButton("LANCER LA PARTIE");
        styliser_bouton(this.Bouton_play, new Color(39, 174, 96));

        panel_boutons.add(this.Bouton_ajouter_joueur);
        panel_boutons.add(this.Bouton_play);
        panel_central.add(panel_boutons);

        panel_central.add(Box.createVerticalStrut(20));

        this.labelMessage = new JLabel(" ", SwingConstants.CENTER);
        this.labelMessage.setFont(Theme.TEXTE);
        this.labelMessage.setForeground(Theme.COULEUR_OR);
        this.labelMessage.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel_central.add(this.labelMessage);
        this.add(panel_central, gbc);
    }

    // Getters

    public JButton getBouton_ajouter_joueur() {
        return this.Bouton_ajouter_joueur;
    }
    public JButton getBouton_play() {
        return this.Bouton_play;
    }
    public String getNomSaisi() {
        return this.champNom.getText();
    }

    // Setters

    /** Vide le champ de texte une fois que le joueur a été ajouté avec succès. */
    public void resetNomSaisi() {
        this.champNom.setText("");
    }

    /**
     * Affiche un message d'information ou d'erreur sur l'écran d'accueil.
     * @param message Le texte à afficher
     */
    public void set_texte_erreur(String message) {
        this.labelMessage.setText(message);
    }

    /** Efface le message actuel de la vue. */
    public void reset_texte_erreur() {
        this.labelMessage.setText("");
    }

    /**
     * Applique un style spécifique aux gros boutons du menu principal,
     * @param b Le bouton à styliser
     * @param bg La couleur de fond désirée pour ce bouton
     */
    private void styliser_bouton(JButton b, Color bg) {
        b.setFont(Theme.SOUS_TITRE);
        b.setBackground(bg);
        b.setForeground(Theme.TEXTE_CLAIR);
        b.setFocusPainted(false);
        b.setPreferredSize(new Dimension(220, 50));
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.TEXTE_CLAIR, 1),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));

        // Effet au survol
        b.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                b.setBackground(bg.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                b.setBackground(bg);
            }
        });
    }
}