package controleur;

import modele.*;
import vue.FenetreJeu;
import vue.Observer;
import vue.Son;

import javax.swing.*;
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
    private action action_courante;
    private boolean partieTerminee = false;
    private Joueur allieADeplacer = null;
    private CarteEquipement objet_en_cours = null;

    /**
     * Les différentes actions qu'un joueur peut efféctué
     */
    private enum action {
        AUCUNE, DEPLACEMENT, CREUSER, DEPLACEMENT_ALLIE, UTILISER_BLASTER,
        UTILISER_JETPACK, UTILISER_TERRASCOPE
    };

    /**
     * Méthode de l'interface Observer
     * Appelée automatiquement à chaque fois que le plateau notifie un changement
     */
    @Override
    public void update() {
        gerer_fin_de_partie();
    }


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

        // Détection de la fin de partie
        p.addObserver(this);

        // Bouton fin de tour
        this.vue.getVc().getBouton_fin_tour().addActionListener(e -> {
            Son.jouer_Bruitage("bouton7.wav");
            vue.getVc().reset_texte_erreur();
            System.out.println("Le désert s'agite...");

            // Réinitialise les actions et surbrillances du joueur précédent
            action_courante = action.AUCUNE;
            p.resetZonesAccessibles();
            this.action_desert();
            p.finDeTour();
            gerer_fin_de_partie();
        });

        // Bouton se déplacer
        this.vue.getVc().getBouton_se_deplacer().addActionListener(e -> {
            Son.jouer_Bruitage("bouton6.wav");
            Joueur actif = this.joueurs.get(p.getIndiceJoueurActif());
            if (actif.getNb_action_restante() > 0) {
                vue.getVc().reset_texte_erreur();
                action_courante = action.DEPLACEMENT;
                if (!actif.getZonesAccessibles(p).isEmpty()) {
                    p.setZonesAccessibles(actif.getZonesAccessibles(p));
                } else {
                    vue.getVc().set_texte_erreur("Aucune tuile accessible");
                }
            } else {
                vue.getVc().set_texte_erreur("Action impossible : plus d'actions..");
            }
        });

        // Bouton creuser
        this.vue.getVc().getBouton_creuser().addActionListener(e -> {
            Son.jouer_Bruitage("bouton6.wav");
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
            Son.jouer_Bruitage("bouton6.wav");
            Joueur actif = this.joueurs.get(p.getIndiceJoueurActif());

            if (actif.getNb_action_restante() > 0) {
                Zone actif_zone = actif.getZone();
                Zone.TypeZone objet_sur_case = actif_zone.get_objet_pose();

                if (objet_sur_case != null) {
                    Plateau.PIECE piece_a_rajouter = getPiece(objet_sur_case);
                    if (piece_a_rajouter != null) {
                        p.add_piece(piece_a_rajouter);
                        actif_zone.poser_objet(null);
                        actif.joue_action();
                        System.out.println("L'équipe a ramassé : " + piece_a_rajouter);
                        vue.getVc().reset_texte_erreur();
                        p.verifier_victoire();
                        p.notifyObservers();
                    }
                } else {
                    vue.getVc().set_texte_erreur("Il n'y a aucune pièce à ramasser ici !");
                }
            } else {
                vue.getVc().set_texte_erreur("Action impossible : plus d'actions...");
            }
        });



        // Bouton donner de l'eau (général)
        this.vue.getVc().getBoutonDonnerEau().addActionListener(e -> {
            Son.jouer_Bruitage("bouton6.wav");
            Joueur actif = this.joueurs.get(p.getIndiceJoueurActif());
            List<Joueur> cibles_possibles = new ArrayList<>();

            if (actif.getNvEau() <= 1) {
                vue.getVc().set_texte_erreur("Vous n'avez pas d'eau à partager !");
                return;
            }

            for(Joueur j : joueurs){
                if (j != actif && actif.peut_donner_eau(j) ){
                    cibles_possibles.add(j);
                }
            }
            if(cibles_possibles.isEmpty()){
                vue.getVc().set_texte_erreur("Vous ne pouvez donner de l'eau à personne");
                return;
            }
            Joueur cible = (Joueur) JOptionPane.showInputDialog(
                    vue, "À qui voulez-vous donner de l'eau ?", "Donner l'eau",
                    JOptionPane.QUESTION_MESSAGE, null, cibles_possibles.toArray(), cibles_possibles.get(0));
            if(cible != null){
                if(actif.donner_eau(cible)){
                    vue.getVc().reset_texte_erreur();
                    System.out.println(actif.getNom() + " donne de l'eau à" + cible.getNom());
                    p.notifyObservers();
                }
            }
        });




        // Bouton Equipement
        vue.getVc().getBouton_utiliser_equipement().addActionListener(e -> {
            Son.jouer_Bruitage("bouton6.wav");
            Joueur actif = this.joueurs.get(p.getIndiceJoueurActif());

            if(actif.get_equipement().isEmpty()){
                vue.getVc().set_texte_erreur("Vous n'avez aucun équipement sur vous");
                return;
            }
            CarteEquipement choix = (CarteEquipement) JOptionPane.showInputDialog(
                    vue, "Quel objet voulez-vous utiliser ?", "Sac à dos",
                    JOptionPane.QUESTION_MESSAGE, null,
                    actif.get_equipement().toArray(),
                    actif.get_equipement().get(0));
            if(choix != null){
                actif.get_equipement().remove(choix);
                vue.getVc().reset_texte_erreur();
                System.out.println(actif.getNom() + "utilise : " + choix);

                switch (choix.getType()) {
                    case RESERVE_D_EAU:
                        for(Joueur j : actif.getZone().getJoueurs()){
                            j.boire_eau(2);
                            Son.jouer_Bruitage("boire.wav");
                        }
                        System.out.println("Tout le monde sur la case boit 2 d'eau");
                        break;
                    case BOUCLIER_SOLAIRE:
                        actif.getZone().set_protege_par_bouclier(true);
                        vue.getVc().set_texte_erreur("Bouclier déployé sur cette case !");
                        break;
                    case BLASTER_DES_DUNES:
                        List<Zone> cibles = actif.getZonesCreusables(p);
                        if(cibles.isEmpty()){
                            vue.getVc().set_texte_erreur("Aucune case ne peut être blasterisée");
                            actif.get_equipement().add(choix); // On remet l'equipement
                        }
                        else {
                            action_courante = action.UTILISER_BLASTER;
                            objet_en_cours = choix;
                            p.setZonesAccessibles(cibles);
                            vue.getVc().set_texte_erreur("Cliquez sur la case à blasteriser");
                        }
                        break;
                    case JETPACK:
                        action_courante = action.UTILISER_JETPACK;
                        objet_en_cours = choix;
                        p.setZonesAccessibles(p.get_toutes_zones());
                        vue.getVc().set_texte_erreur("Cliquez n'importe où pour voler !");
                        break;
                    case TERRASCOPE:
                        List<Zone> cachees = p.get_zones_cachees();
                        if(cachees.isEmpty()){
                            vue.getVc().set_texte_erreur("Toutes les zones sont déjà révélée !");
                            actif.get_equipement().add(choix);
                        }else{
                            action_courante = action.UTILISER_TERRASCOPE;
                            objet_en_cours = choix;
                            p.setZonesAccessibles(cachees);
                            vue.getVc().set_texte_erreur("Choisissez une zone à observer");
                        }
                        break;
                    case ACCELERATEUR_DE_TEMPS:
                        actif.ajouter_actions_bonus(2);
                        vue.getVc().set_texte_erreur("+2 actions pour " + actif.getNom());
                        break;
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
                                    Joueur allieAlpiniste = alpinistePouvoir(allieADeplacer,zone);
                                    if(allieAlpiniste != null){ // Si le joueur déplacé est un alpiniste et qu'il y a un allié sur sa case, il peut l'emmener
                                        allieAlpiniste.setZone(zone);
                                    }
                                    allieADeplacer.setZone(zone);
                                    j.joue_action();
                                    System.out.println(allieADeplacer.getNom() + "a été déplacé par la navigatrice");
                                    p.verifier_victoire();
                                    gerer_fin_de_partie();
                                }
                                break;
                            case DEPLACEMENT:
                                if (zone != null && p.getZonesAccessibles().contains(zone)) {
                                    Joueur allie = alpinistePouvoir(j,zone);
                                    if(allie != null){ // Si le joueur est un alpiniste et qu'il y a un allié sur sa case, il peut l'emmener
                                        allie.setZone(zone);
                                    }
                                    j.setZone(zone);
                                    j.joue_action();
                                    Son.jouer_Bruitage("deplacement.wav");
                                    p.verifier_victoire();
                                    gerer_fin_de_partie();
                                }
                                break;
                            case CREUSER:
                                if (zone != null && p.getZonesAccessibles().contains(zone)) {
                                    Son.jouer_Bruitage("creuser.wav");
                                    j.creuserZone(zone);
                                    joueurs.get(p.getIndiceJoueurActif()).joue_action();
                                    p.notifyObservers();
                                }
                                break;
                            case UTILISER_BLASTER:
                                if(zone != null && p.getZonesAccessibles().contains(zone)){
                                    Son.jouer_Bruitage("laser.wav");
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
                            case UTILISER_JETPACK:
                                Son.jouer_Bruitage("jetpack.wav");
                                if(zone != null && p.getZonesAccessibles().contains(zone)){
                                    Joueur compagnon = alpinistePouvoir(j, zone);
                                    if(compagnon != null){
                                        compagnon.setZone(zone);
                                    }
                                    j.setZone(zone);
                                    System.out.println(j.getNom() + "a atterri en Jetpack");
                                    p.verifier_victoire();
                                }
                                break;
                            case UTILISER_TERRASCOPE:
                                if(zone != null && p.getZonesAccessibles().contains(zone)){
                                    Son.jouer_Bruitage("revelation.wav");
                                    JOptionPane.showMessageDialog(vue,
                                            "l'analyse révèle : " + zone.getType(),
                                            "Terrascope", JOptionPane.INFORMATION_MESSAGE);
                                }
                                break;
                            case AUCUNE:
                                break;
                        }
                        // Nettoyage de l'interface après le clic
                        p.resetZonesAccessibles();
                        action_courante = action.AUCUNE;
                        vue.getVc().reset_texte_erreur();
                        p.notifyObservers();
                    }
                });
            }
        }

        // Action des boutons de la page de lancement
        this.vue.getVl().getBouton_play().addActionListener(e -> {
            Son.jouer_Bruitage("bouton4.wav");
            Son.jouer_musique_boucle("musique_jeu_desert_interdit.wav");
            if (this.joueurs.size() < 2) {
                this.vue.getVl().set_texte_erreur("Il faut au minimum 2 joueurs pour lancer la partie");
            } else {
                this.vue.getVl().reset_texte_erreur();
                this.vue.afficherEcran(false);
                // La vue crée les composants pour les joueurs (voir boucle dans le else)
                // Les boutons spéciaux sont créés à ce moment là
                // On peut donc les liers à l'action qu'ils executent
                for(Joueur j : this.joueurs) {
                    setActionSpeciale(j.getRole());
                }
                this.p.notifyObservers();
            }

        });

        this.vue.getVl().getBouton_ajouter_joueur().addActionListener(e -> {
            Son.jouer_Bruitage("bouton1.wav");
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

        // Permet de revéler toutes les cases (utile pour coder l'ui et débugger)
        InputMap inputMap = vue.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = vue.getRootPane().getActionMap();
        inputMap.put(KeyStroke.getKeyStroke("K"), "reveler_tout");
        inputMap.put(KeyStroke.getKeyStroke("k"), "reveler_tout");

        actionMap.put("reveler_tout", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (p.getEtat() == Plateau.EtatPartie.EN_COURS && !joueurs.isEmpty()) {
                    Son.jouer_Bruitage("bouton3.wav");
                    p.revelerToutLePlateau();
                }
            }
        });

    }

    private static Plateau.PIECE getPiece(Zone.TypeZone objet_sur_case) {
        Plateau.PIECE piece_a_rajouter = null;
        if (objet_sur_case == Zone.TypeZone.INDICE_BOITE) piece_a_rajouter = Plateau.PIECE.BOITE;
        else if (objet_sur_case == Zone.TypeZone.INDICE_HELICE) piece_a_rajouter = Plateau.PIECE.HELICE;
        else if (objet_sur_case == Zone.TypeZone.INDICE_CRISTAL) piece_a_rajouter = Plateau.PIECE.CRISTAL;
        else if (objet_sur_case == Zone.TypeZone.INDICE_NAVIGATION) piece_a_rajouter = Plateau.PIECE.NAVIGATION;
        return piece_a_rajouter;
    }

    // Fenêtre de dialogue pour que l'alpiniste puisse emmener un allié lors d'un déplacement.
    private Joueur alpinistePouvoir(Joueur j, Zone zone) {
        if(j.getRole() == Joueur.Role.ALPINISTE) {
            List<Joueur> joueursZoneCourante = j.getZone().getJoueurs();
            joueursZoneCourante.remove(j);
            if (!joueursZoneCourante.isEmpty()) {
                Joueur cible = (Joueur) JOptionPane.showInputDialog(
                        vue, "Alpiniste, voulez vous emmener un allié ?", "Emmener Allié",
                        JOptionPane.QUESTION_MESSAGE, null,
                        joueursZoneCourante.toArray(), joueursZoneCourante.get(0));
                return cible;
            }
        }
        return null;
    }

    /**
     * Implémente les actions effectuées par les boutons spéciaux présents dans les vues Joueurs
     * Comme les vues Joueurs sont crées au lancement de la partie, il faut attendre le click sur jouer pour pouvoir
     * accéder aux boutons
     * @param roleAttribue
     */
    private void setActionSpeciale(Joueur.Role roleAttribue) {
        switch (roleAttribue){
            case NAVIGATRICE:
                // Bouton deplacer un allié
                this.vue.getBoutonsSpeciaux().get(Joueur.Role.NAVIGATRICE).addActionListener(e -> {
                    Son.jouer_Bruitage("bouton3.wav");
                    Joueur actif = this.joueurs.get(p.getIndiceJoueurActif());

                    if(actif.getNb_action_restante() > 0) {
                        if (actif.getRole() == Joueur.Role.NAVIGATRICE){
                            List<Joueur> cibles = actif.get_joueurs_deplacables();

                            Joueur cible = (Joueur) JOptionPane.showInputDialog(
                                    vue, "Quel allié voulez-vous déplacer ?", "Déplacer allié",
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
                break;

            case METEOROLOGUE:
                // Bouton Amortir la Tempête (Météorologue)
                this.vue.getBoutonsSpeciaux().get(Joueur.Role.METEOROLOGUE).addActionListener(e -> {
                    Son.jouer_Bruitage("bouton3.wav");
                    Joueur actif = this.joueurs.get(p.getIndiceJoueurActif());
                    if(actif.getRole()== Joueur.Role.METEOROLOGUE) {
                        if (actif.getNb_action_restante() > 0) {
                            if (actif.getReductionTempete() < (int) p.getNiveauTempete()) {
                                actif.amortirTempete();
                                vue.getVc().reset_texte_erreur();
                                p.notifyObservers();
                            } else {
                                vue.getVc().set_texte_erreur("Vous ne pouvez pas amortir plus la tempête");
                            }
                        } else {
                            vue.getVc().set_texte_erreur("Vous n'avez plus d'actions...");
                        }
                    } else {
                        vue.getVc().set_texte_erreur("Ce n'est pas au tour du météorologue");
                    }

                });
                break;
            case PORTEUSE_EAU:
                this.vue.getBoutonsSpeciaux().get(Joueur.Role.PORTEUSE_EAU).addActionListener(e -> {
                    Son.jouer_Bruitage("bouton3.wav");
                    Joueur porteuse = null;
                    for(Joueur j : joueurs){
                        if(j.getRole() == Joueur.Role.PORTEUSE_EAU){
                            porteuse=j;
                            break;
                        }
                    }
                    if(porteuse == null){
                        throw new IllegalStateException("La porteuse n'a pas été trouvé (bouton don eau porteuse)");
                    }
                    List<Joueur> cibles_possibles = new ArrayList<>();

                    if (porteuse.getNvEau() <= 1) {
                        vue.getVc().set_texte_erreur("La porteuse d'eau n'a pas d'eau à partager !");
                        return;
                    }

                    for(Joueur j : joueurs){
                        if (j != porteuse && porteuse.peut_donner_eau(j)){
                            cibles_possibles.add(j);
                        }
                    }
                    if(cibles_possibles.isEmpty()){
                        vue.getVc().set_texte_erreur("La porteuse ne peut donner de l'eau à personne");
                        return;
                    }
                    Joueur cible = (Joueur) JOptionPane.showInputDialog(
                            vue, "À qui voulez-vous donner de l'eau ?", "Donner l'eau",
                            JOptionPane.QUESTION_MESSAGE, null, cibles_possibles.toArray(), cibles_possibles.get(0));
                    if(cible != null){
                        if(porteuse.donner_eau(cible)){
                            Son.jouer_Bruitage("boire.wav");
                            vue.getVc().reset_texte_erreur();
                            System.out.println(porteuse.getNom() + "donne de l'eau à" + cible.getNom());
                            p.notifyObservers();
                        }
                    }
                });
                break;
            default: {};
            break;
        }
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
    /**
     * Gère la phase "Pioche" à la fin d'un tour.
     * Utilise un Timer Swing pour simuler le tirage successif des cartes avec un délai visuel,
     * permettant à l'interface de s'animer progressivement.
     */
    public void action_desert() {
        int nbCartes = (int) p.getNiveauTempete();
        // Vérifier si le météorologue a amorti la tempête
        for (Joueur j : joueurs) {
            nbCartes -= j.getAndResetReductionTempete();
        }
        final int nbcartestimer = nbCartes;
        Timer timer = new javax.swing.Timer(1500, null); // On crée un timer qui sera déclenché toutes les 1000ms après le départ du timer
        timer.addActionListener(new ActionListener() { // classe anonyme sur l'interface ActionListener
            int i = 0;

            @Override
            public void actionPerformed(ActionEvent e) { // action effectuée à chaque déclenchement du timer
                if (p.getEtat() != Plateau.EtatPartie.EN_COURS) {
                    ((javax.swing.Timer) e.getSource()).stop();
                    return;
                }
                if (i >= nbcartestimer) {
                    ((javax.swing.Timer) e.getSource()).stop(); // Le bon nombre de cartes à été tiré
                    return;
                }
                p.action_desert();
                i++;
            }
        });
        timer.start();
    }
}
