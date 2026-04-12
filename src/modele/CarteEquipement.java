package modele;

public class CarteEquipement {

    public Type getType() {
        return type;
    }

    public enum Type {
        BLASTER_DES_DUNES("Blaster", "Permet de retirer tout le sable d'une case" +
                "cela ne coûte pas d'action et nettoie la case d'un seul coup, même s'il y a 4 ou 5 jetons de sable."),
        JETPACK("Jetpack", "Permet à un joueur de se déplacer sur n'importe quelle case du plateau" +
                "Très utile pour sauver un allié ou atteindre la case de décollage"),
        BOUCLIER_SOLAIRE("Bouclier Solaire", "Protège tous les joueurs présents sur ta case contre la prochaine Vague de Chaleur" +
                "Tant que le bouclier est actif, personne sur la case ne perd d'eau."),
        TERRASCOPE("Terrascope", "Permet de regarder secrètement ce qui se cache sous une tuile non révélée n'importe où sur le plateau." +
                "Cela permet d'anticiper la position des indices ou des oasis sans avoir à se déplacer et creuser."),
        ACCELERATEUR_DE_TEMPS("Accélérateur", "Accorde immédiatement 2 actions supplémentaires au joueur qui l'utilise pour le tour en cours." +
                "C'est l'item de la dernière chance pour finir d'assembler la machine ou désensabler une case critique."),
        RESERVE_D_EAU("Réserve d'eau", "Redonne immédiatement 2 unités d'eau à tous les joueurs situés sur la même case que l'utilisateur.");

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
