package modele;

import java.awt.*;
import java.util.List;
import java.util.ArrayList;

public /** abstract **/
class Joueur {
    private String nom;
    private int niveauEau;
    protected Zone zone;
    private Color couleur;
    private final int action_par_tour = 4;
    private int nb_action_restante = action_par_tour;

    public Joueur(String nom, Color couleur, Plateau p) {
        this.nom = nom;
        this.niveauEau = 3; // a voir
        this.couleur = couleur;
        // Ajout du joueur au plateau
        p.add_joueur(this);
    }

    public void setZone(Zone zone) {
        if (this.zone != null) {
            this.zone.retirerJoueur(this); // retirer de l'ancienne
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
    }
}