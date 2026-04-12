package modele;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

import vue.Observable;

import javax.swing.Timer;

/**
 * La classe Plateau est le Modèle central du jeu
 * Elle gère la grille de 5x5, la position de l'œil de la tempête, le niveau de
 * sable,
 * les mécaniques de la tempête, et les conditions de victoire ou de défaite.
 */
public class Plateau extends Observable {
    private Zone[][] grille;
    private double niveauTempete = 2.0;
    private int oeilI = 2;
    private int oeilJ = 2;
    private int totalSable = 0;
    private static final int SABLE_MAX = 48;
    private List<Zone> zonesAccessibles = new ArrayList<>();
    private List<Joueur> joueurs = new ArrayList<>();
    private Zone zoneCrash;
    private int indiceJoueurActif = 0;
    private List<Equipement> pioche_equipement = new ArrayList<>();
    private Map<PIECE, Boolean> etatPieces = new HashMap<>();
    private PaquetTempete paquetTempete = new PaquetTempete();

    public PaquetTempete getPaquetTempete() {
        return paquetTempete;
    }

    private List<Zone.TypeZone> attenteTempete = new ArrayList<>();


    public Joueur getJoueurActif() {
        return joueurs.get(indiceJoueurActif);
    }



    public enum PIECE {
        BOITE, HELICE, CRISTAL, NAVIGATION
    }

    public Map<PIECE, Boolean> getEtatPieces() {
        return etatPieces;
    }


    public double getNiveauTempete() {
        return niveauTempete;
    }

    public int getTotalSable() {
        return totalSable;
    }

    public enum EtatPartie {
        EN_COURS, VICTOIRE, DEFAITE
    };

    private EtatPartie etat = EtatPartie.EN_COURS;
    private String raison_fin = "";

    /**
     * Constructeur du Plateau. Prépare la grille vide puis lance l'initialisation.
     */
    public Plateau() {
        super();
        this.grille = new Zone[5][5];
        etatPieces.put(PIECE.BOITE, false);
        etatPieces.put(PIECE.HELICE, false);
        etatPieces.put(PIECE.CRISTAL, false);
        etatPieces.put(PIECE.NAVIGATION, false);
        initialiseGrille();
    }

    /**
     * Initialise le plateau de jeu :
     * - Crée et mélange les tuiles (Zones)
     * - Place l'œil du cyclone au centre
     * - Dispose les premiers marqueurs de sable en croix
     * - Crée et mélange la pioche d'équipements
     */
    public void initialiseGrille() {
        ArrayList<Zone.TypeZone> typesZone = new ArrayList<>(Arrays.asList(Zone.TypeZone.values()));
        ArrayList<Zone.TypeZone> zonesDispo = new ArrayList<>();
        // On ajoute le bon nombre de chaque zone
        for (Zone.TypeZone type : typesZone) {
            for (int i = 0; i < type.nb; i++) {
                zonesDispo.add(type);
            }
        }
        // On mélange les zones
        Collections.shuffle(zonesDispo);
        Map<Zone.TypeZone, Boolean> indiceI =  new HashMap<>();
        indiceI.put(Zone.TypeZone.INDICE_BOITE, false);
        indiceI.put(Zone.TypeZone.INDICE_HELICE, false);
        indiceI.put(Zone.TypeZone.INDICE_CRISTAL, false);
        indiceI.put(Zone.TypeZone.INDICE_NAVIGATION, false);


        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                if (i == oeilI && j == oeilJ) {
                    grille[i][j] = null; // l'oeil
                } else {
                    Zone.TypeZone type = zonesDispo.remove(zonesDispo.size() - 1);
                    char axe = 'c';
                    if(indiceI.containsKey(type)){ // si le type est un indice
                        if (!indiceI.get(type)){
                            axe = 'i';
                            indiceI.put(type, true);
                        }
                        else{
                            axe = 'j';

                        }
                    }
                    grille[i][j] = Zone.creerZone(i, j, type, axe, this);
                    if (type == Zone.TypeZone.CRASH) {
                        zoneCrash = grille[i][j];
                    }
                }
            }
        }

        int[][] posSable = { { 0, 2 }, { 1, 1 }, { 1, 3 }, { 2, 0 }, { 2, 4 }, { 3, 1 }, { 3, 3 }, { 4, 2 } };

        for (int[] pos : posSable) {
            this.ajouterSable(grille[pos[0]][pos[1]], 1);
        }

        for (Equipement e : Equipement.values()) {
            this.pioche_equipement.add(e);
            this.pioche_equipement.add(e);
        }
        Collections.shuffle(this.pioche_equipement);
    }

    // Getters et Setters
    public Zone getZoneCrash() {
        return this.zoneCrash;
    }

    public void add_piece(Plateau.PIECE piece) {
        etatPieces.put(piece, true);
    }

    public int getIndiceJoueurActif() {
        return this.indiceJoueurActif;
    }

    public List<Joueur> getJoueurs() {
        return joueurs;
    }

    public void add_joueur(Joueur j) {
        joueurs.add(j);
        notifyObservers();

    }

    public Zone getZone(int i, int j) {
        return grille[i][j];
    }

    public int getOeilI() {
        return oeilI;
    }

    public int getOeilJ() {
        return oeilJ;
    }

    public void setZonesAccessibles(List<Zone> zones) {
        this.zonesAccessibles = zones;
        notifyObservers();
    }

    public void resetZonesAccessibles() {
        this.zonesAccessibles = new ArrayList<Zone>();
        notifyObservers();
    }

    public List<Zone> getZonesAccessibles() {
        return zonesAccessibles;
    }

    public EtatPartie getEtat() {
        return this.etat;
    }

    public String getRaison_fin() {
        return this.raison_fin;
    }

    /**
     * Pioche la première carte équipement du paquet.
     * 
     * @return L'équipement pioché, ou null si la pioche est vide.
     */
    public Equipement piocher_equipement() {
        if (!this.pioche_equipement.isEmpty()) {
            return pioche_equipement.remove(pioche_equipement.size() - 1);
        }
        return null;
    }

    // Convertisseur en String
    @Override
    public String toString() {
        String res = "";
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                if (i == oeilI && j == oeilJ) {
                    res += "X ";
                } else {
                    res += grille[i][j].toString() + " ";
                }
            }
            res += "\n";
        }
        return res;
    }

    // Déroulement du jeu
    /**
     * Clôture le tour du joueur actif, réinitialise ses actions et déclenche la tempête.
     */
    public void finDeTour() {
        this.joueurs.get(indiceJoueurActif).reset_action();
        this.indiceJoueurActif = (this.indiceJoueurActif + 1) % joueurs.size();
        this.joueurs.get(indiceJoueurActif).reset_action();
    }

    /**
     * Vérifie si les conditions de victoire sont réunies :
     * 1. Tous les joueurs sont sur la case Décollage.
     * 2. Les 4 pièces de la machine ont été ramassées.
     */
    public void verifier_victoire() {
        boolean tous_au_decollage = true;
        for (Joueur j : this.joueurs) {
            if (j.getZone().getType() != Zone.TypeZone.DECOLLAGE) {
                tous_au_decollage = false;
            }
        }
        if (tous_au_decollage && !this.etatPieces.containsValue(false)) { // Si tous les joueurs sont au décollage et
                                                                          // qu'en plus toutes les pièces ont été
                                                                          // ramassées (aucune pièce à false)
            this.etat = EtatPartie.VICTOIRE;
            this.raison_fin = "Vous avez gagné!";
            notifyObservers();
        }
    }

    /**
     * Termine la partie sur une défaite.
     * 
     * @param raison Le texte expliquant la cause de la mort (soif, ensablé,
     *               tempête).
     */
    public void declencher_defaite(String raison) {
        this.etat = EtatPartie.DEFAITE;
        this.raison_fin = raison;
    }

    // Mécaniques de la tempête

    /**
     * Effectue une action du désert et renvoie la carte dont l'action a été effectuée
     */
    public void action_desert() {
        CarteTempete carte = paquetTempete.piocher();
        switch (carte.getType()){
            case VENT:
                leVentSouffle(carte.getDirection(),carte.getForce());
                break;
            case DECHAINE:
                LaTempeteSeDechaine();
                break;
            case CHALEUR:
                vagueDeChaleur();
        }
        notifyObservers();
    }

    /**
     * Déplace les tuiles du plateau dans la direction du vent
     * et ajoute du sable sur les tuiles déplacées.
     * 
     * @param direction
     * @param force     Le nombre de tuiles déplacées (1 à 3)
     */
    public void leVentSouffle(CarteTempete.Direction direction, int force) {

        for (int k = 0; k < force; k++) {
            int iCible = oeilI;
            int jCible = oeilJ;
            switch (direction){
                case NORD:
                    iCible += 1;
                    break;
                case SUD:
                    iCible -= 1;
                    break;
                case EST:
                    jCible -= 1;
                    break;
                case OUEST:
                    jCible += 1;
                    break;
            }


            if (iCible < 0 || iCible >= 5 || jCible < 0 || jCible >= 5) {
                break;
            }

            Zone tuileAspiree = grille[iCible][jCible];

            this.ajouterSable(tuileAspiree, 1);

            grille[oeilI][oeilJ] = tuileAspiree;
            tuileAspiree.set_position(oeilI, oeilJ);
            grille[iCible][jCible] = null;

            // si la tempête a bougé alors il faut potentiellement faire apparaitre un objet en attente
            if(!attenteTempete.isEmpty() && (oeilI != iCible || oeilJ != jCible)){
                Zone z = getZone(oeilI,oeilJ);
                for (Zone.TypeZone type : attenteTempete){
                    z.poser_objet(type);
                }
                attenteTempete.clear();
            }
            oeilI = iCible;
            oeilJ = jCible;
        }
    }

    public void vagueDeChaleur() {
        for (Joueur j : joueurs) {
            boolean vivant = j.vagueDeChaleur();
            if (!vivant) {
                declencher_defaite(j.getNom() + " est mort de soif !");
            }
        }
    }

    public void LaTempeteSeDechaine() {
        this.niveauTempete += 0.5;
        if (this.niveauTempete >= 5.0) {
            this.declencher_defaite("La tempête a atteint son niveau maximal, vous vous êtes envolés..");
            notifyObservers();
        }
    }

    // Gestion du terrain

    /**
     * Ajoute du sable sur une case et vérifie la condition de défaite par
     * ensevelissement.
     * 
     * @param zone     La zone à ensabler
     * @param quantite Nombre de marqueurs à ajouter
     * @return true si l'ajout s'est bien passé, false si le seuil max a déclenché
     *         une défaite
     */
    public boolean ajouterSable(Zone zone, int quantite) {
        if (totalSable + quantite > SABLE_MAX) {
            this.declencher_defaite("Mort ensablés : trop de sable");
            notifyObservers();
            return false;
        }
        zone.ajouterSable(quantite);
        totalSable += quantite;
        return true;
    }

    /**
     * Vérifie si les deux indices (ligne et colonne) d'une même pièce
     * ont été révélés. Si oui, fait apparaître l'objet à leur intersection.
     * 
     * @param type Le type de la case indice venant d'être révélée
     */
    public void verifie_apparition_obj(Zone.TypeZone type) {
        int ii = -1;
        int jj = -1;
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                Zone z = grille[i][j];

                if (z != null && z.getType() == type && z.estRevelee()) {
                    if (z.getAxe() == 'i') {
                        ii = i;
                    } else {
                        jj=j;
                    }
                }
            }
        }
        if (ii != -1 && jj != -1) {
            Zone intersection = getZone(ii, jj);
            if (intersection != null) {
                intersection.poser_objet(type);
                System.out.println("Une pièce de la navette (" + type + ") vient d'apparaitre !");
            } else {
                attenteTempete.add(type);
                System.out.println("L'objet (" + type + ") est dans la tempête pour le moment !");
            }
        }
    }

    /** Retire du sable du compteur global **/
    public void enlever_sable(int quantite) {
        this.totalSable = Math.max(0, this.totalSable - quantite);
        notifyObservers();
    }

    // Méthode  utilisée pour les tests
    public void placerZonePourTest(int i, int j, Zone z) {
        grille[i][j] = z;
        if (z != null) {
            z.set_position(i, j);
        }
    }

    public List<Zone.TypeZone> testGetObjetsEnAttente() {
        return attenteTempete;
    }
}
