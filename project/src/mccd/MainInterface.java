package mccd;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

// ===================== Logo Panel =====================
class LogoPanel extends JPanel {
    private final Image img;

    public LogoPanel(String imagePath) {
        setPreferredSize(new Dimension(120, 120));
        setOpaque(false);
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

            int w = getWidth(), h = getHeight();
            int imgW = img.getWidth(this), imgH = img.getHeight(this);
            double scale = Math.min((double) w / imgW, (double) h / imgH);
            int drawW = (int) (imgW * scale), drawH = (int) (imgH * scale);
            int x = (w - drawW) / 2, y = (h - drawH) / 2;

            g2.drawImage(img, x, y, drawW, drawH, this);
            g2.dispose();
        }
    }
}

// ===================== Car Table Item =====================
class CarTableItem {
    int id;
    String make, model;
    int year;
    String imagePath;

    public CarTableItem(int id, String make, String model, int year, String imagePath) {
        this.id = id;
        this.make = make;
        this.model = model;
        this.year = year;
        this.imagePath = imagePath;
    }
}

// ===================== Car Cell Renderer =====================
class CarCellRenderer extends JPanel implements javax.swing.table.TableCellRenderer {
    private final JLabel imageLabel = new JLabel();
    private final JLabel textLabel = new JLabel();

    public CarCellRenderer() {
        setLayout(new BorderLayout(5, 5));
        add(imageLabel, BorderLayout.WEST);
        add(textLabel, BorderLayout.CENTER);
        setOpaque(true);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus, int row, int column) {
        if (value instanceof CarTableItem car) {
            java.net.URL imgURL = getClass().getClassLoader().getResource("image/" + car.imagePath);
            if (imgURL != null) {
                ImageIcon icon = new ImageIcon(new ImageIcon(imgURL)
                        .getImage().getScaledInstance(70, 50, Image.SCALE_SMOOTH));
                imageLabel.setIcon(icon);
            } else {
                imageLabel.setIcon(null);
            }
            textLabel.setText("<html><b>" + car.make + " " + car.model + "</b><br>Year: " + car.year + "</html>");
        }

        setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
        setForeground(isSelected ? table.getSelectionForeground() : Color.BLACK);
        return this;
    }
}

// ===================== Main Interface =====================
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
        LogoPanel logoPanel = new LogoPanel("image/logo.png");
        topPanel.add(logoPanel, BorderLayout.WEST);
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
        tableModel = new DefaultTableModel(new Object[]{"Car", "Price", "Stock", "Company"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        carTable = new JTable(tableModel);
        carTable.setRowHeight(60);
        carTable.getTableHeader().setReorderingAllowed(false);
        carTable.setAutoCreateRowSorter(true);
        carTable.getColumnModel().getColumn(0).setCellRenderer(new CarCellRenderer());
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

        // === Load Data ===
        loadCompanies();
        loadTableData(null, null);

        // === Actions ===
        companyFilter.addActionListener(e -> loadTableData((String) companyFilter.getSelectedItem(), searchField.getText().trim()));
        searchBtn.addActionListener(e -> loadTableData((String) companyFilter.getSelectedItem(), searchField.getText().trim()));
        addBtn.addActionListener(e -> openAddCarDialog());
        updateBtn.addActionListener(e -> openUpdateCarDialog());
        deleteBtn.addActionListener(e -> deleteSelectedCar());
        logoutBtn.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> new MCCD().setVisible(true));
        });

        carTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && carTable.getSelectedRow() != -1) {
                    int row = carTable.convertRowIndexToModel(carTable.getSelectedRow());
                    CarTableItem car = (CarTableItem) tableModel.getValueAt(row, 0);
                    double price = (double) tableModel.getValueAt(row, 1);
                    int stock = (int) tableModel.getValueAt(row, 2);
                    String company = (String) tableModel.getValueAt(row, 3);

                    CarDetailsDialog dialog = new CarDetailsDialog(MainInterface.this, car, price, stock, company);
                    dialog.setVisible(true);
                }
            }
        });

    }

    // ===================== Data Loading =====================
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
        String query = "SELECT c.car_id, c.make, c.model, c.year, c.price, c.stock, c.image_path, co.name " +
                       "FROM cars c JOIN companies co ON c.company_id = co.company_id WHERE 1=1";

        if (company != null && !"All".equals(company)) query += " AND co.name=?";
        if (search != null && !search.isEmpty()) query += " AND (c.make LIKE ? OR c.model LIKE ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            int idx = 1;
            if (company != null && !"All".equals(company)) ps.setString(idx++, company);
            if (search != null && !search.isEmpty()) {
                String s = "%" + search + "%";
                ps.setString(idx++, s);
                ps.setString(idx, s);
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                CarTableItem car = new CarTableItem(
                        rs.getInt("car_id"),
                        rs.getString("make"),
                        rs.getString("model"),
                        rs.getInt("year"),
                        rs.getString("image_path")
                );
                tableModel.addRow(new Object[]{car, rs.getDouble("price"), rs.getInt("stock"), rs.getString("name")});
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading cars: " + ex.getMessage());
        }
    }

    // ===================== Dialogs =====================
    private void openAddCarDialog() {
        CarDialog dialog = new CarDialog(this, "Add Car", null);
        dialog.setVisible(true);
        loadTableData((String) companyFilter.getSelectedItem(), searchField.getText().trim());
    }

    private void openUpdateCarDialog() {
        int selectedRow = carTable.getSelectedRow();
        if (selectedRow == -1) { JOptionPane.showMessageDialog(this, "Select a car to update."); return; }
        CarTableItem car = (CarTableItem) tableModel.getValueAt(selectedRow, 0);
        CarDialog dialog = new CarDialog(this, "Update Car", car.id);
        dialog.setVisible(true);
        loadTableData((String) companyFilter.getSelectedItem(), searchField.getText().trim());
    }

    private void deleteSelectedCar() {
        int selectedRow = carTable.getSelectedRow();
        if (selectedRow == -1) { JOptionPane.showMessageDialog(this, "Select a car to delete."); return; }
        CarTableItem car = (CarTableItem) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure to delete this car?");
        if (confirm != JOptionPane.YES_OPTION) return;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM cars WHERE car_id=?")) {
            ps.setInt(1, car.id);
            ps.executeUpdate();
            loadTableData((String) companyFilter.getSelectedItem(), searchField.getText().trim());
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error deleting car: " + ex.getMessage());
        }
    }

    // ===================== Show Car Details =====================
    private void showCarDetails(CarTableItem car) {
        JPanel panel = new JPanel(new BorderLayout(10,10));
        java.net.URL imgURL = getClass().getClassLoader().getResource("image/" + car.imagePath);
        if (imgURL != null) {
            JLabel imgLabel = new JLabel(new ImageIcon(new ImageIcon(imgURL)
                    .getImage().getScaledInstance(250, 150, Image.SCALE_SMOOTH)));
            panel.add(imgLabel, BorderLayout.WEST);
        }

        JLabel details = new JLabel("<html><b>Make:</b> " + car.make +
                "<br><b>Model:</b> " + car.model +
                "<br><b>Year:</b> " + car.year + "</html>");
        panel.add(details, BorderLayout.CENTER);

        JOptionPane.showMessageDialog(this, panel, "Car Details", JOptionPane.PLAIN_MESSAGE);
    }
}
