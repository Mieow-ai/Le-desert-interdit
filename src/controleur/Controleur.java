package controleur;

import modele.Equipement;
import modele.Joueur;
import modele.Plateau;
import modele.Zone;
import vue.FenetreJeu;
import vue.Observer;
import vue.VueCommande;

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


/**
 * La classe controleur fait le lien entre le modèle et la vue.
 * Elle gère toutes les interactions de l'utilisateur et applique
 * les règles du jeu en fonction des actions qu'on fait etc,...
 * Elle implémente aussi Observer pour "listen" les fin de partie déclenchée par le modèle
 */
public class Controleur implements Observer {
    private final Plateau p;
    private FenetreJeu vue;
    private List<Joueur> joueurs;
    private List<Joueur.Role> rolesDisponibles;
    private boolean partieTerminee = false;
    private Joueur allieADeplacer = null;
    private Equipement objet_en_cours = null;

    /**
     * Les différentes actions qu'un joueur peut efféctué
     */
    private enum action {
        AUCUNE, DEPLACEMENT, CREUSER, DEPLACEMENT_ALLIE, UTILISER_BLASTER
    };

    /**
     * Méthode de l'interface Observer
     * Appelée automatiquement à chaque fois que le plateau notifie un changement
     */
    @Override
    public void update() {
        gerer_fin_de_partie();
    }

    private action action_courante;

    /**
     * Constructeur du contrôleur. Initialise le jeu et attache tous les "listener"
     * sur les boutons et les cases du plateau.
     * @param modele Le Plateau de jeu
     * @param vue La fenêtre principale
     */
    public Controleur(Plateau modele, FenetreJeu vue) {
        this.p = modele;
        this.vue = vue;
        this.joueurs = modele.getJoueurs();
        this.action_courante = action.AUCUNE;
        this.rolesDisponibles = new ArrayList<>(Arrays.asList(Joueur.Role.values()));
        Collections.shuffle(this.rolesDisponibles);

        // Détection du GameOver
        p.addObserver(this);

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

        // Bouton ramasser
        this.vue.getVc().getBouton_ramasser().addActionListener(e -> {
            Joueur actif = this.joueurs.get(p.getIndiceJoueurActif());
            if (actif.getNb_action_restante() > 0) {
                Zone actif_zone = actif.getZone();
                if (actif_zone.get_objet_pose() != null) {
                    Zone.TypeZone objet = actif_zone.get_objet_pose();
                    actif_zone.poser_objet(null);
                    System.out.println("L'équipe vient de ramasser : " + objet);
                    actif.joue_action();
                    p.add_piece(Plateau.PIECE.CRISTAL);
                    vue.getVc().reset_texte_erreur();
                    p.notifyObservers();
                } else {
                    vue.getVc().set_texte_erreur("Vous n'êtes sur aucun objet !");
                }
            } else {
                vue.getVc().set_texte_erreur("Action impossible : plus d'actions...");
            }
        });

        // Bouton Amortir la Tempête (Météorologue)
        this.vue.getVc().getBoutonAmortirTempete().addActionListener(e -> {
            Joueur actif = this.joueurs.get(p.getIndiceJoueurActif());
            if(actif.getNb_action_restante() >0){
                if (actif.peut_amortir_tempete()) {
                    if(actif.getReductionTempete() < (int) p.getNiveauTempete()) {
                        actif.amortirTempete();
                        vue.getVc().reset_texte_erreur();
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

        // bouton donner de l'eau (Porteuse d'eau)
        this.vue.getVc().getBoutonDonnerEau().addActionListener(e -> {
            Joueur actif = this.joueurs.get(p.getIndiceJoueurActif());
            List<Joueur> cibles_possibles = new ArrayList<>();

            if (actif.getNvEau() <= 0) {
                vue.getVc().set_texte_erreur("Vous n'avez pas d'eau à partager !");
                return;
            }

            for(Joueur j : joueurs){
                if (j != actif && actif.peut_donner_eau(j) && j.getNvEau() < j.get_niveau_eau_max()){
                    cibles_possibles.add(j);
                }
            }
            if(cibles_possibles.isEmpty()){
                vue.getVc().set_texte_erreur("Peut donner de l'eau à personne");
                return;
            }
            Joueur cible = (Joueur) JOptionPane.showInputDialog(
                    vue, "À qui voulez-vous donner de l'eau ?", "Donner l'eau",
                    JOptionPane.QUESTION_MESSAGE, null, cibles_possibles.toArray(), cibles_possibles.get(0));
            if(cible != null){
                if(actif.donner_eau(cible)){
                    vue.getVc().reset_texte_erreur();
                    System.out.println(actif.getNom() + "donne de l'eau à" + cible.getNom());
                    p.notifyObservers();
                }
            }
        });


        // bouton deplacer un allié (Navigatrice)
        vue.getVc().getBoutonDeplacerAllie().addActionListener(e -> {
            Joueur actif = this.joueurs.get(p.getIndiceJoueurActif());

            if(actif.getNb_action_restante() > 0) {
                if (actif.getRole() == Joueur.Role.NAVIGATRICE){
                    List<Joueur> cibles = actif.get_joueurs_deplacables();

                    Joueur cible = (Joueur) JOptionPane.showInputDialog(
                            vue, "Quel allié voulez-vous déplacer ?", "Déplaceer allié",
                            JOptionPane.QUESTION_MESSAGE, null,
                            cibles.toArray(), cibles.get(0));

                    if (cible != null) {
                        allieADeplacer = cible;
                        action_courante = action.DEPLACEMENT_ALLIE;
                        p.setZonesAccessibles(cible.getZonesAccessibles(p));
                        vue.getVc().set_texte_erreur("Cliquer sur la case où vous  voulez déplacer" + cible.getNom());
                    }
                }
                else {
                    vue.getVc().set_texte_erreur("Seule la Navigatrice peut faire cette action");
                }
            } else {
                vue.getVc().set_texte_erreur("Plus d'actions restantes");
            }
        });

        // Bouton Equipement
        vue.getVc().getBouton_utiliser_equipement().addActionListener(e -> {
            Joueur actif = this.joueurs.get(p.getIndiceJoueurActif());

            if(actif.get_equipement().isEmpty()){
                vue.getVc().set_texte_erreur("Vous n'avez aucun équipement sur vous");
                return;
            }
            Equipement choix = (Equipement) JOptionPane.showInputDialog(
                    vue, "Quel objet voulez-vous utiliser ?", "Sac à dos",
                    JOptionPane.QUESTION_MESSAGE, null,
                    actif.get_equipement().toArray(),
                    actif.get_equipement().get(0));
            if(choix != null){
                actif.get_equipement().remove(choix);
                vue.getVc().reset_texte_erreur();
                System.out.println(actif.getNom() + "utilise : " + choix);

                switch (choix) {
                    case RESERVE_D_EAU:
                        for(Joueur j : actif.getZone().getJoueurs()){
                            j.boire_eau(2);
                        }
                        System.out.println("Tout le monde sur la case boit 2 d'eau");
                        break;
                    case BOUCLIER_SOLAIRE:
                        break;
                    case BLASTER_DES_DUNES:
                        List<Zone> cibles = new ArrayList<>();
                        if(actif.getZone().getNbSable() > 0){
                            cibles.add(actif.getZone());
                        }
                        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
                        for(int[] dir : directions){
                            int ni = actif.getZone().getX() + dir[0];
                            int nj = actif.getZone().getY() + dir[1];
                            if(ni >= 0 && ni < 5 && nj >= 0 && nj < 5){
                                Zone voisine = p.getZone(ni, nj);
                                if(voisine != null && voisine.getNbSable() > 0){
                                    cibles.add(voisine);
                                }
                            }
                        }

                        if(cibles.isEmpty()){
                            vue.getVc().set_texte_erreur("Aucune case ne peut être blasterisée");
                            actif.get_equipement().add(choix);
                        }
                        else {
                            action_courante = action.UTILISER_BLASTER;
                            objet_en_cours = choix;
                            p.setZonesAccessibles(cibles);
                            vue.getVc().set_texte_erreur("Cliquez sur la case à blasterisée");
                        }
                        break;
                    case JETPACK:
                        break;
                        // pas encore implémenté
                    case TERRASCOPE:
                        break;
                        // pas encore implémenté
                    case ACCELERATEUR_DE_TEMPS:
                        break;
                        // pas encore implémenté
                }
                p.notifyObservers();
            }
        });


        // Action réalisée par le click sur une case
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                final int fi = i, fj = j;
                vue.getVp().getCase(i, j).addMouseListener(new MouseAdapter() {
                    public void mouseClicked(MouseEvent e) {
                        Zone zone = p.getZone(fi, fj);
                        Joueur j = joueurs.get(p.getIndiceJoueurActif());
                        // Exécute l'action correspondante à l'état du contrôleur
                        switch (action_courante) {
                            case DEPLACEMENT_ALLIE:
                                if(zone != null && p.getZonesAccessibles().contains(zone) && allieADeplacer != null){
                                    allieADeplacer.setZone(zone);
                                    joueurs.get(p.getIndiceJoueurActif()).joue_action();
                                    System.out.println(allieADeplacer.getNom() + "a été déplacé par la navigatrice");
                                    p.verifier_victoire();
                                    gerer_fin_de_partie();
                                }
                                break;
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
                                    //if (zone.getType().name().startsWith("INDICE")) {
                                     //   p.verifie_apparition_obj(zone);
                                  //  }
                                    p.notifyObservers();
                                }
                                break;
                            case UTILISER_BLASTER:
                                if(zone != null && p.getZonesAccessibles().contains(zone)){
                                    int sable_retire = zone.viderSable();
                                    p.enlever_sable(sable_retire);
                                    System.out.println("Boom ! Le Blaster a détruit " + sable_retire + "bloc(s) de sable");
                                    p.notifyObservers();
                                }
                                else {
                                    System.out.println("Tir annulé");
                                    if(objet_en_cours != null){
                                        j.get_equipement().add(objet_en_cours);
                                        p.notifyObservers();
                                    }
                                }
                                objet_en_cours = null;
                                break;
                            case AUCUNE:
                                break;
                        }
                        // Nettoyage de l'interface après le clic
                        p.resetZonesAccessibles();
                        action_courante = action.AUCUNE;
                        vue.getVc().reset_texte_erreur();
                    }
                });
            }
        }

        // Action des boutons de la page de lancement
        this.vue.getVl().getBouton_play().addActionListener(e -> {
            if (this.joueurs.size() < 2) {
                this.vue.getVl().set_texte_erreur("Il faut au minimum 2 joueurs pour lancer la partie");
            } else {
                this.vue.getVl().reset_texte_erreur();
                this.vue.afficherEcran(false);
                this.p.notifyObservers();
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

    /**
     * Vérifie l'état actuel de la partie.
     * Si la partie est perdue ou gagnée, verrouille l'état de défaite
     * et propose de recommencer la partie ou de quitter le jeu
     */
    private void gerer_fin_de_partie() {
        if (p.getEtat() != Plateau.EtatPartie.EN_COURS && !partieTerminee) {
            partieTerminee = true;
            String titre = (p.getEtat() == Plateau.EtatPartie.VICTOIRE) ? "Victoire !" : "Game Over";
            String message = p.getRaison_fin() + "\n\n Voulez-vous recommencer une partie ?";
            int choix = JOptionPane.showConfirmDialog(vue, message, titre, JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            if (choix == JOptionPane.YES_OPTION) {
                vue.dispose(); // détruit l'ancienne fenêtre
                // on réécrit le main ici
                Plateau nouveauPlateau = new Plateau();
                FenetreJeu nouvelleFenetre = new FenetreJeu(nouveauPlateau);
                Controleur nouveauControleur = new Controleur(nouveauPlateau, nouvelleFenetre);
                nouvelleFenetre.afficherEcran(true);
            } else {
                System.exit(0); // Quitte le jeu
            }
        }
    }
}
