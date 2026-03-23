package vue;

import javax.swing.*;
import java.awt.*;
import modele.Plateau;

public class VuePlateau extends JPanel {
    public VuePlateau(Plateau plateau){
        this.setLayout(new GridLayout(5, 5, 5, 5));
        this.setBackground(Color.GRAY);

        for(int i = 0;i<5;i++){
            for(int j = 0;j<5;j++){
                JPanel cp = new JPanel();
                cp.setPreferredSize(new Dimension(100, 100));
                cp.setLayout(new GridBagLayout());

                int quantite_sable = plateau.getZone(i, j).getNbSable();
                JLabel sable_label = new JLabel(quantite_sable + "sable(s)");
                sable_label.setFont(new Font("Arial", Font.BOLD, 14));
                if (plateau.getZone(i, j).est_oeilTempete()){
                    cp.setBackground(Color.BLUE);
                    sable_label.setForeground(Color.WHITE);
                }
                else{
                    cp.setBackground(new Color(177, 141, 54));
                    sable_label.setForeground(Color.BLACK);
                }
                cp.add(sable_label);
                this.add(cp);
            }
        }
    }
}
