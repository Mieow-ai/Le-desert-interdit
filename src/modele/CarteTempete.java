package modele;
public class CarteTempete {
    public enum Type {
        VENT("Vent", ""),
        CHALEUR("Chaleur", ""),
        DECHAINE("Dechaine", "");

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

}