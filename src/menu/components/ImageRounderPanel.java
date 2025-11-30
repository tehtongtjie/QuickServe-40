package menu.components;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import javax.swing.*;

/**
 * Panel untuk menampilkan gambar dengan sudut membulat.
 * Bisa memilih sisi mana yang rounded.
 */
public class ImageRounderPanel extends JPanel {

    private Image image;
    private int cornerRadius;
    private boolean topLeft;
    private boolean topRight;
    private boolean bottomLeft;
    private boolean bottomRight;

    public ImageRounderPanel(int radius,
                             boolean topLeft,
                             boolean topRight,
                             boolean bottomLeft,
                             boolean bottomRight) {
        this.cornerRadius = radius;
        this.topLeft = topLeft;
        this.topRight = topRight;
        this.bottomLeft = bottomLeft;
        this.bottomRight = bottomRight;

        setOpaque(false);
    }

    /** Set image to display */
    public void setImage(ImageIcon img) {
        if (img != null) {
            this.image = img.getImage();
            repaint();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image == null) return;

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // Draw rounded clip
        Shape clip = createRoundShape(w, h);
        g2.setClip(clip);

        g2.drawImage(image, 0, 0, w, h, this);
        g2.dispose();
    }

    /**
     * Membuat bentuk rounded dengan opsi sisi mana yang rounded.
     */
    private Shape createRoundShape(int w, int h) {
        int r = cornerRadius;

        int tl = topLeft ? r : 0;
        int tr = topRight ? r : 0;
        int bl = bottomLeft ? r : 0;
        int br = bottomRight ? r : 0;

        return new RoundRectangle2D.Float(0, 0, w, h, r, r);
    }
}
