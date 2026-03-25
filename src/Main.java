import controleur.Controleur;
import modele.Joueur;
import modele.Plateau;
import vue.FenetreJeu;

import java.awt.*;

public class Main {
    public static void main(String[] args){

        Plateau p = new Plateau();
        System.out.println("--- État initial du désert ---");
        System.out.println(p.toString());
        Joueur j1 = new Joueur("Alice", Color.RED);
        Joueur j2 = new Joueur("Bob", Color.GREEN);
        j1.setZone(p.getZone(0, 0));
        j2.setZone(p.getZone(1, 2));


        FenetreJeu f = new FenetreJeu(p);
        Controleur controleur = new Controleur(p, f);
        f.setVisible(true);
    }
}
