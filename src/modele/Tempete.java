package modele;

public class Tempete {
    private int nbZone = 1;
    private int niveau_tempete;

    public Tempete(int nv_tempete){
        this.niveau_tempete = nv_tempete;
    }

    public void augmenter_niveau(int nb_niveau){
        this.niveau_tempete += nb_niveau;
    }
}
