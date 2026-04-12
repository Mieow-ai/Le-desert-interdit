package modele;
public class CarteTempete {
    public enum Type {
        VENT("Vent", "Déplace l'œil du cyclone d'un nombre de cases défini par la force de la carte." +
                "Toutes les tuiles survolées reçoivent +1 marqueur de sable. Si une case dépasse 1 sable, elle devient bloquée."),
        CHALEUR("Chaleur", "Chaque joueur perd immédiatement 1 unité d'eau." +
                "Exception : Les joueurs situés dans un tunnel révélé ou protégés par un \"Bouclier Solaire\" ne perdent rien."),
        DECHAINE("Dechaine", "Fait progresser le niveau de la Tempête de 0.5." +
                "Cela augmente le nombre de cartes que vous devrez piocher à la fin de chaque tour futur.");

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
    public enum Direction {NORD, SUD, EST, OUEST, NODIRECTION};
    private final Type type;
    private final Direction direction;
    private final int force;

    public CarteTempete(Type type, Direction direction, int force) {
        this.type = type;
        this.direction = direction;
        this.force = force;
    }
    public String getDescription(){
        return type.getDescription();

    }
    public Type getType() {
        return type;
    }

    public Direction getDirection() {
        return direction;
    }

    public int getForce() {
        return force;
    }
    @Override
    public String toString() {
        return type.toString();
    }

}