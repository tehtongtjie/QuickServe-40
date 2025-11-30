package menu.border;

import app.Theme;
import java.awt.*;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.border.LineBorder;

public class RoundedCornerBorder extends LineBorder {
    private final int radius;

    public RoundedCornerBorder(int radius) {
        // pakai warna border dari Theme, sama seperti sebelumnya
        super(Theme.BORDER_COLOR, 1);
        this.radius = radius;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // isi background biar efek kartu-nya halus
        g2.setColor(c.getBackground());
        g2.fillRoundRect(x, y, width, height, radius, radius);

        // garis pinggir
        g2.setColor(getLineColor());
        g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);

        g2.dispose();
    }
}
