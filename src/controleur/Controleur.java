package controleur;

import modele.Joueur;
import modele.Plateau;
import modele.Zone;
import vue.FenetreJeu;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.Collections;

public class Controleur {
    private final Plateau p;
    private FenetreJeu vue;
    private List<Joueur> joueurs;
    private List<Joueur.Role> rolesDisponibles;

    private enum action {
        AUCUNE, DEPLACEMENT, CREUSER
    };

    private action action_courante;

    public Controleur(Plateau modele, FenetreJeu vue) {
        this.p = modele;
        this.vue = vue;
        this.joueurs = modele.getJoueurs();
        this.action_courante = action.AUCUNE;
        this.rolesDisponibles = new ArrayList<>(Arrays.asList(Joueur.Role.values()));
        Collections.shuffle(this.rolesDisponibles);

        // Bouton fin de tour
        this.vue.getVc().getBouton_fin_tour().addActionListener(e -> {
            vue.getVc().reset_texte_erreur();
            System.out.println("Le désert s'agite...");

            // Réinitialise les actions et surbrillances du joueur précédent
            action_courante = action.AUCUNE;
            p.resetZonesAccessibles();
            p.finDeTour();
            gerer_fin_de_partie();
        });

        // Bouton se déplacer
        this.vue.getVc().getBouton_se_deplacer().addActionListener(e -> {
            Joueur actif = this.joueurs.get(p.getIndiceJoueurActif());
            if (actif.getNb_action_restante() > 0) {
                vue.getVc().reset_texte_erreur();
                action_courante = action.DEPLACEMENT;
                p.setZonesAccessibles(actif.getZonesAccessibles(p));
            } else {
                vue.getVc().set_texte_erreur("Action impossible : plus d'actions..");
            }
        });

        // Bouton creuser
        this.vue.getVc().getBouton_creuser().addActionListener(e -> {
            Joueur actif = this.joueurs.get(p.getIndiceJoueurActif());
            if (actif.getNb_action_restante() > 0) {
                action_courante = action.CREUSER;
                List<Zone> zones = actif.getZonesCreusables(p);
                if (zones.isEmpty()) {
                    vue.getVc().set_texte_erreur("Aucune case creusable autour de vous.");
                    action_courante = action.AUCUNE;
                    p.resetZonesAccessibles();
                } else {
                    vue.getVc().reset_texte_erreur();
                    p.setZonesAccessibles(zones);
                }
            } else {
                vue.getVc().set_texte_erreur("Action impossible : plus d'actions..");
            }
        });

        this.vue.getVc().getBouton_ramasser().addActionListener(e -> {
            Joueur actif = this.joueurs.get(p.getIndiceJoueurActif());
            if (actif.getNb_action_restante() > 0) {
                Zone actif_zone = actif.getZone();
                if (actif_zone.get_objet_pose() != null) {
                    Zone.TypeZone objet = actif_zone.get_objet_pose();
                    actif_zone.poser_objet(null);
                    System.out.println("L'équipe vient de ramasser : " + objet);
                    actif.joue_action();
                    vue.getVc().reset_texte_erreur();
                    p.notifyObservers();
                } else {
                    vue.getVc().set_texte_erreur("Vous n'êtes sur aucun objet !");
                }
            } else {
                vue.getVc().set_texte_erreur("Action impossible : plus d'actions...");
            }
        });

        this.vue.getVc().getBoutonAmortirTempete().addActionListener(e -> {
            Joueur actif = this.joueurs.get(p.getIndiceJoueurActif());
            if(actif.getNb_action_restante() >0){
                if (actif.peut_amortir_tempete()) {
                    if(actif.getReductionTempete() < p.getNiveauTempete()) {
                        actif.amortirTempete();
                        p.notifyObservers();
                    } else {
                        vue.getVc().set_texte_erreur("Vous ne pouvez pas amortir plus la tempête");
                    }

                } else {
                        vue.getVc().set_texte_erreur("Vous ne pouvez pas amortir la tempête");
                }
            } else {
                vue.getVc().set_texte_erreur("Action impossible : plus d'actions...");
            }

        });

        // Action réalisé par le click sur une case
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                final int fi = i, fj = j;
                vue.getVp().getCase(i, j).addMouseListener(new MouseAdapter() {
                    public void mouseClicked(MouseEvent e) {
                        Zone zone = p.getZone(fi, fj);
                        Joueur j = joueurs.get(p.getIndiceJoueurActif());
                        switch (action_courante) {
                            case DEPLACEMENT:
                                if (zone != null && p.getZonesAccessibles().contains(zone)) {
                                    j.setZone(zone);
                                    j.joue_action();
                                    p.verifier_victoire();
                                    gerer_fin_de_partie();
                                }
                                break;
                            case CREUSER:
                                if (zone != null && p.getZonesAccessibles().contains(zone)) {
                                    j.creuserZone(zone);
                                    joueurs.get(p.getIndiceJoueurActif()).joue_action();
                                    if (zone.getType().name().startsWith("INDICE")) {
                                        p.verifie_apparition_obj(zone);
                                    }
                                    p.notifyObservers();
                                }
                                break;
                            case AUCUNE:
                                break;
                        }
                        p.resetZonesAccessibles();
                        action_courante = action.AUCUNE;
                    }
                });
            }
        }

        // Action des boutons de la page de lancement
        this.vue.getVl().getBouton_play().addActionListener(e -> {
            if (this.joueurs.size() < 2) {
                this.vue.getVl().set_texte_erreur("Il faut au minimum 2 joueurs pour lancer la partie");
            } else {
                this.p.notifyObservers();
                this.vue.getVl().reset_texte_erreur();
                this.vue.afficherEcran(false);
            }

        });

        this.vue.getVl().getBouton_ajouter_joueur().addActionListener(e -> {
            if (this.rolesDisponibles.isEmpty()) {
                this.vue.getVl().set_texte_erreur("Nombre maximum de joueurs atteint (6).");
                return;
            }

            String nomSaisi = this.vue.getVl().getNomSaisi().trim(); // On récupère le nom via le textfield
            if (nomSaisi.isEmpty()) {
                nomSaisi = "Joueur " + (this.joueurs.size() + 1); // Permet d'ajouter des joueurs sans nom
            }

            Joueur.Role roleAttribue = this.rolesDisponibles.remove(this.rolesDisponibles.size() - 1);
            nomSaisi += " " + roleAttribue;
            Joueur j = Joueur.creerJoueur(roleAttribue, nomSaisi, p); // Méthode statique

            j.setZone(p.getZoneCrash());
            this.joueurs = this.p.getJoueurs();

            this.vue.getVl().set_texte_erreur(nomSaisi + " est prêt ! (" + this.joueurs.size() + " total)");
            this.vue.getVl().resetNomSaisi();
            p.notifyObservers();
        });


    }

    private void gerer_fin_de_partie() {
        if (p.getEtat() != Plateau.EtatPartie.EN_COURS) {
            String titre = (p.getEtat() == Plateau.EtatPartie.VICTOIRE) ? "Victoire !" : "Game Over";
            String message = p.getRaison_fin() + "\n\n Voulez-vous recommencer une partie ?";
            int choix = JOptionPane.showConfirmDialog(vue, message, titre, JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
            if (choix == JOptionPane.YES_OPTION) {
                vue.dispose();

                // On relance le main
                Plateau nouveauPlateau = new Plateau();
                FenetreJeu nouvelleFenetre = new FenetreJeu(nouveauPlateau);
                Controleur nouveauControleur = new Controleur(nouveauPlateau, nouvelleFenetre);
                nouvelleFenetre.afficherEcran(true);
            } else {
                System.exit(0);
            }
        }
    }
}
