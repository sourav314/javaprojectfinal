package main_project;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class SignUpWindow extends JFrame {

    public SignUpWindow() {
        setTitle("Sign Up");
        setSize(400, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        JPanel panel = new JPanel();
        panel.setBackground(new Color(245, 245, 245));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Sign Up");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setForeground(new Color(33, 33, 33));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Labels and fields
        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JTextField nameField = new JTextField();
        nameField.setMaximumSize(new Dimension(300, 30));
        nameField.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel phoneLabel = new JLabel("Phone Number:");
        phoneLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JTextField phoneField = new JTextField();
        phoneField.setMaximumSize(new Dimension(300, 30));
        phoneField.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPasswordField passwordField = new JPasswordField();
        passwordField.setMaximumSize(new Dimension(300, 30));
        passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel confirmPasswordLabel = new JLabel("Confirm Password:");
        confirmPasswordLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPasswordField confirmPasswordField = new JPasswordField();
        confirmPasswordField.setMaximumSize(new Dimension(300, 30));
        confirmPasswordField.setAlignmentX(Component.CENTER_ALIGNMENT);

        RoundedButton signupBtn = new RoundedButton("Sign Up");
        signupBtn.setBackground(new Color(76, 175, 80));
        signupBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        signupBtn.addActionListener((ActionEvent e) -> {
            String name = nameField.getText().trim();
            String phone = phoneField.getText().trim();
            String password = new String(passwordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());

            if (name.isEmpty() || phone.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
            } else if (!password.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(this, "Passwords do not match!", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Account created successfully!\nName: " + name + "\nPhone: " + phone);
                // Backend signup logic can be added here
            }
        });

        // Add components
        panel.add(Box.createVerticalStrut(20));
        panel.add(title);
        panel.add(Box.createVerticalStrut(20));
        panel.add(nameLabel);
        panel.add(nameField);
        panel.add(Box.createVerticalStrut(10));
        panel.add(phoneLabel);
        panel.add(phoneField);
        panel.add(Box.createVerticalStrut(10));
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(Box.createVerticalStrut(10));
        panel.add(confirmPasswordLabel);
        panel.add(confirmPasswordField);
        panel.add(Box.createVerticalStrut(20));
        panel.add(signupBtn);
        panel.add(Box.createVerticalGlue());

        add(panel);
    }
}