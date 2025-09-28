package mccd;

import javax.swing.*;
import java.awt.*;

public class RoundedTextField extends JTextField {

    private Color borderColor = new Color(200, 200, 200);
    private int radius = 15;

    public RoundedTextField(int columns) {
        super(columns);
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        setFont(new Font("Arial", Font.PLAIN, 14));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Background
        g2.setColor(Color.WHITE);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);

        // Border
        if (isFocusOwner()) {
            g2.setColor(new Color(33, 150, 243)); // highlight on focus
        } else {
            g2.setColor(borderColor);
        }
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, radius, radius);

        g2.dispose();
        super.paintComponent(g);
    }
}
