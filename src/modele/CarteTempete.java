package modele;
public class CarteTempete {
    public enum Type { VENT, CHALEUR, DECHAINE }
    public enum Direction {NORD, SUD, EST, OUEST, NODIRECTION};
    private final Type type;
    private final Direction direction;
    private final int force;

    public CarteTempete(Type type, Direction direction, int force) {
        this.type = type;
        this.direction = direction;
        this.force = force;
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