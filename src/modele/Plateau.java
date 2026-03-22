package modele;

public class Plateau {
    private Zone[][] grille;
    private int niveauTempete;

    public Plateau() {
        this.grille = new Zone[5][5];
        this.niveauTempete = 2;
        initialiseGrille();
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
}
