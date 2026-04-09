package vue;
import modele.Plateau;
import javax.swing.*;
import java.awt.*;



public class VueInfoGlobales  extends JPanel implements Observer {
    private Plateau plateau;
    private JLabel labelTempete;
    private JLabel labelSable;
    private JLabel labelJoueurCourant;
    public VueInfoGlobales(Plateau plateau) {
        this.plateau = plateau;
        plateau.addObserver(this);
        this.setBackground(Theme.FOND_PANNEAU);
        this.setLayout(new FlowLayout(FlowLayout.CENTER, 60, 10));

        labelTempete = new JLabel("NNiveau de la Tempête : ?");
        labelTempete.setForeground(Theme.TEXTE_CLAIR);
        labelTempete.setFont(Theme.SOUS_TITRE);

        labelJoueurCourant = new JLabel("Au tour de ");
        labelJoueurCourant.setForeground(Theme.TEXTE_CLAIR);
        labelJoueurCourant.setFont(Theme.TITRE);

        labelSable = new JLabel("⏳ Tonnes de sable : ");
        labelSable.setForeground(Theme.TEXTE_CLAIR);
        labelSable.setFont(Theme.SOUS_TITRE);

        this.add(labelTempete);
        this.add(labelJoueurCourant);
        this.add(labelSable);
        this.update();
    }
    @Override
    public void update() {
        labelTempete.setText("🌪️ Niveau de la Tempête : " + plateau.getNiveauTempete() + "/ 5");
        labelSable.setText("⏳ Tonnes de sable : " + plateau.getTotalSable() + " / 48");
        String nom = "";
        if(!this.plateau.getJoueurs().isEmpty()) {
            nom = this.plateau.getJoueurs().get(this.plateau.getIndiceJoueurActif()).getNom();
        }
        labelJoueurCourant.setText("Au tour de : " + nom);

    }
}
