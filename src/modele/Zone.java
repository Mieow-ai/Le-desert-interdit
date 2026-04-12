package modele;

import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Classe abstraite représentant une tuile (case) du plateau de jeu.
 * Elle gère l'état physique de la case (niveau de sable, statut révélé,
 * coordonnées),
 * les joueurs présents, et les pièces de la navette qui peuvent y apparaître.
 * Elle sert de super-classe pour tous les types spécifiques de zones.
 */
public abstract class Zone {
    private int nbSable = 0;
    private int x;
    private int y;
    private List<Joueur> joueurs = new ArrayList<>();
    protected boolean revelee = false;
    private TypeZone objetpose = null;
    protected Plateau p;
    private boolean protege_par_bouclier = false;

    public char getAxe() {
        throw new IllegalStateException("La zone n'est pas un indice");
    };

    /**
     * Énumération de tous les types de tuiles possibles dans le jeu.
     * Contient également le nombre d'exemplaires de chaque tuile pour la constitution du paquet.
     */
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

    /**
     * Constructeur Zone. Ne peut être appelé que par les classes filles. (Pattern Factory)
     * 
     * @param i Coordonnée X initiale
     * @param j Coordonnée Y initiale
     */
    protected Zone(int i, int j, Plateau p) {
        this.p = p;
        this.x = i;
        this.y = j;
    }

    /**
     * Factory Method : Instancie et retourne la bonne classe fille en fonction du type demandé.
     * 
     * @param i    Coordonnée X
     * @param j    Coordonnée Y
     * @param type Le type de la zone à créer
     * @return L'instance spécifique de la Zone (ex: ZoneEau, ZoneTunnel, etc.)
     */
    public static Zone creerZone(int i, int j, TypeZone type, char axe, Plateau p) {
        switch (type) {
            case CRASH:
                return new ZoneCrash(i, j, p);
            case DECOLLAGE:
                return new ZoneDecollage(i, j, p);
            case EAU:
                return new ZoneEau(i, j, p);
            case MIRAGE:
                return new ZoneMirage(i, j, p);
            case TUNNEL:
                return new ZoneTunnel(i, j, p);
            case INDICE_BOITE:
            case INDICE_CRISTAL:
            case INDICE_HELICE:
            case INDICE_NAVIGATION:
                return new ZoneIndice(i, j, type, axe, p);
            case ENGRENAGE:
            default:
                return new ZoneEngrenage(i, j, p);
        }
    }

    // Getters
    public abstract TypeZone getType();
    public TypeZone get_objet_pose() {
        return this.objetpose;
    }
    public List<Joueur> getJoueurs() {
        return joueurs;
    }
    public int getNbSable() {
        return this.nbSable;
    }
    public int getX() {
        return this.x;
    }
    public int getY() {
        return this.y;
    }
    public void poser_objet(TypeZone objet) {
        this.objetpose = objet;
    }
    public boolean estRevelee() { return this.revelee; }

    // Setters
    public void set_protege_par_bouclier(boolean etat) {
        this.protege_par_bouclier = etat;
    }
    public void set_position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    // Méthodes
    public TypeZone ramasser_objet() {
        TypeZone obj = this.objetpose;
        this.objetpose = null;
        return obj;
    }

    public void ajouterJoueur(Joueur j) {
        joueurs.add(j);
    }
    public void retirerJoueur(Joueur j) {
        joueurs.remove(j);
    }

    /**
     * La case est toujours accessible si l'alpiniste est dessus
     * Sinon il faut 0 ou 1 tonne sable
     * @return Booléen indiquant si la case est accessible
     */
    public boolean estAccessible() {
        boolean alpiniste = false;
        for (Joueur j : joueurs) {
            if (j.getRole() == Joueur.Role.ALPINISTE) {
                alpiniste = true;
                break;
            }
        }
        return this.nbSable <= 1 || alpiniste;
    }

    // Mécanique de terrain

    /**
     * Ajoute du sable
     * @param nbQuantite quantité de sable à ajouter
     */
    public void ajouterSable(int nbQuantite) {
        nbSable += nbQuantite;
    }

    /**
     * Retire un marqueur de sable. Lève une exception si la case est déjà vide.
     **/
    public void creuser() {
        if (nbSable > 0) {
            nbSable--;
        } else {
            throw new IllegalStateException("Case sans sable, impossible de creuser");
        }
    }

    /**
     * Détruit tout le sable d'un seul coup (utilisé par le Blaster des Dunes).
     * @return Le nombre exact de marqueurs de sable retirés.
     */
    public int viderSable() {
        int sable_retirer = this.nbSable;
        this.nbSable = 0;
        return sable_retirer;
    }

    /**
     * Retourne la tuile face visible. Ne fonctionne que s'il n'y a plus de sable.
     * Déclenche l'effet spécifique (selon le type) de la tuile via `actionReveler()`.
     */
    public void reveler() {
        if (nbSable == 0 && !this.revelee) {
            this.revelee = true;
            this.actionReveler();
        } else {
            throw new IllegalStateException("Case avec sable ou déjà révélée");
        }

    }

    /**
     * Force la révélation de la zone sans vérifier la présence de sable.
     * Utile pour les tests et le débogage ainsi que pour le design de l'ui.
     */
    public void forcerRevelation() {
        if (!this.revelee) {
            this.revelee = true;
            this.actionReveler();
        }
    }

    // méthodes polymorphes


    /** Override par la zone tunnel
     * @return true si cette case protège de la carte "Vague de Chaleur". (Faux par défaut)
     */
    public boolean protegeChaleur() {
        return protege_par_bouclier;
    }
    /**
     * * Action déclenchée automatiquement au moment où la case est révélée.
     * Par défaut ne fait rien, mais est surchargée par Oasis, Tunnel, etc.
     */
    protected void actionReveler() {
    }

    /**
     * @return La liste des autres tunnels connectés à cette case. (Vide par défaut)
     */
    public Collection<Zone> getZonesTunnel() {
        return Collections.emptyList();
    }

    // Format String d'une Zone
    public String toString() {
        return String.valueOf(nbSable);
    }

}

/** Point de départ de l'équipe. Est révélée dès le début de la partie. **/
class ZoneCrash extends Zone {
    @Override
    public TypeZone getType() {
        return TypeZone.CRASH;
    }

    public ZoneCrash(int i, int j, Plateau p) {
        super(i, j, p);
        this.revelee = true;
    }
}

/**
 * Tuile classique contenant un symbole d'engrenage. Gérée dans la méthode
 * creuser() du Joueur.
 **/
class ZoneEngrenage extends Zone {
    public ZoneEngrenage(int i, int j, Plateau p) {
        super(i, j, p);
    }

    @Override
    public TypeZone getType() {
        return TypeZone.ENGRENAGE;
    }
}

/**
 * Tuile indiquant les coordonnées X ou Y d'une pièce de la machine volante.
 **/
class ZoneIndice extends Zone {
    private TypeZone typeIndice;
    private char axe;

    public ZoneIndice(int i, int j, TypeZone type, char axe, Plateau p) {
        super(i, j, p);
        this.typeIndice = type;
        this.axe = axe;
    }

    @Override
    public TypeZone getType() {
        return this.typeIndice;
    }

    @Override
    protected void actionReveler() {
        p.verifie_apparition_obj(this.typeIndice);
    }

    @Override
    public char getAxe() {
        return this.axe;
    }
}

/**
 * Les tunnels protègent de la chaleur s'ils sont révélés
 * et permettent de se téléporter vers n'importe quel autre tunnel révélé.
 */
class ZoneTunnel extends Zone {
    static Collection<Zone> tunnelsReveles = new HashSet<>();
    public ZoneTunnel(int i, int j, Plateau p) {
        super(i, j, p);
        // Lors de la création les tunnels révélés sont réinitialisé (signe d'une nouvelle partie)
        resetTunnels();

    }
    // L'attribut tunnels révélés doit être réinitialisé lorsqu'une nouvelle partie commence
    public static void resetTunnels() {
        tunnelsReveles.clear();
    }



    @Override
    public boolean protegeChaleur() {
        if (this.revelee){return true;}
        else {
            return super.protegeChaleur();
        }
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

// Lieu d'assemblage final. Tous les joueurs doivent s'y trouver pour gagner.

class ZoneDecollage extends Zone {
    public ZoneDecollage(int i, int j, Plateau p) {
        super(i, j, p);
    }

    @Override
    public TypeZone getType() {
        return TypeZone.DECOLLAGE;
    }
}

/**
 * Restaure 2 unités d'eau à tous les joueurs présents lors de sa découverte. ù
 */
class ZoneEau extends Zone {
    public ZoneEau(int i, int j, Plateau p) {
        super(i, j, p);
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

/** Ne donne aucune eau lors de sa révélation. **/
class ZoneMirage extends Zone {
    public ZoneMirage(int i, int j, Plateau p) {
        super(i, j, p);
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
