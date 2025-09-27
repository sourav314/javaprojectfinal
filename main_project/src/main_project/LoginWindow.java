package main_project;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class LoginWindow extends JFrame {

    public LoginWindow() {
        setTitle("Login");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        JPanel panel = new JPanel();
        panel.setBackground(new Color(245, 245, 245));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Login");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setForeground(new Color(33, 33, 33));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Label and field for Name or Phone Number
        JLabel namePhoneLabel = new JLabel("Name or Phone Number:");
        namePhoneLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JTextField namePhoneField = new JTextField();
        namePhoneField.setMaximumSize(new Dimension(300, 30));
        namePhoneField.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Label and field for Password
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JPasswordField passwordField = new JPasswordField();
        passwordField.setMaximumSize(new Dimension(300, 30));
        passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Login button
        RoundedButton loginBtn = new RoundedButton("Login");
        loginBtn.setBackground(new Color(33, 150, 243));
        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        loginBtn.addActionListener((ActionEvent e) -> {
            String input = namePhoneField.getText().trim();
            String password = new String(passwordField.getPassword());

            if (input.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter both fields!", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                // Detect if input is phone number or name
                if (input.matches("\\d+")) {
                    JOptionPane.showMessageDialog(this, "Phone Number: " + input + "\nLogged in successfully!");
                } else {
                    JOptionPane.showMessageDialog(this, "Name: " + input + "\nLogged in successfully!");
                }
            }
        });

        // Add components
        panel.add(Box.createVerticalStrut(30));
        panel.add(title);
        panel.add(Box.createVerticalStrut(20));
        panel.add(namePhoneLabel);
        panel.add(namePhoneField);
        panel.add(Box.createVerticalStrut(10));
        panel.add(passwordLabel);
        panel.add(passwordField);
        panel.add(Box.createVerticalStrut(20));
        panel.add(loginBtn);
        panel.add(Box.createVerticalGlue());

        add(panel);
    }
}