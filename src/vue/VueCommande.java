package vue;

import javax.swing.*;
import java.awt.*;

public class VueCommande extends JPanel{
    private JButton Bouton_fin_tour;

    public VueCommande(){
        this.setBackground(Color.LIGHT_GRAY);
        this.Bouton_fin_tour = new JButton("Fin de tour");
        this.add(this.Bouton_fin_tour);
    }

    public JButton getBouton_fin_tour(){
        return this.Bouton_fin_tour;
    }
}
