package vue;

import javax.swing.*;
import java.awt.*;

public class VuePiece extends JPanel {
    private Image image;

    public VuePiece(String path, int width, int height) {
        this.image = new ImageIcon(path).getImage();
        this.setOpaque(false); // Transparent !
        this.setPreferredSize(new Dimension(width, height));
    }

    public void setImage(String path) {
        this.image = new ImageIcon(path).getImage();
        this.repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image != null) {
            g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
