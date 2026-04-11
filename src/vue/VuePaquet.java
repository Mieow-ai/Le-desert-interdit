package vue;

import modele.CarteTempete;
import modele.Paquet;
import modele.PaquetTempete;
import modele.Plateau;

import javax.swing.*;

public abstract class VuePaquet<T> extends JPanel implements Observer {
    private final Plateau plateau;
    protected final Paquet<T> paquet;

    public VuePaquet(Plateau plateau, Paquet<T> paquet ) {
        plateau.addObserver(this);
        this.plateau = plateau;
        this.paquet = paquet;
    }
    @Override
    public abstract void update();
}


class VuePaquetTempete extends VuePaquet<CarteTempete> {

    public VuePaquetTempete(Plateau plateau, PaquetTempete paquet) {
        super(plateau, paquet);
    }

    @Override
    public void update() {

    }
}

