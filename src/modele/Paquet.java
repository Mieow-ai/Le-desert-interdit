package modele;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Classe abstraite représentant un paquet (possibilité de piocher, regarder la défausse (pour l'affichage))
 * Classe étendue par PaquetEquipement avec le type CarteEquipement et par PaquetTempete avec le type CarteTempete
 * @param <T>
 */
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
        // Retire la dernière carte pour améliorer complexité (pas besoin de tout copier en décalant)
        T carte = pioche.remove(pioche.size()-1);
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
