/*
 * Change Password Page
 * Made by: Mohd Dayan
 *
 * The logged-in user enters the old password and a new one.
 * The old password is verified in MySQL before the "users" table is updated.
 * After a successful change the user must login again.
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class ChangePassword extends BaseFrame implements ActionListener {

    JLabel titleLabel, userLabel, oldLabel, newLabel, confirmLabel;
    JPasswordField oldField, newField, confirmField;
    JButton updateButton, backButton;

    public ChangePassword() {

        super("Change Password", 440, 400);

        titleLabel = new JLabel("Change Password");
        titleLabel.setFont(new Font("Serif", Font.BOLD, 16));
        titleLabel.setBounds(130, 10, 250, 30);
        body.add(titleLabel);

        userLabel = new JLabel("User: " + Session.getUsername() + " (" + Session.getRole() + ")");
        userLabel.setBounds(40, 48, 340, 25);
        body.add(userLabel);

        oldLabel = new JLabel("Old Password:");
        oldLabel.setBounds(40, 85, 150, 25);
        body.add(oldLabel);
        oldField = new JPasswordField();
        oldField.setBounds(210, 85, 160, 25);
        body.add(oldField);

        newLabel = new JLabel("New Password:");
        newLabel.setBounds(40, 125, 150, 25);
        body.add(newLabel);
        newField = new JPasswordField();
        newField.setBounds(210, 125, 160, 25);
        body.add(newField);

        confirmLabel = new JLabel("Confirm Password:");
        confirmLabel.setBounds(40, 165, 150, 25);
        body.add(confirmLabel);
        confirmField = new JPasswordField();
        confirmField.setBounds(210, 165, 160, 25);
        body.add(confirmField);

        updateButton = new JButton("Update Password");
        updateButton.setBounds(110, 215, 190, 32);
        updateButton.addActionListener(this);
        body.add(updateButton);

        backButton = new JButton("Back to Dashboard");
        backButton.setBounds(110, 258, 190, 28);
        backButton.addActionListener(this);
        body.add(backButton);

        getRootPane().setDefaultButton(updateButton);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == updateButton) {
            String oldPass = new String(oldField.getPassword());
            String newPass = new String(newField.getPassword());
            String confirmPass = new String(confirmField.getPassword());

            if (oldPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
                Util.error(this, "Please fill all password fields.");
                return;
            }
            if (!newPass.equals(confirmPass)) {
                Util.error(this, "New password and confirm password do not match.");
                return;
            }
            if (newPass.equals(oldPass)) {
                Util.error(this, "New password must be different from the old password.");
                return;
            }
            String error = Validator.password(newPass);
            if (error != null) {
                Util.error(this, error);
                return;
            }

            try {
                if (UserDAO.changePassword(Session.getUsername(), oldPass, newPass)) {
                    Util.info(this, "Password updated successfully! Please login again.");
                    Session.clear();
                    new Login();
                    dispose();
                } else {
                    Util.error(this, "Old password is incorrect.");
                    oldField.setText("");
                }
            } catch (SQLException ex) {
                Util.dbError(this, ex);
            }
        }

        else if (e.getSource() == backButton) {
            openDashboard();
        }
    }
}
