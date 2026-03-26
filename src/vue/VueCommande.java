package vue;

import javax.swing.*;
import java.awt.*;

public class VueCommande extends JPanel{
    private JButton Bouton_fin_tour;
    private JButton Bouton_se_deplacer;

    public VueCommande(){
        this.setBackground(Color.LIGHT_GRAY);
        this.Bouton_fin_tour = new JButton("Fin de tour");
        this.Bouton_se_deplacer =  new JButton("Se déplacer");
        this.add(this.Bouton_fin_tour);
        this.add(this.Bouton_se_deplacer);
    }

    public JButton getBouton_fin_tour(){
        return this.Bouton_fin_tour;
    }
    public JButton getBouton_se_deplacer(){
        return this.Bouton_se_deplacer;
    }

}
