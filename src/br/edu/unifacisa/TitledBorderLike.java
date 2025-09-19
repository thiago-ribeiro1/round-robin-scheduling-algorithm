package br.edu.unifacisa;

import javax.swing.*;
import java.awt.*;

public class TitledBorderLike extends javax.swing.border.AbstractBorder {
    private final String title;
    private final Insets insets = new Insets(24, 10, 10, 10);

    public TitledBorderLike(String title) {
        this.title = title;
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return insets;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
        // Desenha uma borda personalizada
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(new Color(255, 255, 255, 40));
        g2.drawRoundRect(x + 1, y + 8, w - 3, h - 10, 10, 10);
        g2.setFont(c.getFont().deriveFont(Font.BOLD, 12f));
        g2.setColor(new Color(230, 230, 230));
        g2.drawString(title, x + 12, y + 20);
        g2.dispose();
    }
}
