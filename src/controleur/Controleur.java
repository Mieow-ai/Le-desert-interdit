package controleur;

import modele.Joueur;
import modele.Plateau;
import modele.Zone;
import vue.FenetreJeu;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class Controleur {
    private final Plateau p;
    private final FenetreJeu vue;
    private List<Joueur> joueurs;
    private int indiceJoueurActif = 0;

    private enum action {
        AUCUNE, DEPLACEMENT, CREUSER
    };

    private action action_courante;

    public Controleur(Plateau modele, FenetreJeu vue) {
        this.p = modele;
        this.vue = vue;
        this.joueurs = modele.getJoueurs();
        this.action_courante = action.AUCUNE;
        // Bouton fin de tour
        this.vue.getVc().getBouton_fin_tour().addActionListener(e -> {
            vue.getVc().reset_texte_erreur();
            System.out.println("Le désert s'agite...");
            this.indiceJoueurActif = (this.indiceJoueurActif + 1) % joueurs.size();
            this.joueurs.get(indiceJoueurActif).reset_action(); // nouveau joueur
            this.p.action_desert();
        });

        // Bouton se déplacer
        this.vue.getVc().getBouton_se_deplacer().addActionListener(e -> {
            Joueur actif = this.joueurs.get(this.indiceJoueurActif);
            if (actif.getNb_action_restante() > 0) {
                vue.getVc().reset_texte_erreur();
                action_courante = action.DEPLACEMENT;
                p.setZonesAccessibles(actif.getZonesAccessibles(p));
            } else {
                vue.getVc().set_texte_erreur("Action impossible : plus d'actions..");
            }
        });

        this.vue.getVc().getBouton_creuser().addActionListener(e -> {
            Joueur actif = this.joueurs.get(this.indiceJoueurActif);
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

        // On défini l'action réalisé par le click sur une case
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                final int fi = i, fj = j;
                vue.getVp().getCase(i, j).addMouseListener(new MouseAdapter() {
                    public void mouseClicked(MouseEvent e) {
                        Zone zone = p.getZone(fi, fj);
                        switch (action_courante) {
                            case DEPLACEMENT:
                                if (zone != null && p.getZonesAccessibles().contains(zone)) {
                                    joueurs.get(indiceJoueurActif).setZone(zone);
                                    joueurs.get(indiceJoueurActif).joue_action();
                                }
                                break;
                            case CREUSER:
                                if (zone != null && p.getZonesAccessibles().contains(zone)) {
                                    zone.creuser();
                                    joueurs.get(indiceJoueurActif).joue_action();
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
    }
}
