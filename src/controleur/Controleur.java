package controleur;

import modele.Joueur;
import modele.Plateau;
import modele.Zone;
import vue.FenetreJeu;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class Controleur {
    private Plateau p;
    private FenetreJeu vue;
    private List<Joueur> joueurs;
    private int indiceJoueurActif = 0;

    public Controleur(Plateau modele, FenetreJeu vue) {
        this.p = modele;
        this.vue = vue;
        this.joueurs = modele.getJoueurs();

        // Bouton fin de tour
        this.vue.getVc().getBouton_fin_tour().addActionListener(e -> {
            System.out.println("Le désert s'agite...");
            this.p.action_desert();
        });

        // Bouton se déplacer
        this.vue.getVc().getBouton_se_deplacer().addActionListener(e -> {
            Joueur actif = this.joueurs.get(this.indiceJoueurActif);
            p.setZonesAccessibles(actif.getZonesAccessibles(p));
        });

        // On défini l'action réalisé par le click sur une case
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                final int fi = i, fj = j;
                vue.getVp().getCase(i, j).addMouseListener(new MouseAdapter() {
                    public void mouseClicked(MouseEvent e) {
                        Zone zone = p.getZone(fi, fj);
                        if (zone != null && p.getZonesAccessibles().contains(zone)) {
                            joueurs.get(indiceJoueurActif).setZone(zone);
                            p.setZonesAccessibles(new ArrayList<>());
                        }
                    }
                });
            }
        }
    }
}
