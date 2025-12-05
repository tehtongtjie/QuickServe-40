package menu.border;

import java.awt.*;
import javax.swing.border.LineBorder;

// garis tepi
public class RoundedBorder extends LineBorder {
    private final int radius;

    public RoundedBorder(Color color, int radius) {
        super(color, 1); // Memanggil konstruktor induk (LineBorder)
        this.radius = radius;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(getLineColor()); // Menggunakan warna dari LineBorder
        g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
        g2.dispose();
    }

    // padding border
    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(radius / 2 + 1, radius / 2 + 1, radius / 2 + 1, radius / 2 + 1);
    } // insets: objek yang menyimpan nilai margin
    @Override
    public Insets getBorderInsets(Component c, Insets insets) {
        insets.left = insets.right = insets.bottom = insets.top = radius / 2 + 1;
        return insets;
    }
}
