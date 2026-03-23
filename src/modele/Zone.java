package modele;

public class Zone {
    private int nbSable;
    private boolean oeilTempete = false;

    // Constructeur Zone
    public Zone() {
        this.nbSable = 0;
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

    // Format String d'une Zone
    public String toString() {
        if (this.oeilTempete){
            return "X";
        }
        return String.valueOf(nbSable);
    }
}

