package vue;

import javax.swing.*;
import java.awt.*;
import vue.Observer;
import modele.Plateau;

public class VuePlateau extends JPanel implements Observer {
    private Plateau plateau;

    public VuePlateau(Plateau plateau){
        this.plateau = plateau;
        this.setLayout(new GridLayout(5, 5, 5, 5));
        this.setBackground(Color.GRAY);
        dessinerPlateau();
        this.plateau.addObserver(this);
    }

    public void dessinerPlateau(){
        this.removeAll();
        for(int i = 0;i<5;i++){
            for(int j = 0;j<5;j++){
                JPanel cp = new JPanel();
                cp.setPreferredSize(new Dimension(100, 100));
                cp.setLayout(new GridBagLayout());

                int quantite_sable = this.plateau.getZone(i, j).getNbSable();
                JLabel sable_label = new JLabel(quantite_sable + "sable(s)");
                sable_label.setFont(new Font("Arial", Font.BOLD, 14));
                if (this.plateau.getZone(i, j).est_oeilTempete()){
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
        this.revalidate();
        this.repaint();
    }

    public void update(){
        dessinerPlateau();
    }
}
