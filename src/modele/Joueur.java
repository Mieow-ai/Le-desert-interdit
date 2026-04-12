package modele;

import java.awt.*;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;


/**
 * Classe abstraite représentant un joueur dans le jeu
 * Contient toutes les mécaniques communes à tous les rôles/joueurs
 * ainsi que les méthodes dites "par défaut" qui seront override dans chaque classes rôle
 */
public abstract class Joueur {

    private final String nom;
    protected Zone zone;
    private final Plateau p;
    private final Color couleur;
    private final int action_par_tour = 4;
    private int nb_action_restante = action_par_tour;
    protected int niveauEau = 4;
    protected int niveauEauMax = 4;
    protected int reductionTempete = 0;
    private List<Equipement> equipements = new ArrayList<>();
    protected int[][] getDirections() {
        return new int[][] { { -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 } };
    }

    public enum Role {
        ARCHEOLOGUE,
        ALPINISTE,
        EXPLORATEUR,
        METEOROLOGUE,
        NAVIGATRICE,
        PORTEUSE_EAU
    }

    /**
     * Constructeur de base d'un Joueur.
     * @param nom Nom du joueur
     * @param couleur Couleur associée au pion du joueur
     * @param p Le plateau de jeu
     */
    public Joueur(String nom, Color couleur, Plateau p) {
        this.nom = nom;
        this.couleur = couleur;
        this.p = p;
        // Ajout du joueur au plateau
        p.add_joueur(this);
    }

    /**
     * Design Pattern Factory : Crée et retourne l'instance spécifique correspondant au rôle choisi.
     * @param role Le rôle souhaité
     * @param nom Le nom du joueur
     * @param p Le plateau de jeu
     * @return Une instance d'une classe fille de Joueur comme Navigatrice
     */
    public static Joueur creerJoueur(Role role, String nom, Plateau p) {
        switch (role) {
            case ARCHEOLOGUE:
                return new Archeologue(nom, Color.RED, p);
            case ALPINISTE:
                return new Alpiniste(nom, Color.BLACK, p);
            case EXPLORATEUR:
                return new Explorateur(nom, Color.GREEN, p);
            case METEOROLOGUE:
                return new Meteorologue(nom, Color.WHITE, p);
            case NAVIGATRICE:
                return new Navigatrice(nom, Color.YELLOW, p);
            case PORTEUSE_EAU:
                return new PorteuseEau(nom, Color.BLUE, p);
            default:
                throw new IllegalArgumentException("Rôle inconnu : " + role);
        }
    }
    @Override
    public String toString() {
        return this.nom;
    }
    // Getters
    public String getNom() {
        return this.nom;
    }
    public int getNvEau() {
        return this.niveauEau;
    }
    public int getNb_action_restante() {
        return this.nb_action_restante;
    }
    public Zone getZone() {
        return this.zone;
    }
    public Color getCouleur() {
        return couleur;
    }
    public Plateau get_plateau() {
        return this.p;
    }
    public int get_niveau_eau_max() {
        return this.niveauEauMax;
    }
    public List<Equipement> get_equipement(){
        return this.equipements;
    }

    // Getters abstraits
    public abstract Role getRole();

    // Getters méthodes

    /**
     * Calcule et retourne la liste des zones où le joueur peut se déplacer.
     * Prend en compte l'ensevelissement des cases et le réseau de tunnels.
     * @param plateau Le plateau de jeu
     * @return Liste des zones accessibles
     */
    public List<Zone> getZonesAccessibles(Plateau plateau) {
        Set<Zone> accessibles = new HashSet<>();
        int[][] directions = getDirections();
        if (zone.estAccessible()) { // à tester
            for (int[] dir : directions) {
                int ni = zone.getX() + dir[0];
                int nj = zone.getY() + dir[1];
                if (ni >= 0 && ni < 5 && nj >= 0 && nj < 5) {
                    Zone voisine = plateau.getZone(ni, nj);
                    if (voisine != null && voisine.estAccessible()) {
                        accessibles.add(voisine);
                    }
                }
            }
            accessibles.addAll(zone.getZonesTunnel());
        }
        return new ArrayList<>(accessibles);
    }

    /**
     * Calcule et retourne la liste des zones que le joueur peut désensabler ou révéler.
     * @param plateau Le plateau de jeu
     * @return Liste des zones creusables
     */
    public List<Zone> getZonesCreusables(Plateau plateau) {
        List<Zone> creusables = new ArrayList<>();
        int[][] directions = getDirections();
        for (int[] dir : directions) {
            int ni = zone.getX() + dir[0];
            int nj = zone.getY() + dir[1];
            if (ni >= 0 && ni < 5 && nj >= 0 && nj < 5) {
                Zone voisine = plateau.getZone(ni, nj);
                if (voisine != null
                        && (voisine.getNbSable() > 0 || (voisine.getNbSable() == 0 && !voisine.estRevelee()))) {
                    creusables.add(voisine);
                }
            }
        }
        if (this.zone.getNbSable() > 0 || !this.zone.estRevelee()) {
            creusables.add(this.zone);
        }
        return creusables;
    }


    // Setters

    /**
     * Déplace le joueur sur une nouvelle zone et met à jour les présences sur le plateau.
     * @param zone La nouvelle zone de destination
     */
    public void setZone(Zone zone) {
        if (this.zone != null) {
            this.zone.retirerJoueur(this); // retirer de l'ancienne case
        }
        this.zone = zone;
        zone.ajouterJoueur(this);
    }

    // Méthodes

    /** Consomme un point d'action **/
    public void joue_action() {
        this.nb_action_restante--;
    }

    /** Réinitialise les actions du joueur **/
    public void reset_action() {
        this.nb_action_restante = this.action_par_tour;
    }

    /**
     * Applique les effets d'une carte "Vague de Chaleur".
     * Baisse le niveau d'eau si le joueur n'est pas protégé par un tunnel.
     */
    public boolean vagueDeChaleur() {
        if (!this.zone.protegeChaleur()) {
            this.niveauEau--;
            return this.niveauEau > 0;
        }
        else {
            System.out.println(this.nom + "est à l'abri dans le tunnel");
            return true;
        }
    }

    /**
     * Retire du sable de la zone ciblée. Si la zone n'a plus de sable, elle est révélée.
     * Gère également la récupération automatique des engrenages.
     * @param zone La zone à creuser ou révéler
     */
    public void creuserZone(Zone zone) {
        if(zone.getNbSable() > 0) {
            zone.creuser();
        } else if (!zone.estRevelee() && zone.getNbSable() == 0){
            zone.reveler();

            if(zone.getType() == Zone.TypeZone.ENGRENAGE){
                Equipement obj = p.piocher_equipement();
                if(obj != null){
                    this.equipements.add(obj);
                    System.out.println(this.nom + "a trouvé :" + obj);
                }
            }
        } else {
            throw new IllegalStateException("Case sans sable et déjà révélée");
        }
    }

    // Gestion de l'eau

    /**
     * Fait boire de l'eau au joueur
     * @param quantite Quantité d'eau à boire
     * @return La quantité d'eau réellement bue
     */
    public int boire_eau(int quantite) {
        int avant = this.niveauEau;
        this.niveauEau = Math.min(this.niveauEau + quantite, this.niveauEauMax);
        return this.niveauEau - avant;
    }

    /**
     * Transfère de l'eau à un autre joueur.
     * @param cible Le joueur qui reçoit l'eau
     * @return true si le transfert a réussi, false sinon
     */
    public boolean donner_eau(Joueur cible) {
        if (!peut_donner_eau(cible))
            return false;
        this.niveauEau--;
        cible.boire_eau(1);
        return true;
    }

    /** Vérifie si le joueur est en condition pour donner de l'eau à la cible **/
    public boolean peut_donner_eau(Joueur cible) {
        return this.zone == cible.zone && cible.niveauEau < cible.niveauEauMax && this.niveauEau >= 1;
    }
    public void remplirGourde(){
        if (niveauEau < niveauEauMax){
            niveauEau++;
        }
    }

    public int get_eau_recuperee() {
        return 1;
    }

    // ----- Météorologue : amortir la tempête -----
    public int getReductionTempete(){
        return this.reductionTempete;
    }
    public int getAndResetReductionTempete() {
        int val = this.reductionTempete;
        this.reductionTempete = 0;
        return val;
    }
    public void amortirTempete() {
        throw new IllegalStateException("Amortissement pour un joueur autre que météorologue");
    }

    // ----- Navigatrice : déplacer un autre joueur -----
    public List<Joueur> get_joueurs_deplacables() {
        return new ArrayList<>();
    }
    public int get_max_pas_allié() {
        return 0;
    }

    // ----- Alpiniste : emmener un compagnon -----
    public boolean peut_emmener_compagnon() {
        return false;
    }
    public List<Joueur> get_compagnons_possibles() {
        return new ArrayList<>();
    }
}


/**
 * L'Archéologue peut retirer 2 unités de sable pour une seule action.
 */
class Archeologue extends Joueur {
    public Archeologue(String nom, Color couleur, Plateau p) {
        super(nom, couleur, p);
    }

    @Override
    public Role getRole() {
        return Role.ARCHEOLOGUE;
    }

    @Override
    public void creuserZone(Zone zone) {
        if(zone.getNbSable() > 1) {// Si il y a au moins 2 blocs de sable
            zone.creuser();
            zone.creuser(); // L'archéologue creuse 2 fois
        } else {
            super.creuserZone(zone); // Sinon il fait comme tout le monde
        }

    }
}

/**
 * L'Alpiniste ignore les restrictions de déplacement liées au sable
 * et peut emmener un autre joueur avec lui.
 */
class Alpiniste extends Joueur {
    public Alpiniste(String nom, Color couleur, Plateau p) {
        super(nom, couleur, p);
    }

    @Override
    public Role getRole() {
        return Role.ALPINISTE;
    }

    // L'alpiniste n'est pas impacté par le niveau de sable
    @Override
    public List<Zone> getZonesAccessibles(Plateau plateau) {
        List<Zone> accessibles = new ArrayList<>();
        int[][] directions = getDirections();
        for (int[] dir : directions) {
            int ni = zone.getX() + dir[0];
            int nj = zone.getY() + dir[1];
            if (ni >= 0 && ni < 5 && nj >= 0 && nj < 5) {
                Zone voisine = plateau.getZone(ni, nj);
                if (voisine != null) { // Le sable n'impacte pas l'alpiniste
                    accessibles.add(voisine);
                }
            }

        }
        return accessibles;
    }

    // L'alpiniste peut emmener un joueur avec lui
    @Override
    public boolean peut_emmener_compagnon() {
        return true;
    }

    @Override
    public List<Joueur> get_compagnons_possibles() {
        List<Joueur> compagnons = new ArrayList<>();
        if (zone != null) {
            for (Joueur j : zone.getJoueurs()) {
                if (j != this) {
                    compagnons.add(j);
                }
            }
        }
        return compagnons;
    }
}

/**
 * L'Explorateur peut se déplacer et creuser en diagonale.
 */
class Explorateur extends Joueur {
    public Explorateur(String nom, Color couleur, Plateau p) {
        super(nom, couleur, p);
    }

    @Override
    public Role getRole() {
        return Role.EXPLORATEUR;
    }

    // L'explorateur peut aller dans toutes les directions (y compris diagonales)
    @Override
    protected int[][] getDirections() {
        return new int[][] { { -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 }, { -1, -1 }, { -1, 1 }, { 1, -1 }, { 1, 1 } };
    }
}

/**
 * Le Météorologue peut dépenser des actions pour réduire le nombre de cartes Tempête piochées à la fin du tour.
 */
class Meteorologue extends Joueur {
    public Meteorologue(String nom, Color couleur, Plateau p) {
        super(nom, couleur, p);
    }
    @Override
    public Role getRole() {
        return Role.METEOROLOGUE;
    }

    @Override
    public void amortirTempete(){
        this.reductionTempete++;
        this.joue_action();
    }

}

/**
 * La Navigatrice peut déplacer d'autres joueurs jusqu'à 3 cases pour une seule action.
 */
class Navigatrice extends Joueur {
    public Navigatrice(String nom, Color couleur, Plateau p) {
        super(nom, couleur, p);
    }

    @Override
    public Role getRole() {
        return Role.NAVIGATRICE;
    }

    // La navigatrice peut déplacer un autre joueur de 3 cases max
    @Override
    public List<Joueur> get_joueurs_deplacables() {
        List<Joueur> deplacables = new ArrayList<>();
        for (Joueur j : get_plateau().getJoueurs()) {
            if (j != this) {
                deplacables.add(j);
            }
        }
        return deplacables;
    }

    @Override
    public int get_max_pas_allié() {
        return 3;
    }
}

/**
 * La Porteuse d'Eau a une plus grande réserve (5) et peut donner de l'eau aux cases adjacentes.
 */
class PorteuseEau extends Joueur {
    public PorteuseEau(String nom, Color couleur, Plateau p) {
        super(nom, couleur, p);
        this.niveauEau = 5;
        this.niveauEauMax = 5;
    }

    @Override
    public Role getRole() {
        return Role.PORTEUSE_EAU;
    }

    @Override
    public int get_eau_recuperee() {
        return 2;
    }

    // La porteuse d'eau peut donner de l'eau aux joueurs adjacents (N/S/E/O) ou sur
    // la même case

    @Override
    public boolean peut_donner_eau(Joueur cible) {
        List<Zone> accessibles = getZonesAccessibles(this.get_plateau());
        return accessibles.contains(cible.getZone()) && cible.niveauEau < cible.niveauEauMax && this.niveauEau >= 1;
    }

    @Override
    public void remplirGourde(){
        for (int i = 0; i < 2; i++) {
            if (niveauEau < niveauEauMax){
                niveauEau++;
            }
        }

    }

}