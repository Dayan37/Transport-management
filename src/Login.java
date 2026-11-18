/*
 * Login Page
 * Made by: Mohd Dayan
 *
 * Takes Username, Password and "Login As" (Admin / User).
 * The details are checked against the MySQL "users" table (password stored as SHA-256 hash).
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class Login extends JFrame implements ActionListener {

    JLabel titleLabel, userLabel, passLabel, roleLabel;
    JTextField userField;
    JPasswordField passField;
    JComboBox<String> roleBox;
    JButton loginButton, exitButton;

    public Login() {

        setTitle("Transport System - Login");
        setSize(420, 330);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        titleLabel = new JLabel("Student Transport Fee & Route System");
        titleLabel.setFont(new Font("Serif", Font.BOLD, 16));
        titleLabel.setBounds(30, 20, 380, 30);
        add(titleLabel);

        userLabel = new JLabel("Username:");
        userLabel.setBounds(40, 90, 150, 25);
        add(userLabel);

        userField = new JTextField();
        userField.setBounds(200, 90, 170, 25);
        add(userField);

        passLabel = new JLabel("Password:");
        passLabel.setBounds(40, 130, 150, 25);
        add(passLabel);

        passField = new JPasswordField();
        passField.setBounds(200, 130, 170, 25);
        add(passField);

        roleLabel = new JLabel("Login As:");
        roleLabel.setBounds(40, 170, 150, 25);
        add(roleLabel);

        roleBox = new JComboBox<>(new String[]{"Admin", "User"});
        roleBox.setBounds(200, 170, 170, 25);
        add(roleBox);

        loginButton = new JButton("Login");
        loginButton.setBounds(70, 220, 130, 32);
        loginButton.addActionListener(this);
        add(loginButton);

        exitButton = new JButton("Exit");
        exitButton.setBounds(220, 220, 130, 32);
        exitButton.addActionListener(this);
        add(exitButton);

        getRootPane().setDefaultButton(loginButton);   // Enter key = Login
        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == exitButton) {
            System.exit(0);
        }

        if (e.getSource() == loginButton) {
            String username = userField.getText().trim();
            String password = new String(passField.getPassword());
            String role = (String) roleBox.getSelectedItem();

            if (username.isEmpty() || password.isEmpty()) {
                Util.error(this, "Please enter username and password.");
                return;
            }

            try {
                if (UserDAO.authenticate(username, password, role)) {
                    Session.start(username, role);
                    new Dashboard();
                    dispose();
                } else {
                    Util.error(this, "Invalid username, password or role.");
                    passField.setText("");
                }
            } catch (SQLException ex) {
                Util.dbError(this, ex);
            }
        }
    }
}
