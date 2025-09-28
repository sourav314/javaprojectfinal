package mccd;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.*;

public class CarDialog extends JDialog {

    private JTextField vinField, makeField, modelField, yearField, priceField, stockField;
    private JComboBox<String> companyBox;
    private Integer carId; // null = add, not null = update

    public CarDialog(JFrame parent, String title, Integer carId) {
        super(parent, title, true);
        setSize(400, 450);
        setLocationRelativeTo(parent);
        this.carId = carId;

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(new Color(245, 245, 245));

        vinField = addField(mainPanel, "VIN:");
        makeField = addField(mainPanel, "Make:");
        modelField = addField(mainPanel, "Model:");
        yearField = addField(mainPanel, "Year:");
        priceField = addField(mainPanel, "Price:");
        stockField = addField(mainPanel, "Stock:");

        // Restrict input for numeric fields
        restrictToInteger(yearField);
        restrictToDouble(priceField);
        restrictToInteger(stockField);

        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(new JLabel("Company:"));
        companyBox = new JComboBox<>();
        loadCompanies();
        companyBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        mainPanel.add(companyBox);

        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(245, 245, 245));
        RoundedButton saveBtn = new RoundedButton("Save");
        RoundedButton cancelBtn = new RoundedButton("Cancel");
        saveBtn.setPreferredSize(new Dimension(100, 30));
        cancelBtn.setPreferredSize(new Dimension(100, 30));
        buttonPanel.add(saveBtn);
        buttonPanel.add(Box.createRigidArea(new Dimension(20, 0)));
        buttonPanel.add(cancelBtn);

        mainPanel.add(buttonPanel);
        add(mainPanel);

        if (carId != null) loadCarDetails(carId);

        saveBtn.addActionListener(e -> saveCar());
        cancelBtn.addActionListener(e -> dispose());
    }

    private JTextField addField(JPanel panel, String label) {
        JLabel jLabel = new JLabel(label);
        panel.add(jLabel);
        JTextField field = new JTextField();
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        panel.add(field);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        return field;
    }

    private void restrictToInteger(JTextField field) {
        field.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c) && c != KeyEvent.VK_BACK_SPACE) e.consume();
            }
        });
    }

    private void restrictToDouble(JTextField field) {
        field.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c) && c != KeyEvent.VK_BACK_SPACE && c != '.') e.consume();
            }
        });
    }

    private void loadCompanies() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            ResultSet rs = conn.createStatement().executeQuery("SELECT name FROM companies");
            while (rs.next()) companyBox.addItem(rs.getString("name"));
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading companies: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadCarDetails(int carId) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT vin, make, model, year, price, stock, company_id FROM cars WHERE car_id=?");
            ps.setInt(1, carId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                vinField.setText(rs.getString("vin"));
                makeField.setText(rs.getString("make"));
                modelField.setText(rs.getString("model"));
                yearField.setText(String.valueOf(rs.getInt("year")));
                priceField.setText(String.valueOf(rs.getDouble("price")));
                stockField.setText(String.valueOf(rs.getInt("stock")));

                int companyId = rs.getInt("company_id");
                for (int i = 0; i < companyBox.getItemCount(); i++) {
                    if (getCompanyIdByName(companyBox.getItemAt(i)) == companyId) {
                        companyBox.setSelectedIndex(i);
                        break;
                    }
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading car: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private int getCompanyIdByName(String name) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("SELECT company_id FROM companies WHERE name=?");
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("company_id");
        }
        return -1;
    }

    private void saveCar() {
        String vin = vinField.getText().trim();
        String make = makeField.getText().trim();
        String model = modelField.getText().trim();
        String yearStr = yearField.getText().trim();
        String priceStr = priceField.getText().trim();
        String stockStr = stockField.getText().trim();
        String companyName = (String) companyBox.getSelectedItem();

        if (vin.isEmpty() || make.isEmpty() || model.isEmpty() || yearStr.isEmpty() ||
                priceStr.isEmpty() || stockStr.isEmpty() || companyName == null) {
            JOptionPane.showMessageDialog(this, "Please fill all fields.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int year = Integer.parseInt(yearStr);
            double price = Double.parseDouble(priceStr);
            int stock = Integer.parseInt(stockStr);
            int companyId = getCompanyIdByName(companyName);

            try (Connection conn = DatabaseConnection.getConnection()) {
                PreparedStatement ps;
                if (carId == null) {
                    // Add new car
                    ps = conn.prepareStatement(
                            "INSERT INTO cars (vin, make, model, year, price, stock, company_id) VALUES (?,?,?,?,?,?,?)");
                    ps.setString(1, vin);
                    ps.setString(2, make);
                    ps.setString(3, model);
                    ps.setInt(4, year);
                    ps.setDouble(5, price);
                    ps.setInt(6, stock);
                    ps.setInt(7, companyId);
                } else {
                    // Update existing car
                    ps = conn.prepareStatement(
                            "UPDATE cars SET vin=?, make=?, model=?, year=?, price=?, stock=?, company_id=? WHERE car_id=?");
                    ps.setString(1, vin);
                    ps.setString(2, make);
                    ps.setString(3, model);
                    ps.setInt(4, year);
                    ps.setDouble(5, price);
                    ps.setInt(6, stock);
                    ps.setInt(7, companyId);
                    ps.setInt(8, carId);
                }
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Car saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Year, Price, and Stock must be valid numbers.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
