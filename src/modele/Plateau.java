package modele;

import java.lang.Math.*;
import vue.Observable;

public class Plateau extends Observable {
    private Zone[][] grille;
    private double niveauTempete = 2.0;
    private int oeilI = 2;
    private int oeilJ = 2;
    private int totalSable = 0;
    private static final int SABLE_MAX = 48;
    public Plateau() {
        super();
        this.grille = new Zone[5][5];
        initialiseGrille();
    }

    public Zone getZone(int i, int j){
        return grille[i][j];
    }

    // Initialise la Grille/Plateau
    public void initialiseGrille(){
        for(int i = 0;i<5;i++) {
            for (int j = 0; j < 5; j++) {
                if (i == oeilI && j == oeilJ) {
                    grille[i][j] = null;  // l'oeil
                } else {
                  grille[i][j] = new Zone(i,j);
                }
            }
        }

        int[][] posSable = {{0,2}, {1,1}, {1,3}, {2,0}, {2,4}, {3,1}, {3,3}, {4,2}};

        for( int[] pos : posSable){
            this.ajouterSable(grille[pos[0]][pos[1]],1);
        }
    }

    // Convertisseur en String
    @Override
    public String toString() {
        String res = "";
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                if( i == oeilI && j == oeilJ){
                    res += "X ";
                } else {
                    res += grille[i][j].toString() + " ";
                }
            }
            res += "\n";
        }
        return res;
    }

    public void action_desert(){
        double tirage = Math.random();
        if (tirage <= 0.33){
            double tirage_dir = Math.random();
            int force = (int)(Math.random() * 3) + 1;
            String direction ="";
            if(tirage_dir <= 0.25){
                direction = "nord";
                leVentSouffle(direction, force);
            }
            else if(tirage_dir <= 0.5){
                direction = "sud";
                leVentSouffle(direction, force);
            }
            else if(tirage_dir <= 0.75){
                direction = "ouest";
                leVentSouffle(direction, force);
            }
            else {
                direction = "est";
                leVentSouffle(direction, force);
            }
        }
        else if (tirage <= 0.66){
            LaTempeteSeDechaine();
        }
        else{
            vagueDeChaleur();
        }
        this.notifyObservers();
    }

    public void leVentSouffle(String direction, int force){

        for(int k = 0;k<force;k++){
            int iCible = oeilI;
            int jCible = oeilJ;

            if(direction == "nord") {iCible += 1;}
            else if (direction == "ouest") {jCible += 1;}
            else if (direction == "est") {jCible -= 1;}
            else { iCible -= 1;}

            if(iCible < 0 || iCible >= 5 || jCible < 0 || jCible >= 5){
                break;
            }

            Zone tuileAspiree = grille[iCible][jCible];

            this.ajouterSable(tuileAspiree,1);

            grille[oeilI][oeilJ] = tuileAspiree;
            tuileAspiree.set_position(oeilI,oeilJ);
            grille[iCible][jCible] = null;

            oeilI = iCible;
            oeilJ = jCible;
        }
    }

    public void vagueDeChaleur(){
        return;
    }

    public void LaTempeteSeDechaine(){
        this.niveauTempete += 0.5;
    }

    public int getOeilI() { return oeilI; }
    public int getOeilJ() { return oeilJ; }
    public boolean ajouterSable(Zone zone, int quantite) {
        if (totalSable + quantite > SABLE_MAX) {
            return false; // A modifier pour fin de partie
        }
        zone.ajouterSable(quantite);
        totalSable += quantite;
        return true;
    }

}
