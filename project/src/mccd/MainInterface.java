package mccd;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

// Professional LogoPanel
class LogoPanel extends JPanel {
    private final Image img;

    public LogoPanel(String imagePath) {
        setPreferredSize(new Dimension(120, 120));
        setOpaque(false);

        // Load image from classpath
        java.net.URL imageURL = getClass().getClassLoader().getResource(imagePath);
        if (imageURL != null) {
            img = new ImageIcon(imageURL).getImage();
        } else {
            img = null;
            System.err.println("Logo image not found! Make sure it is in src/image/logo.png");
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (img != null) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Scale image proportionally
            int w = getWidth();
            int h = getHeight();
            int imgW = img.getWidth(this);
            int imgH = img.getHeight(this);
            double scale = Math.min((double) w / imgW, (double) h / imgH);
            int drawW = (int) (imgW * scale);
            int drawH = (int) (imgH * scale);

            int x = (w - drawW) / 2;
            int y = (h - drawH) / 2;

            g2.drawImage(img, x, y, drawW, drawH, this);
            g2.dispose();
        }
    }
}

// MainInterface refactored
public class MainInterface extends JFrame {
    private JTable carTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> companyFilter;
    private JTextField searchField;

    public MainInterface() {
        setTitle("CarDeX - Main Interface");
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // === Top Panel ===
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(33, 150, 243));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Logo on left
        LogoPanel logoPanel = new LogoPanel("image/logo.png");
        topPanel.add(logoPanel, BorderLayout.WEST);

        // Title centered
        JLabel titleLabel = new JLabel("CarDeX - Multi-Company Car Dealership", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 26));
        titleLabel.setForeground(Color.WHITE);
        topPanel.add(titleLabel, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);

        // === Filter & Search Panel ===
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        filterPanel.setBackground(new Color(245, 245, 245));

        filterPanel.add(new JLabel("Filter by Company:"));
        companyFilter = new JComboBox<>();
        filterPanel.add(companyFilter);

        filterPanel.add(new JLabel("Search:"));
        searchField = new JTextField(20);
        filterPanel.add(searchField);

        RoundedButton searchBtn = new RoundedButton("Search");
        filterPanel.add(searchBtn);

        add(filterPanel, BorderLayout.PAGE_START);

        // === Table ===
        tableModel = new DefaultTableModel(new String[]{
                "ID", "VIN", "Make", "Model", "Year", "Price", "Stock", "Company"}, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };

        carTable = new JTable(tableModel);
        carTable.setRowHeight(28);
        carTable.getTableHeader().setReorderingAllowed(false);
        carTable.setAutoCreateRowSorter(true);

        // Center text
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < carTable.getColumnCount(); i++)
            carTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);

        JScrollPane scrollPane = new JScrollPane(carTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(scrollPane, BorderLayout.CENTER);

        // === Buttons Panel ===
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBackground(new Color(245, 245, 245));

        RoundedButton addBtn = new RoundedButton("Add Car");
        RoundedButton updateBtn = new RoundedButton("Update Car");
        RoundedButton deleteBtn = new RoundedButton("Delete Car");
        RoundedButton logoutBtn = new RoundedButton("Logout");
        logoutBtn.setBackground(new Color(244, 67, 54));

        buttonPanel.add(addBtn);
        buttonPanel.add(updateBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(logoutBtn);

        add(buttonPanel, BorderLayout.SOUTH);

        // === Load data ===
        loadCompanies();
        loadTableData(null, null);

        // === Actions ===
        companyFilter.addActionListener(e -> loadTableData((String) companyFilter.getSelectedItem(), searchField.getText().trim()));
        searchBtn.addActionListener(e -> loadTableData((String) companyFilter.getSelectedItem(), searchField.getText().trim()));
        addBtn.addActionListener(e -> openAddCarDialog());
        updateBtn.addActionListener(e -> openUpdateCarDialog());
        deleteBtn.addActionListener(e -> deleteSelectedCar());

        // Logout
        logoutBtn.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> new MCCD().setVisible(true));
        });
    }

    // === Data loading methods ===
    private void loadCompanies() {
        companyFilter.removeAllItems();
        companyFilter.addItem("All");
        try (Connection conn = DatabaseConnection.getConnection()) {
            ResultSet rs = conn.createStatement().executeQuery("SELECT name FROM companies");
            while (rs.next()) companyFilter.addItem(rs.getString("name"));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading companies: " + ex.getMessage());
        }
    }

    private void loadTableData(String company, String search) {
        tableModel.setRowCount(0);
        String query = "SELECT c.car_id, c.vin, c.make, c.model, c.year, c.price, c.stock, co.name " +
                "FROM cars c JOIN companies co ON c.company_id = co.company_id WHERE 1=1";

        if (company != null && !company.equals("All")) query += " AND co.name=?";
        if (search != null && !search.isEmpty()) query += " AND (c.vin LIKE ? OR c.make LIKE ? OR c.model LIKE ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            int index = 1;
            if (company != null && !company.equals("All")) ps.setString(index++, company);
            if (search != null && !search.isEmpty()) {
                String likeSearch = "%" + search + "%";
                ps.setString(index++, likeSearch);
                ps.setString(index++, likeSearch);
                ps.setString(index, likeSearch);
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4),
                        rs.getInt(5), rs.getDouble(6), rs.getInt(7), rs.getString(8)
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading cars: " + ex.getMessage());
        }
    }

    private void openAddCarDialog() {
        CarDialog dialog = new CarDialog(this, "Add Car", null);
        dialog.setVisible(true);
        loadTableData((String) companyFilter.getSelectedItem(), searchField.getText().trim());
    }

    private void openUpdateCarDialog() {
        int selectedRow = carTable.getSelectedRow();
        if (selectedRow == -1) { JOptionPane.showMessageDialog(this, "Select a car to update."); return; }
        int carId = (int) tableModel.getValueAt(selectedRow, 0);
        CarDialog dialog = new CarDialog(this, "Update Car", carId);
        dialog.setVisible(true);
        loadTableData((String) companyFilter.getSelectedItem(), searchField.getText().trim());
    }

    private void deleteSelectedCar() {
        int selectedRow = carTable.getSelectedRow();
        if (selectedRow == -1) { JOptionPane.showMessageDialog(this, "Select a car to delete."); return; }
        int carId = (int) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure to delete this car?");
        if (confirm != JOptionPane.YES_OPTION) return;

        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("DELETE FROM cars WHERE car_id=?");
            ps.setInt(1, carId);
            ps.executeUpdate();
            loadTableData((String) companyFilter.getSelectedItem(), searchField.getText().trim());
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error deleting car: " + ex.getMessage());
        }
    }
}
