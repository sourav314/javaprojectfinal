package mccd;

import java.awt.*;
import javax.swing.*;

// RoundedButton class remains the same
class RoundedButton extends JButton {
    private Color hoverBackgroundColor;
    private Color pressedBackgroundColor;

    public RoundedButton(String text) {
        super(text);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setForeground(Color.WHITE);
        setFont(new Font("Arial", Font.BOLD, 16));
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setOpaque(false);

        setBackground(new Color(33, 150, 243));
        hoverBackgroundColor = getBackground().brighter();
        pressedBackgroundColor = getBackground().darker();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Color c = getBackground();
        if (getModel().isArmed()) c = pressedBackgroundColor;
        else if (getModel().isRollover()) c = hoverBackgroundColor;

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(c);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
        g2.dispose();
        super.paintComponent(g);
    }
}

// ImagePanel loads image directly from file system
class ImagePanel extends JPanel {
    private final Image img;
    private final long startTime;
    private static final double ANIMATION_SPEED = 0.8;

    public ImagePanel() {
        setLayout(null);
        setDoubleBuffered(true);
        // Load image directly from file path
        img = new ImageIcon("src/image/logo.png").getImage(); 
        startTime = System.nanoTime();
        new Timer(16, e -> repaint()).start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (img != null) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            double elapsed = (System.nanoTime() - startTime) / 1_000_000_000.0;
            float alpha = (float) ((1 + Math.cos(elapsed * ANIMATION_SPEED)) / 2.0);

            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

            int x = (getWidth() - img.getWidth(this)) / 2;
            int y = (getHeight() - img.getHeight(this)) / 2 - 100;

            g2d.drawImage(img, x, y, this);
            g2d.dispose();
        }
    }
}

// Main MCCD frame
public class MCCD extends JFrame {

    public MCCD() {
        setTitle("CarDeX Dealerships");
        setSize(800, 600);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        ImagePanel panel = new ImagePanel();
        panel.setBackground(new Color(245, 245, 245));

        JLabel description = new JLabel(
            "<html><div style='text-align: center; width: 800px;'>"
            + "<h2 style='color:#212121;'>CarDeX</h2>"
            + "<p style='font-size:16px; color:#424242;'>"
            + "A secure offline system to manage car inventories across dealerships in real time, "
            + "with role-based access for fast, accurate, and efficient operations."
            + "</p></div></html>",
            SwingConstants.CENTER
        );
        description.setFont(new Font("Arial", Font.PLAIN, 18));
        description.setForeground(new Color(33, 33, 33));

        RoundedButton loginBtn = new RoundedButton("Login");
        loginBtn.setBackground(new Color(33, 150, 243));

        RoundedButton signupBtn = new RoundedButton("Sign Up");
        signupBtn.setBackground(new Color(76, 175, 80));

        // Open login/signup windows
        loginBtn.addActionListener(e -> {
            LoginWindow login = new LoginWindow(this);
            login.setVisible(true);
        });

        signupBtn.addActionListener(e -> {
            SignUpWindow signup = new SignUpWindow(this);
            signup.setVisible(true);
        });

        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        description.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        signupBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(Box.createVerticalStrut(400));
        panel.add(description);
        panel.add(Box.createRigidArea(new Dimension(0, 40)));
        panel.add(loginBtn);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(signupBtn);
        panel.add(Box.createVerticalGlue());

        add(panel);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MCCD frame = new MCCD();
            frame.setVisible(true);
        });
    }
}
