package modele;

import java.awt.*;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

public abstract class Joueur {

    public String getNom() {
        return this.nom;
    }

    public int getNvEau() {
        return this.niveauEau;
    }

    public void vagueDeChaleur() {
        if (!this.zone.protegeChaleur()) {
            this.niveauEau--;
            if (this.niveauEau <= 0) {
                p.declencher_defaite(this.nom + " est mort de soif !"); // A modifier pour gerer dans le controlleur
            }
        }
    }

    public enum Role {
        ARCHEOLOGUE,
        ALPINISTE,
        EXPLORATEUR,
        METEOROLOGUE,
        NAVIGATRICE,
        PORTEUSE_EAU
    }

    public abstract Role getRole();

    private String nom;
    protected int niveauEau = 4;
    protected int niveauEauMax = 4;
    protected Zone zone;
    private Color couleur;
    private final int action_par_tour = 4;
    private int nb_action_restante = action_par_tour;
    private Plateau p;
    private boolean tempeteAmortie = false; // météorologue

    public Joueur(String nom, Color couleur, Plateau p) {
        this.nom = nom;
        this.couleur = couleur;
        this.p = p;
        // Ajout du joueur au plateau
        p.add_joueur(this);
    }

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

    public void setZone(Zone zone) {
        if (this.zone != null) {
            this.zone.retirerJoueur(this); // retirer de l'ancienne case
        }
        this.zone = zone;
        zone.ajouterJoueur(this);
    }

    public int getNb_action_restante() {
        return this.nb_action_restante;
    }

    public void joue_action() {
        this.nb_action_restante--;
    }

    public void reset_action() {
        this.nb_action_restante = this.action_par_tour;
    }

    public Zone getZone() {
        return this.zone;
    }

    public Color getCouleur() {
        return couleur;
    }

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

    // Par défaut
    protected int[][] getDirections() {
        return new int[][] { { -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 } };
    }

    // ----- Gestion de l'eau -----

    public int get_niveau_eau_max() {
        return this.niveauEauMax;
    }

    public int boire_eau(int quantite) {
        int avant = this.niveauEau;
        this.niveauEau = Math.min(this.niveauEau + quantite, this.niveauEauMax);
        return this.niveauEau - avant;
    }

    public boolean donner_eau(Joueur cible) {
        if (this.niveauEau <= 0)
            return false;
        if (cible.niveauEau >= cible.niveauEauMax)
            return false;
        if (!peut_donner_eau(cible))
            return false;
        this.niveauEau--;
        cible.boire_eau(1);
        return true;
    }

    public boolean peut_donner_eau(Joueur cible) {
        return this.zone == cible.zone;
    }

    public int get_sable_retire_par_action() {
        return 1;
    }

    public int get_eau_recuperee() {
        return 1;
    }

    // ----- Météorologue : amortir la tempête -----

    public boolean is_tempete_amortie() {
        return tempeteAmortie;
    }

    public void set_tempete_amortie(boolean b) {
        this.tempeteAmortie = b;
    }

    public boolean peut_amortir_tempete() {
        return false;
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

    public Plateau get_plateau() {
        return this.p;
    }

}

class Archeologue extends Joueur {
    public Archeologue(String nom, Color couleur, Plateau p) {
        super(nom, couleur, p);
    }

    @Override
    public Role getRole() {
        return Role.ARCHEOLOGUE;
    }

    // L'archéologue retire 2 sable par action de creusage
    @Override
    public int get_sable_retire_par_action() {
        return 2;
    }
}

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

class Meteorologue extends Joueur {
    public Meteorologue(String nom, Color couleur, Plateau p) {
        super(nom, couleur, p);
    }

    @Override
    public Role getRole() {
        return Role.METEOROLOGUE;
    }

    @Override
    public boolean peut_amortir_tempete() {
        return true;
    }
}

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
        if (this.getZone() == null || cible.getZone() == null)
            return false;
        if (this.getZone() == cible.getZone())
            return true;
        int dx = Math.abs(this.getZone().getX() - cible.getZone().getX());
        int dy = Math.abs(this.getZone().getY() - cible.getZone().getY());
        return (dx + dy) == 1;
    }

    public boolean donner_eau_gratuit() {
        return true;
    }
}