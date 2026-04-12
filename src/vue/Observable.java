package vue;

import java.util.ArrayList;
import vue.Observer;

/**
 * Classe abstraite Observable
 * Une classe qui hérite d'observable va être capable d'être "écoutée"
 * par d'autres objets comme les Observeurs
 * Dès que les données de l'Observable changent, il appelle notifyObservers()
 * pour forcer les observeurs à agir.
 * Prise dans le cours
 */
public abstract class Observable {
    private ArrayList<Observer> observers;

    /**
     * Constructeur par défaut.
     */
    public Observable(){
        this.observers = new ArrayList<Observer>();
    }

    /**
     * Ajoute un nouvel observateur à cet objet.
     * @param o L'observateur (généralement une Vue)
     */
    public void addObserver(Observer o){
        observers.add(o);
    }

    /**
     * Déclenche une notification globale.
     * Ça parcourt la liste de tous les observateurs ajoutés et appelle leur méthode update()
     */
    public void notifyObservers(){
        for(Observer o : observers){
            o.update();
        }
    }
}
