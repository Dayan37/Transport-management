/*
 * Student Registration / Add Student Page
 * Made by: Mohd Dayan
 *
 * Form to add a new student and assign a route. Saves into the MySQL "students" table.
 * The route list is loaded from the "routes" table.
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class AddStudent extends BaseFrame implements ActionListener {

    JLabel titleLabel, nameLabel, rollLabel, courseLabel, routeLabel;
    JTextField nameField, rollField, courseField;
    JComboBox<Item> routeBox;
    JButton saveButton, clearButton, viewAllButton, backButton;

    public AddStudent() {

        super("Add Student", 440, 420);
        if (!requireAdmin()) {
            return;
        }

        titleLabel = new JLabel("Student Registration Form");
        titleLabel.setFont(new Font("Serif", Font.BOLD, 16));
        titleLabel.setBounds(60, 10, 350, 30);
        body.add(titleLabel);

        rollLabel = new JLabel("Roll No:");
        rollLabel.setBounds(40, 55, 150, 25);
        body.add(rollLabel);
        rollField = new JTextField();
        rollField.setBounds(200, 55, 180, 25);
        body.add(rollField);

        nameLabel = new JLabel("Student Name:");
        nameLabel.setBounds(40, 95, 150, 25);
        body.add(nameLabel);
        nameField = new JTextField();
        nameField.setBounds(200, 95, 180, 25);
        body.add(nameField);

        courseLabel = new JLabel("Course:");
        courseLabel.setBounds(40, 135, 150, 25);
        body.add(courseLabel);
        courseField = new JTextField();
        courseField.setBounds(200, 135, 180, 25);
        body.add(courseField);

        routeLabel = new JLabel("Assigned Route:");
        routeLabel.setBounds(40, 175, 150, 25);
        body.add(routeLabel);
        routeBox = new JComboBox<>();
        routeBox.setBounds(200, 175, 180, 25);
        body.add(routeBox);
        loadRoutes();

        saveButton = new JButton("Save Student");
        saveButton.setBounds(40, 230, 160, 30);
        saveButton.addActionListener(this);
        body.add(saveButton);

        clearButton = new JButton("Clear");
        clearButton.setBounds(220, 230, 160, 30);
        clearButton.addActionListener(this);
        body.add(clearButton);

        viewAllButton = new JButton("View All Students");
        viewAllButton.setBounds(40, 275, 160, 30);
        viewAllButton.addActionListener(this);
        body.add(viewAllButton);

        backButton = new JButton("Back to Dashboard");
        backButton.setBounds(220, 275, 160, 30);
        backButton.addActionListener(this);
        body.add(backButton);

        setVisible(true);
    }

    private void loadRoutes() {
        try {
            routeBox.removeAllItems();
            for (Item item : RouteDAO.routeItems()) {
                routeBox.addItem(item);
            }
        } catch (SQLException ex) {
            Util.dbError(this, ex);
        }
    }

    private void clearForm() {
        rollField.setText("");
        nameField.setText("");
        courseField.setText("");
        if (routeBox.getItemCount() > 0) {
            routeBox.setSelectedIndex(0);
        }
        rollField.requestFocus();
    }

    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == saveButton) {
            String roll = rollField.getText().trim().toUpperCase();
            String name = nameField.getText().trim();
            String course = courseField.getText().trim();

            String error = Validator.student(roll, name, course);
            if (error != null) {
                Util.error(this, error);
                return;
            }

            Item route = (Item) routeBox.getSelectedItem();
            try {
                StudentDAO.insert(roll, name, course, route == null ? null : route.idOrNull());
                Util.info(this, "Student saved successfully!");
                clearForm();
            } catch (SQLException ex) {
                Util.dbError(this, ex);
            }
        }

        else if (e.getSource() == clearButton) {
            clearForm();
        }

        else if (e.getSource() == viewAllButton) {
            openView();
        }

        else if (e.getSource() == backButton) {
            openDashboard();
        }
    }
}
