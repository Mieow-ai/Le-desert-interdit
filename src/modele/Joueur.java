package modele;

import java.awt.*;
import java.util.List;
import java.util.ArrayList;

public abstract class Joueur {

    public String getNom() {
        return this.nom;
    }

    public int getNvEau() {
        return this.niveauEau;
    }

    public enum Role {
        ARCHEOLOGUE,
        ALPINISTE,
        EXPLORATEUR,
        METEOROLOGUE,
        NAVIGATRICE,
        PORTEUSE_EAU

    }

    private String nom;
    protected int niveauEau = 4;
    protected final int niveauEauMax = 4;
    protected Zone zone;
    private Color couleur;
    private final int action_par_tour = 4;
    private int nb_action_restante = action_par_tour;

    public Joueur(String nom, Color couleur, Plateau p) {
        this.nom = nom;
        this.couleur = couleur;
        // Ajout du joueur au plateau
        p.add_joueur(this);
    }

    public static Joueur creerJoueur(Role role, String nom, Plateau p) {
        switch (role) {
            case ARCHEOLOGUE: return new Archeologue(nom, Color.red, p);
            case ALPINISTE: return new Alpiniste(nom, Color.black, p);
            case EXPLORATEUR: return new Explorateur(nom, Color.green, p);
            case METEOROLOGUE: return new Meteorologue(nom, Color.WHITE, p);
            case NAVIGATRICE: return new Navigatrice(nom, Color.yellow, p);
            case PORTEUSE_EAU: return new PorteuseEau(nom, Color.blue, p);
            default: throw new IllegalArgumentException("Rôle inconnu");
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
    public void boireEau(){
        this.niveauEau--;
    }

    public List<Zone> getZonesAccessibles(Plateau plateau) {
        List<Zone> accessibles = new ArrayList<>();
        int[][] directions = getDirections();
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
        return accessibles;
    }

    public List<Zone> getZonesCreusables(Plateau plateau) {
        List<Zone> creusables = new ArrayList<>();
        int[][] directions = getDirections();
        for (int[] dir : directions) {
            int ni = zone.getX() + dir[0];
            int nj = zone.getY() + dir[1];
            if (ni >= 0 && ni < 5 && nj >= 0 && nj < 5) {
                Zone voisine = plateau.getZone(ni, nj);
                if (voisine != null && voisine.getNbSable() > 0) {
                    creusables.add(voisine);
                }
            }
        }
        if (this.zone.getNbSable() > 0) {
            creusables.add(this.zone);
        }
        return creusables;
    }

    // Par défaut
    protected int[][] getDirections() {
        return new int[][] { { -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 } };
    }

}

class Archeologue extends Joueur {
    public Archeologue(String nom, Color couleur, Plateau p) {
        super(nom, couleur, p);
    }
}

class Alpiniste extends Joueur {
    public Alpiniste(String nom, Color couleur, Plateau p) {
        super(nom, couleur, p);
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
}

class Explorateur extends Joueur {
    public Explorateur(String nom, Color couleur, Plateau p) {
        super(nom, couleur, p);
    }

    // L'explorateur peut aller dans toutes les directions
    @Override
    protected int[][] getDirections() {
        return new int[][] { { -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 }, { -1, -1 }, { -1, 1 }, { 1, -1 }, { 1, 1 } };
    }

}

class Meteorologue extends Joueur {
    public Meteorologue(String nom, Color couleur, Plateau p) {
        super(nom, couleur, p);
    }
}

class Navigatrice extends Joueur {
    public Navigatrice(String nom, Color couleur, Plateau p) {
        super(nom, couleur, p);
    }
}

class PorteuseEau extends Joueur {
    public PorteuseEau(String nom, Color couleur, Plateau p) {
        super(nom, couleur, p);
        this.niveauEau = 5; // La porteuse d'eau part avec un niveau de 5
        //this.niveauEauMax = 5;
    }
}