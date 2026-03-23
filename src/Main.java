import controleur.Controleur;
import modele.Plateau;
import vue.FenetreJeu;

public class Main {
    public static void main(String[] args){

        Plateau p = new Plateau();
        System.out.println("--- État initial du désert ---");
        System.out.println(p.toString());

        FenetreJeu f = new FenetreJeu(p);
        Controleur controleur = new Controleur(p, f);
        f.setVisible(true);
    }
}
