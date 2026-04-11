package modele;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Paquet<T> {
    protected List<T> pioche = new ArrayList<>();
    protected List<T> defausse = new ArrayList<>();

    public Paquet() {
        creerPaquet();
        // Mélanger le paquet
        Collections.shuffle(pioche);
    }

    protected abstract void creerPaquet();

    public T piocher() {
        if (pioche.isEmpty()) {
            pioche.addAll(defausse);
            defausse.clear();
            Collections.shuffle(pioche);
        }
        return pioche.remove(0); // Retire et renvoie la première carte
    }


    public void defausser(T carte) {
        defausse.add(carte);
    }
}
