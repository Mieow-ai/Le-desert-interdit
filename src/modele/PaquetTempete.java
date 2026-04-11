package modele;

public class PaquetTempete extends Paquet<CarteTempete> {

    @Override
    protected void creerPaquet() {
       // Il y a 3 cartes la tempête se déchaine
        for (int i = 0; i < 3; i++) {
            pioche.add(new CarteTempete(CarteTempete.Type.DECHAINE, CarteTempete.Direction.NODIRECTION,0));
        }
        // Il y a 4 cartes vague de chaleur
        for (int i = 0; i < 4; i++) {
            pioche.add(new CarteTempete(CarteTempete.Type.CHALEUR, CarteTempete.Direction.NODIRECTION,0));
        }
        // Le reste des cartes -> le vent souffle (31-7=24) avec direction et force aléatoire
        for (int i = 0; i < 24; i++) {
            double tirage_dir = Math.random();
            int force = (int) (Math.random() * 3) + 1;
            CarteTempete.Direction direction;
            if (tirage_dir <= 0.25) {
                direction = CarteTempete.Direction.NORD;
            } else if (tirage_dir <= 0.5) {
                direction = CarteTempete.Direction.SUD;
            } else if (tirage_dir <= 0.75) {
                direction = CarteTempete.Direction.EST;
            } else {
                direction = CarteTempete.Direction.OUEST;
            }
            pioche.add(new CarteTempete(CarteTempete.Type.VENT, direction, force));

        }
    }
}

