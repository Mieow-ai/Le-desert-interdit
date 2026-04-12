package modele;

public class CarteEquipement {

    public Type getType() {
        return type;
    }

    public enum Type {
        BLASTER_DES_DUNES("Blaster", ""),
        JETPACK("Jetpack", ""),
        BOUCLIER_SOLAIRE("Bouclier Solaire", ""),
        TERRASCOPE("Terrascope", ""),
        ACCELERATEUR_DE_TEMPS("Accélérateur", ""),
        RESERVE_D_EAU("Réserve d'eau", "");

        private final String nom_aff;
        private final String description;

        Type(String nom_aff, String description) {
            this.nom_aff = nom_aff;
            this.description = description;
        }

        @Override
        public String toString() {
            return nom_aff;
        }

        public String getDescription() {
            return description;
        }
    }

    private final Type type;

    public CarteEquipement(Type type) {
        this.type = type;
    }

    public String getDescription() {
        return type.getDescription();
    }

    @Override
    public String toString() {
        return type.toString();
    }
}
