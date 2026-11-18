/*
 * Transport Fee Management Page
 * Made by: Mohd Dayan
 *
 * Form to record a student's transport fee. Saves into the MySQL "transport_fees" table.
 * The student list comes from the database; the route is shown automatically.
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.time.LocalDate;

public class TransportFee extends BaseFrame implements ActionListener {

    JLabel titleLabel, studentLabel, routeLabel, routeValue, amountLabel, dueLabel, statusLabel;
    JComboBox<Item> studentBox;
    JComboBox<String> statusBox;
    JTextField amountField, dueField;
    JButton saveButton, clearButton, backButton;

    public TransportFee() {

        super("Fee Management", 440, 465);
        if (!requireAdmin()) {
            return;
        }

        titleLabel = new JLabel("Transport Fee Management");
        titleLabel.setFont(new Font("Serif", Font.BOLD, 16));
        titleLabel.setBounds(40, 10, 350, 30);
        body.add(titleLabel);

        studentLabel = new JLabel("Student:");
        studentLabel.setBounds(40, 55, 150, 25);
        body.add(studentLabel);
        studentBox = new JComboBox<>();
        studentBox.setBounds(170, 55, 220, 25);
        studentBox.addActionListener(this);
        body.add(studentBox);

        routeLabel = new JLabel("Route:");
        routeLabel.setBounds(40, 95, 150, 25);
        body.add(routeLabel);
        routeValue = new JLabel("-");
        routeValue.setBounds(170, 95, 220, 25);
        body.add(routeValue);

        amountLabel = new JLabel("Amount (Rs):");
        amountLabel.setBounds(40, 135, 150, 25);
        body.add(amountLabel);
        amountField = new JTextField();
        amountField.setBounds(170, 135, 220, 25);
        body.add(amountField);

        dueLabel = new JLabel("Due Date:");
        dueLabel.setBounds(40, 175, 150, 25);
        body.add(dueLabel);
        dueField = new JTextField(LocalDate.now().plusMonths(1).toString());
        dueField.setToolTipText("Format: yyyy-MM-dd");
        dueField.setBounds(170, 175, 220, 25);
        body.add(dueField);

        JLabel formatHint = new JLabel("(format: yyyy-MM-dd)");
        formatHint.setFont(new Font("SansSerif", Font.ITALIC, 11));
        formatHint.setBounds(170, 200, 220, 18);
        body.add(formatHint);

        statusLabel = new JLabel("Status:");
        statusLabel.setBounds(40, 230, 150, 25);
        body.add(statusLabel);
        statusBox = new JComboBox<>(new String[]{"Pending", "Paid"});
        statusBox.setBounds(170, 230, 220, 25);
        body.add(statusBox);

        saveButton = new JButton("Save Fee Record");
        saveButton.setBounds(40, 285, 170, 30);
        saveButton.addActionListener(this);
        body.add(saveButton);

        clearButton = new JButton("Clear");
        clearButton.setBounds(220, 285, 170, 30);
        clearButton.addActionListener(this);
        body.add(clearButton);

        backButton = new JButton("Back to Dashboard");
        backButton.setBounds(40, 325, 350, 28);
        backButton.addActionListener(this);
        body.add(backButton);

        loadStudents();
        setVisible(true);
    }

    private void loadStudents() {
        try {
            studentBox.removeAllItems();
            for (Item item : StudentDAO.studentItems()) {
                studentBox.addItem(item);
            }
        } catch (SQLException ex) {
            Util.dbError(this, ex);
        }
    }

    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == studentBox) {
            // show the route of the chosen student
            Item student = (Item) studentBox.getSelectedItem();
            if (student == null || student.id == 0) {
                routeValue.setText("-");
            } else {
                try {
                    routeValue.setText(StudentDAO.routeNameOf(student.id));
                } catch (SQLException ex) {
                    Util.dbError(this, ex);
                }
            }
        }

        else if (e.getSource() == saveButton) {
            Item student = (Item) studentBox.getSelectedItem();
            if (student == null || student.id == 0) {
                Util.error(this, "Please select a student.");
                return;
            }
            String amount = amountField.getText().trim();
            String due = dueField.getText().trim();

            String error = Validator.fee(amount, due);
            if (error != null) {
                Util.error(this, error);
                return;
            }
            try {
                FeeDAO.insert(student.id, amount, due, (String) statusBox.getSelectedItem());
                Util.info(this, "Fee record saved successfully!");
                amountField.setText("");
                statusBox.setSelectedIndex(0);
            } catch (SQLException ex) {
                Util.dbError(this, ex);
            }
        }

        else if (e.getSource() == clearButton) {
            if (studentBox.getItemCount() > 0) {
                studentBox.setSelectedIndex(0);
            }
            amountField.setText("");
            dueField.setText(LocalDate.now().plusMonths(1).toString());
            statusBox.setSelectedIndex(0);
        }

        else if (e.getSource() == backButton) {
            openDashboard();
        }
    }
}
