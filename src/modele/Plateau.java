package modele;

import java.lang.Math.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import vue.Observable;

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

    public Zone getZoneCrash() {
        return this.zoneCrash;
    }

    public Plateau() {
        super();
        this.grille = new Zone[5][5];
        initialiseGrille();
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

    // Initialise la Grille/Plateau
    public void initialiseGrille() {
        ArrayList<Zone.TypeZone> typesZone = new ArrayList<>(Arrays.asList(Zone.TypeZone.values()));
        ArrayList<Zone.TypeZone> zonesDispo = new ArrayList<>();
        // On ajoute le bon nombre de chaque zone
        for(Zone.TypeZone type : typesZone ){
            for (int i = 0; i <type.nb ; i++) {
                zonesDispo.add(type);
            }
        }
        // On mélange les zones
        Collections.shuffle(zonesDispo);
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                if (i == oeilI && j == oeilJ) {
                    grille[i][j] = null; // l'oeil
                } else {
                    Zone.TypeZone type = zonesDispo.removeLast(); //
                    grille[i][j] = Zone.creerZone(i,j,type);
                    if (type == Zone.TypeZone.CRASH){
                        zoneCrash = grille[i][j];
                    }
                }
            }
        }

        int[][] posSable = { { 0, 2 }, { 1, 1 }, { 1, 3 }, { 2, 0 }, { 2, 4 }, { 3, 1 }, { 3, 3 }, { 4, 2 } };

        for (int[] pos : posSable) {
            this.ajouterSable(grille[pos[0]][pos[1]], 1);
        }
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

    public void action_desert() {
        double tirage = Math.random();
        if (tirage <= 0.33) {
            double tirage_dir = Math.random();
            int force = (int) (Math.random() * 3) + 1;
            String direction = "";
            if (tirage_dir <= 0.25) {
                direction = "nord";
                leVentSouffle(direction, force);
            } else if (tirage_dir <= 0.5) {
                direction = "sud";
                leVentSouffle(direction, force);
            } else if (tirage_dir <= 0.75) {
                direction = "ouest";
                leVentSouffle(direction, force);
            } else {
                direction = "est";
                leVentSouffle(direction, force);
            }
        } else if (tirage <= 0.66) {
            LaTempeteSeDechaine();
        } else {
            vagueDeChaleur();
        }
        this.notifyObservers();
    }

    public void leVentSouffle(String direction, int force) {

        for (int k = 0; k < force; k++) {
            int iCible = oeilI;
            int jCible = oeilJ;

            if (direction == "nord") {
                iCible += 1;
            } else if (direction == "ouest") {
                jCible += 1;
            } else if (direction == "est") {
                jCible -= 1;
            } else {
                iCible -= 1;
            }

            if (iCible < 0 || iCible >= 5 || jCible < 0 || jCible >= 5) {
                break;
            }

            Zone tuileAspiree = grille[iCible][jCible];

            this.ajouterSable(tuileAspiree, 1);

            grille[oeilI][oeilJ] = tuileAspiree;
            tuileAspiree.set_position(oeilI, oeilJ);
            grille[iCible][jCible] = null;

            oeilI = iCible;
            oeilJ = jCible;
        }
    }

    public void vagueDeChaleur() {
        for (Joueur j : joueurs) {
            j.vagueDeChaleur();
        }
    }

    public void LaTempeteSeDechaine() {
        this.niveauTempete += 0.5;
    }

    public int getOeilI() {
        return oeilI;
    }

    public int getOeilJ() {
        return oeilJ;
    }

    public boolean ajouterSable(Zone zone, int quantite) {
        if (totalSable + quantite > SABLE_MAX) {
            return false; // A modifier pour fin de partie
        }
        zone.ajouterSable(quantite);
        totalSable += quantite;
        return true;
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

}
