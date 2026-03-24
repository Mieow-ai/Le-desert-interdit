package controleur;
import modele.Joueur;
import modele.Plateau;
import vue.FenetreJeu;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Controleur implements ActionListener {
    private Plateau p;
    private FenetreJeu vue;

    public Controleur(Plateau modele, FenetreJeu vue){
        this.p = modele;
        this.vue = vue;

        this.vue.getVc().getBouton_fin_tour().addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e){
        System.out.println("Le désert s'agite...");
        this.p.action_desert();
        //vue.getVp().dessinerPlateau();
    }

}
