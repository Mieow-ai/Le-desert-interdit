package vue;


/**
 * L'interface Observer définit les objets qui souhaitent "écouter" un autre objet (Observable)
 * Dans le MVC, les Vues sont des Observers, tandis que les éléments du modèle sont des Observable.
 */
public interface Observer {
    void update();
}

