package mccd;

import javax.swing.*;
import java.awt.*;

public class CarDetailsDialog extends JDialog {

    public CarDetailsDialog(JFrame parent, CarTableItem car, double price, int stock, String company) {
        super(parent, "Car Details", true);
        setSize(400, 300);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        // === Image Panel ===
        JLabel imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        java.net.URL imgURL = getClass().getClassLoader().getResource("image/" + car.imagePath);
        if (imgURL != null) {
            ImageIcon icon = new ImageIcon(new ImageIcon(imgURL)
                    .getImage().getScaledInstance(150, 100, Image.SCALE_SMOOTH));
            imageLabel.setIcon(icon);
        }
        add(imageLabel, BorderLayout.NORTH);

        // === Details Panel ===
        JPanel detailsPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        detailsPanel.add(new JLabel("Make: " + car.make));
        detailsPanel.add(new JLabel("Model: " + car.model));
        detailsPanel.add(new JLabel("Year: " + car.year));
        detailsPanel.add(new JLabel("Price: $" + price));
        detailsPanel.add(new JLabel("Stock: " + stock));
        detailsPanel.add(new JLabel("Company: " + company));

        add(detailsPanel, BorderLayout.CENTER);

        // === Close Button ===
        JButton closeBtn = new JButton("Close");
        closeBtn.addActionListener(e -> dispose());
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(closeBtn);
        add(buttonPanel, BorderLayout.SOUTH);
    }
}
