/*
 * Dashboard (Home Page after login)
 * Made by: Mohd Dayan
 *
 * Shows a quick overview (counts from the database) and buttons to every module.
 * Add / Update / Delete pages are enabled for Admin only.
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class Dashboard extends BaseFrame implements ActionListener {

    JLabel welcomeLabel, noteLabel;
    JLabel studentCount, routeCount, busCount, pendingCount;
    JButton addStudentButton, routeBusButton, feeButton, viewButton, updateDeleteButton,
            changePassButton, logoutButton;

    public Dashboard() {

        super("Dashboard", 560, 480);

        welcomeLabel = new JLabel("Welcome, " + Session.getUsername() + " (" + Session.getRole() + ")");
        welcomeLabel.setFont(new Font("Serif", Font.BOLD, 18));
        welcomeLabel.setBounds(30, 15, 480, 30);
        body.add(welcomeLabel);

        // ---- overview boxes (numbers come from MySQL) ----
        studentCount = statBox("Students", 30);
        routeCount = statBox("Routes", 160);
        busCount = statBox("Buses", 290);
        pendingCount = statBox("Pending Fees", 420);

        noteLabel = new JLabel(" ");
        noteLabel.setForeground(Color.RED);
        noteLabel.setBounds(30, 125, 500, 20);
        body.add(noteLabel);

        addStudentButton = button("Add Student", 30, 155);
        routeBusButton = button("Route & Bus Management", 285, 155);
        feeButton = button("Transport Fee Management", 30, 200);
        viewButton = button("View All Records", 285, 200);
        updateDeleteButton = button("Update / Delete Records", 30, 245);
        changePassButton = button("Change Password", 285, 245);
        logoutButton = button("Logout", 157, 300);

        if (!Session.isAdmin()) {
            addStudentButton.setEnabled(false);
            routeBusButton.setEnabled(false);
            feeButton.setEnabled(false);
            updateDeleteButton.setEnabled(false);
        }

        loadCounts();
        setVisible(true);
    }

    private JLabel statBox(String title, int x) {
        JPanel box = new JPanel(null);
        box.setBounds(x, 55, 115, 65);
        box.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        JLabel value = new JLabel("-", SwingConstants.CENTER);
        value.setFont(new Font("SansSerif", Font.BOLD, 22));
        value.setBounds(0, 8, 115, 30);
        box.add(value);

        JLabel name = new JLabel(title, SwingConstants.CENTER);
        name.setBounds(0, 38, 115, 20);
        box.add(name);

        body.add(box);
        return value;
    }

    private JButton button(String text, int x, int y) {
        JButton b = new JButton(text);
        b.setBounds(x, y, 245, 38);
        b.addActionListener(this);
        body.add(b);
        return b;
    }

    private void loadCounts() {
        try {
            Object[] c = FeeDAO.dashboardCounts();
            studentCount.setText(String.valueOf(c[0]));
            routeCount.setText(String.valueOf(c[1]));
            busCount.setText(String.valueOf(c[2]));
            pendingCount.setText(String.valueOf(c[3]));
        } catch (SQLException ex) {
            noteLabel.setText("Could not load overview: database not reachable.");
        }
    }

    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();

        if (src == addStudentButton) {
            openAddStudent();
        } else if (src == routeBusButton) {
            openRouteBus();
        } else if (src == feeButton) {
            openFee();
        } else if (src == viewButton) {
            openView();
        } else if (src == updateDeleteButton) {
            openUpdateDelete();
        } else if (src == changePassButton) {
            openChangePassword();
        } else if (src == logoutButton) {
            logout();
        }
    }
}
