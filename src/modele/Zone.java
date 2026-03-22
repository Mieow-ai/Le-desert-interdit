package modele;

public class Zone {
    private int nbSable;
    private boolean oeilTempete;

    // Constructeur Zone
    public Zone(boolean estOeil) {
        this.oeilTempete = estOeil;
        this.nbSable = 0;
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

