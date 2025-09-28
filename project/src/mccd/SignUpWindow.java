package mccd;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SignUpWindow extends JFrame {

    private JFrame parentFrame; // reference to main MCCD frame

    // ✅ Constructor that accepts parent frame
    public SignUpWindow(JFrame parent) {
        this(); // call default UI setup
        this.parentFrame = parent;
    }

    // Default constructor sets up UI
    public SignUpWindow() {
        setTitle("Sign Up");
        setSize(360, 300);
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
        JLabel title = new JLabel("Create Account", SwingConstants.CENTER);
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

        // Sign Up Button
        gbc.gridy++;
        RoundedButton signupBtn = new RoundedButton("Sign Up");
        signupBtn.setPreferredSize(new Dimension(100, 30));
        signupBtn.setBackground(new Color(76, 175, 80));
        mainPanel.add(signupBtn, gbc);

        // Pressing Enter in password field triggers signup
        passField.addActionListener(e -> signupBtn.doClick());

        // Signup Action
        signupBtn.addActionListener(e -> {
            String userInput = userField.getText().trim();
            String password = new String(passField.getPassword()).trim();

            if(userInput.isEmpty() || password.isEmpty() ||
               userInput.equals("Enter username or email") || password.equals("Enter password")) {
                JOptionPane.showMessageDialog(this,"Please fill in all fields.","Error",JOptionPane.ERROR_MESSAGE);
                return;
            }

            String query = "INSERT INTO users (user_identifier, password) VALUES (?, ?)";

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(query)) {
                
                ps.setString(1, userInput);
                ps.setString(2, password);
                ps.executeUpdate();

                JOptionPane.showMessageDialog(this,"Account created successfully!","Success",JOptionPane.INFORMATION_MESSAGE);

                // ✅ Close parent MCCD frame if exists
                if(parentFrame != null) parentFrame.dispose();

                // Open main interface after signup
                new MainInterface().setVisible(true);

                dispose(); // Close signup window

            } catch(SQLException ex){
                if (ex.getErrorCode() == 1062) { // Duplicate entry
                    JOptionPane.showMessageDialog(this,"This username or email already exists.","Error",JOptionPane.ERROR_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this,"Error: "+ex.getMessage(),"Database Error",JOptionPane.ERROR_MESSAGE);
                }
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
