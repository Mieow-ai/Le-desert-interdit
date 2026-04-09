package modele;

public enum Equipement {
    BLASTER_DES_DUNES("Blaster"),
    JETPACK("Jetpack"),
    BOUCLIER_SOLAIRE("Bouclier Solaire"),
    TERRASCOPE("Terrascope"),
    ACCELERATEUR_DE_TEMPS("Accélérateur"),
    RESERVE_D_EAU("Réserve d'eau");
    private final String nom_aff;

    Equipement(String nom_aff){
        this.nom_aff = nom_aff;
    }

    @Override
    public String toString(){
        return nom_aff;
    }
}
