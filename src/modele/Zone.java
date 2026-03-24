package modele;
class position {
    private int x;
    private int y;
    public position(int i, int j){
        this.x = i;
        this.y = j;
    }
}

public class Zone {
    private int nbSable;
    private boolean oeilTempete = false;
    private position pos;

    // Constructeur Zone
    public Zone(int x, int y) {
        this.nbSable = 0;
        this.pos = new position(x,y);
    }

    public void set_oeilTempete(boolean estOeil){
        this.oeilTempete = estOeil;
    }

    public boolean est_oeilTempete(){
        return this.oeilTempete;
    }

    // Ajoute du sable selon une quantité
    public void ajouterSable(int nbQuantite) {
        if (!this.oeilTempete)
            nbSable += nbQuantite;
    }

    public int getNbSable(){
        return this.nbSable;
    }

    // Format String d'une Zone
    public String toString() {
        if (this.oeilTempete){
            return "X";
        }
        return String.valueOf(nbSable);
    }
}

