package mccd;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginWindow extends JFrame {

    private JFrame parentFrame; // reference to main MCCD frame

    // ✅ Constructor accepting parent frame
    public LoginWindow(JFrame parent) {
        this(); // call default UI setup
        this.parentFrame = parent;
    }

    // Default constructor sets up the UI
    public LoginWindow() {
        setTitle("Login");
        setSize(360, 250);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(new Color(245, 245, 245));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;

        // Title
        gbc.gridy = 0;
        JLabel title = new JLabel("Login", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        mainPanel.add(title, gbc);

        // Username/Email Field
        gbc.gridy++;
        RoundedTextField userField = new RoundedTextField(20);
        setPlaceholder(userField, "Enter username or email");
        mainPanel.add(userField, gbc);

        // Password Field
        gbc.gridy++;
        RoundedPasswordField passField = new RoundedPasswordField(20);
        setPlaceholder(passField, "Enter password");
        mainPanel.add(passField, gbc);

        // Move focus from username/email to password on Enter
        userField.addActionListener(e -> passField.requestFocusInWindow());

        // Login Button
        gbc.gridy++;
        RoundedButton loginBtn = new RoundedButton("Login");
        loginBtn.setPreferredSize(new Dimension(100, 30));
        loginBtn.setBackground(new Color(33, 150, 243));
        mainPanel.add(loginBtn, gbc);

        // Pressing Enter in password field triggers login
        passField.addActionListener(e -> loginBtn.doClick());

        // Login action
        loginBtn.addActionListener(e -> {
            String userInput = userField.getText().trim();
            String password = new String(passField.getPassword()).trim();

            if(userInput.isEmpty() || password.isEmpty() ||
               userInput.equals("Enter username or email") || password.equals("Enter password")) {
                JOptionPane.showMessageDialog(this,"Please fill in all fields.","Error",JOptionPane.ERROR_MESSAGE);
                return;
            }

            String query = "SELECT * FROM users WHERE user_identifier=? AND password=?";

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(query)) {

                ps.setString(1, userInput);
                ps.setString(2, password);

                ResultSet rs = ps.executeQuery();
                if(rs.next()) {
                    JOptionPane.showMessageDialog(this,"Login successful!","Success",JOptionPane.INFORMATION_MESSAGE);

                    // ✅ Close parent MCCD frame if exists
                    if(parentFrame != null) parentFrame.dispose();

                    // Open main interface
                    new MainInterface().setVisible(true);

                    dispose(); // Close login window
                } else {
                    JOptionPane.showMessageDialog(this,"Invalid username or password.","Error",JOptionPane.ERROR_MESSAGE);
                }

            } catch(SQLException ex){
                JOptionPane.showMessageDialog(this,"Error: "+ex.getMessage(),"Database Error",JOptionPane.ERROR_MESSAGE);
            }
        });

        add(mainPanel);
    }

    private void setPlaceholder(JTextField field, String placeholder) {
        field.setForeground(Color.GRAY);
        field.setText(placeholder);
        field.setHorizontalAlignment(JTextField.CENTER);
        field.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                }
            }
            public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setForeground(Color.GRAY);
                    field.setText(placeholder);
                }
            }
        });
    }
}
