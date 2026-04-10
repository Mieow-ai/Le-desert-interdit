package modele;

import org.junit.Test;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import static modele.Joueur.Role.ARCHEOLOGUE;

/**
 * class position {
 * private int x;
 * private int y;
 * public position(int i, int j){
 * this.x = i;
 * this.y = j;
 * }
 * }
 **/

public abstract class Zone {
    private int nbSable = 0;
    private int x;
    private int y;
    private List<Joueur> joueurs = new ArrayList<>();
    protected boolean revelee = false;
    private TypeZone objetpose = null;

    public void poser_objet(TypeZone objet){
        this.objetpose = objet;
    }

    public TypeZone ramasser_objet(){
        TypeZone obj = this.objetpose;
        this.objetpose = null;
        return obj;
    }

    public TypeZone get_objet_pose() {
        return this.objetpose;
    }

    public boolean protegeChaleur() {
        return false;
    }

    public Collection<Zone> getZonesTunnel() {
        return Collections.emptyList();
    }

    public boolean estRevelee() {
        return this.revelee;
    }

    public abstract TypeZone getType();

    public enum TypeZone {
        ENGRENAGE(8),
        DECOLLAGE(1),
        CRASH(1),
        EAU(2),
        MIRAGE(1),
        INDICE_BOITE(2),
        INDICE_HELICE(2),
        INDICE_CRISTAL(2),
        INDICE_NAVIGATION(2),
        TUNNEL(3);

        public final int nb;

        TypeZone(int nb) {
            this.nb = nb;
        }
    }

    // Constructeur Zone
    protected Zone(int i, int j) {
        this.x = i;
        this.y = j;
    }

    public static Zone creerZone(int i, int j, TypeZone type) {
        switch (type) {
            case CRASH:
                return new ZoneCrash(i, j);
            case DECOLLAGE:
                return new ZoneDecollage(i, j);
            case EAU:
                return new ZoneEau(i, j);
            case MIRAGE:
                return new ZoneMirage(i, j);
            case TUNNEL:
                return new ZoneTunnel(i, j);
            case INDICE_BOITE:
            case INDICE_CRISTAL:
            case INDICE_HELICE:
            case INDICE_NAVIGATION:
                return new ZoneIndice(i, j, type);
            case ENGRENAGE:
            default:
                return new ZoneEngrenage(i, j);
        }
    }

    public int viderSable(){
        int sable_retirer = this.nbSable;
        this.nbSable = 0;
        return sable_retirer;
    }

    public void set_position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public void ajouterJoueur(Joueur j) {
        joueurs.add(j);
    }

    public void retirerJoueur(Joueur j) {
        joueurs.remove(j);
    }

    public List<Joueur> getJoueurs() {
        return joueurs;
    }

    // Ajoute du sable selon une quantité
    public void ajouterSable(int nbQuantite) {
        nbSable += nbQuantite;
    }


    public void creuser() {
        if (nbSable > 0) {
            nbSable--;
        }
        else {
            throw new IllegalStateException("Case sans sable, impossible de creuser");
        }
    }

    public void reveler () {
        if (nbSable == 0 && !this.revelee) {
            this.revelee = true;
            this.actionReveler();
        } else {
            throw new IllegalStateException("Case avec sable ou déjà révélée");
        }

    }

    protected void actionReveler() {
    }

    public int getNbSable() {
        return this.nbSable;
    }

    public boolean estAccessible() {
        return this.nbSable <= 1;
    }


    // Format String d'une Zone
    public String toString() {
        return String.valueOf(nbSable);
    }
}

class ZoneCrash extends Zone {
    @Override
    public TypeZone getType() {
        return TypeZone.CRASH;
    }

    public ZoneCrash(int i, int j) {
        super(i, j);
        this.revelee = true;
    }
}

class ZoneEngrenage extends Zone {
    public ZoneEngrenage(int i, int j) {
        super(i, j);
    }
    @Override
    public TypeZone getType() {
        return TypeZone.ENGRENAGE;
    }
}

class ZoneIndice extends Zone {
    private TypeZone typeIndice;

    public ZoneIndice(int i, int j, TypeZone type) {
        super(i, j);
        this.typeIndice = type;
    }

    @Override
    public TypeZone getType() {
        return this.typeIndice; // à gérer renvoie toujours le même
    }

}

class ZoneTunnel extends Zone {
    static Collection<Zone> tunnelsReveles = new HashSet<>();

    public static void resetTunnels() {
        tunnelsReveles.clear();
    }

    public ZoneTunnel(int i, int j) {
        super(i, j);
    }

    @Override
    public boolean protegeChaleur() {
        return this.revelee;
    }

    @Override
    public Collection<Zone> getZonesTunnel() {
        return tunnelsReveles.stream()
                .filter(Zone::estAccessible)
                .collect(Collectors.toList());
    }

    @Override
    protected void actionReveler() {
        this.tunnelsReveles.add(this);
    }
    @Override
    public TypeZone getType() {
        return TypeZone.TUNNEL;
    }
}

class ZoneDecollage extends Zone {
    public ZoneDecollage(int i, int j) {
        super(i, j);
    }
    @Override
    public TypeZone getType() {
        return TypeZone.DECOLLAGE;
    }
}

class ZoneEau extends Zone {
    public ZoneEau(int i, int j) {
        super(i, j);
    }
    @Override
    public TypeZone getType() {
        return TypeZone.EAU;
    }
    @Override
    protected void actionReveler() {
        System.out.println("Source d'eau découverte");
        for (Joueur j : this.getJoueurs()) {
            j.boire_eau(2);
        }
    }
}

class ZoneMirage extends Zone {
    public ZoneMirage(int i, int j){
        super(i, j);
    }
    @Override
    public TypeZone getType() {
        return TypeZone.MIRAGE;
    }
    @Override
    protected void actionReveler() {
        System.out.println("C'est un mirage");
    }
}
