package modele;
import java.util.List;
import java.util.ArrayList;
/**class position {
    private int x;
    private int y;
    public position(int i, int j){
        this.x = i;
        this.y = j;
    }
}
 **/

public class Zone {
    private int nbSable = 0;
    private int x;
    private int y;
    private List<Joueur> joueurs = new ArrayList<>();

    // Constructeur Zone
    public Zone(int i, int j) {
        this.x = i;
        this.y = j;
    }

    public void set_position(int x, int y) {
        this.x = x;
        this.y = y;
    }
    public int getX(){return this.x;}
    public int getY(){return this.y;}

    public void ajouterJoueur(Joueur j) { joueurs.add(j); }
    public void retirerJoueur(Joueur j) { joueurs.remove(j); }

    public List<Joueur> getJoueurs() { return joueurs; }



    // Ajoute du sable selon une quantité
    public void ajouterSable(int nbQuantite) {
        nbSable += nbQuantite;
    }

    public int getNbSable(){
        return this.nbSable;
    }
    public boolean estAccessible() {
        return this.nbSable <=1;
    }
    // Format String d'une Zone
    public String toString() {
        return String.valueOf(nbSable);
    }
}

