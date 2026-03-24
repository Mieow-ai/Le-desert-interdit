package modele;

import java.lang.Math.*;
import vue.Observable;

public class Plateau extends Observable {
    private Zone[][] grille;
    private double niveauTempete;

    public Plateau() {
        super();
        this.grille = new Zone[5][5];
        this.niveauTempete = 2.0;
        initialiseGrille();
    }

    public Zone getZone(int i, int j){
        return grille[i][j];
    }

    // Initialise la Grille/Plateau
    public void initialiseGrille(){
        for(int i = 0;i<5;i++) {
            for (int j = 0; j < 5; j++) {
                grille[i][j] = new Zone();
            }
        }
        grille[2][2].set_oeilTempete(true);

        int[][] posSable = {{0,2}, {1,1}, {1,3}, {2,0}, {2,4}, {3,1}, {3,3}, {4,2}};

        for( int[] pos : posSable){
            grille[pos[0]][pos[1]].ajouterSable(1);
        }
    }

    // Convertisseur en String
    @Override
    public String toString() {
        String res = "";
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                res += grille[i][j].toString() + " ";
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
        int x_oeil = 0;
        int y_oeil = 0;

        for(int i = 0;i<5;i++){
            for(int j = 0;j<5;j++){
                if(this.grille[i][j].est_oeilTempete()){
                    x_oeil = j;
                    y_oeil = i;
                }
            }
        }
        for(int i = 0;i<force;i++){
            int x_cible = x_oeil;
            int y_cible = y_oeil;

            if(direction == "nord") {y_cible += 1;}
            else if (direction == "ouest") {x_cible += 1;}
            else if (direction == "est") {x_cible -= 1;}
            else { y_cible -= 1;}

            if(x_cible < 0 || x_cible >= 5 || y_cible < 0 || y_cible >= 5){
                break;
            }

            Zone tuileAspirer = grille[y_cible][x_cible];
            Zone tuileTrou = grille[y_oeil][x_oeil];

            tuileAspirer.ajouterSable(1);

            grille[y_oeil][x_oeil] = tuileAspirer;
            grille[y_cible][x_cible] = tuileTrou;

            x_oeil = x_cible;
            y_oeil = y_cible;
        }
    }

    public void vagueDeChaleur(){
        return;
    }

    public void LaTempeteSeDechaine(){
        this.niveauTempete += 0.5;
    }
}
