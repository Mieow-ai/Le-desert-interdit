package modele;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.Math.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import vue.Observable;

import javax.swing.*;

public class Plateau extends Observable {
    private Zone[][] grille;
    private double niveauTempete = 2.0;
    private int oeilI = 2;
    private int oeilJ = 2;
    private int totalSable = 0;
    private static final int SABLE_MAX = 48;
    private List<Zone> zonesAccessibles = new ArrayList<>();
    private List<Joueur> joueurs = new ArrayList<>();
    private Zone zoneCrash;
    private int indiceJoueurActif = 0;

    public double getNiveauTempete() {
        return niveauTempete;
    }

    public int getTotalSable() {
        return totalSable;
    }

    public enum EtatPartie {
        EN_COURS, VICTOIRE, DEFAITE
    };

    private EtatPartie etat = EtatPartie.EN_COURS;
    private String raison_fin = "";
    List<Zone.TypeZone> pieces_ramassees = new ArrayList<>();

    public Zone getZoneCrash() {
        return this.zoneCrash;
    }

    public void add_piece(Zone.TypeZone obj) {
        this.pieces_ramassees.add(obj);
    }

    public int getIndiceJoueurActif() {
        return this.indiceJoueurActif;
    }

    public Plateau() {
        super();
        this.grille = new Zone[5][5];
        initialiseGrille();
    }

    public List<Joueur> getJoueurs() {
        return joueurs;
    }

    public void add_joueur(Joueur j) {
        joueurs.add(j);
        notifyObservers();

    }

    public Zone getZone(int i, int j) {
        return grille[i][j];
    }

    public void verifier_victoire() {
        boolean tous_au_decollage = true;
        for (Joueur j : this.joueurs) {
            if (j.getZone().getType() != Zone.TypeZone.DECOLLAGE) {
                tous_au_decollage = false;
            }
        }
        if (tous_au_decollage && this.pieces_ramassees.size() == 4) {
            this.etat = EtatPartie.VICTOIRE;
            this.raison_fin = "Vous avez gagné!";
            notifyObservers();
        }
    }

    public void finDeTour() {
        this.indiceJoueurActif = (this.indiceJoueurActif + 1) % joueurs.size();
        this.joueurs.get(indiceJoueurActif).reset_action();
        this.action_desert();

    }

    // Initialise la Grille/Plateau
    public void initialiseGrille() {
        ArrayList<Zone.TypeZone> typesZone = new ArrayList<>(Arrays.asList(Zone.TypeZone.values()));
        ArrayList<Zone.TypeZone> zonesDispo = new ArrayList<>();
        // On ajoute le bon nombre de chaque zone
        for (Zone.TypeZone type : typesZone) {
            for (int i = 0; i < type.nb; i++) {
                zonesDispo.add(type);
            }
        }
        // On mélange les zones
        Collections.shuffle(zonesDispo);
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                if (i == oeilI && j == oeilJ) {
                    grille[i][j] = null; // l'oeil
                } else {
                    Zone.TypeZone type = zonesDispo.remove(zonesDispo.size() - 1); //
                    grille[i][j] = Zone.creerZone(i, j, type);
                    if (type == Zone.TypeZone.CRASH) {
                        zoneCrash = grille[i][j];
                    }
                }
            }
        }

        int[][] posSable = { { 0, 2 }, { 1, 1 }, { 1, 3 }, { 2, 0 }, { 2, 4 }, { 3, 1 }, { 3, 3 }, { 4, 2 } };

        for (int[] pos : posSable) {
            this.ajouterSable(grille[pos[0]][pos[1]], 1);
        }
    }

    // Convertisseur en String
    @Override
    public String toString() {
        String res = "";
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                if (i == oeilI && j == oeilJ) {
                    res += "X ";
                } else {
                    res += grille[i][j].toString() + " ";
                }
            }
            res += "\n";
        }
        return res;
    }

    public void action_desert() {
        int nbCartes = (int) this.niveauTempete;
        // Vérifier si le météorologue a amorti la tempête
        for (Joueur j : joueurs) {
            nbCartes -= j.getAndResetReductionTempete();
        }
        final int nbcartestimer = nbCartes;

        Timer timer = new javax.swing.Timer(700, null); // On crée un timer qui sera déclenché toutes les 700ms après le départ du timer
        timer.addActionListener(new ActionListener() {  // classe anonyme sur l'interface ActionListener
            int i = 0;
            @Override
            public void actionPerformed(ActionEvent e) {  // action effectuée à chaque déclenchement du timer
                if (i >= nbcartestimer) {
                    ((javax.swing.Timer) e.getSource()).stop(); // Le bon nombre de cartes à été tiré
                    return;
                }

                double tirage = Math.random();
                if (tirage <= 0.33) {
                    double tirage_dir = Math.random();
                    int force = (int) (Math.random() * 3) + 1;
                    String direction = "";
                    if (tirage_dir <= 0.25) {
                        direction = "nord";
                        leVentSouffle(direction, force);
                    } else if (tirage_dir <= 0.5) {
                        direction = "sud";
                        leVentSouffle(direction, force);
                    } else if (tirage_dir <= 0.75) {
                        direction = "ouest";
                        leVentSouffle(direction, force);
                    } else {
                        direction = "est";
                        leVentSouffle(direction, force);
                    }
                } else if (tirage <= 0.66) {
                    LaTempeteSeDechaine();
                } else {
                    vagueDeChaleur();
                }
                notifyObservers();
                i++;
            }
        });
        timer.start();
    }

    public void leVentSouffle(String direction, int force) {

        for (int k = 0; k < force; k++) {
            int iCible = oeilI;
            int jCible = oeilJ;

            if (direction.equals("nord")) {
                iCible += 1;
            } else if (direction.equals("ouest")) {
                jCible += 1;
            } else if (direction.equals("est")) {
                jCible -= 1;
            } else {
                iCible -= 1;
            }

            if (iCible < 0 || iCible >= 5 || jCible < 0 || jCible >= 5) {
                break;
            }

            Zone tuileAspiree = grille[iCible][jCible];

            this.ajouterSable(tuileAspiree, 1);

            grille[oeilI][oeilJ] = tuileAspiree;
            tuileAspiree.set_position(oeilI, oeilJ);
            grille[iCible][jCible] = null;

            oeilI = iCible;
            oeilJ = jCible;
        }
    }

    public void vagueDeChaleur() {
        for (Joueur j : joueurs) {
            j.vagueDeChaleur();
        }
    }

    public void LaTempeteSeDechaine() {
        this.niveauTempete += 0.5;
        if (this.niveauTempete >= 5.0) {
            this.declencher_defaite("La tempête a atteint son niveau maximal, vous vous êtes envolés..");
            notifyObservers();
        }
    }

    public int getOeilI() {
        return oeilI;
    }

    public int getOeilJ() {
        return oeilJ;
    }

    public boolean ajouterSable(Zone zone, int quantite) {
        if (totalSable + quantite > SABLE_MAX) {
            this.declencher_defaite("Mort ensablés : trop de sable");
            notifyObservers();
            return false;
        }
        zone.ajouterSable(quantite);
        totalSable += quantite;
        return true;
    }

    public void setZonesAccessibles(List<Zone> zones) {
        this.zonesAccessibles = zones;
        notifyObservers();
    }

    public void resetZonesAccessibles() {
        this.zonesAccessibles = new ArrayList<Zone>();
        notifyObservers();
    }

    public List<Zone> getZonesAccessibles() {
        return zonesAccessibles;
    }

    public EtatPartie getEtat() {
        return this.etat;
    }

    public String getRaison_fin() {
        return this.raison_fin;
    }

    public void declencher_defaite(String raison) {
        this.etat = EtatPartie.DEFAITE;
        this.raison_fin = raison;
    }

    public void verifie_apparition_obj(Zone type_indice_creuse) {
        Zone.TypeZone type = type_indice_creuse.getType();
        Zone i_1 = null;
        Zone i_2 = null;

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                Zone z = grille[i][j];

                if (z != null && z.getType() == type && z.estRevelee()) {
                    if (i_1 == null) {
                        i_1 = z;
                    } else {
                        i_2 = z;
                    }
                }
            }
        }
        if (i_1 != null && i_2 != null) {
            int boite_x = i_1.getX();
            int boite_y = i_2.getY();
            Zone intersection = getZone(boite_x, boite_y);
            if (intersection == null) {
                intersection = getZone(i_2.getX(), i_1.getY());
            }
            intersection.poser_objet(type);
            System.out.println("Une pièce de la navette (" + type + ") vient d'apparaitre !");
        }
    }
}
