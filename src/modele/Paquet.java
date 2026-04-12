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
        T carte = pioche.remove(0);
        defausse.add(carte);

        return carte;
    }
    public T regarderDefausse(){
        if(defausse.isEmpty()){
            return null;
        } else {
            return defausse.get(defausse.size() - 1);
        }
    }
}
