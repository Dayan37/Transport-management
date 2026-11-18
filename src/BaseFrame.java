/*
 * Base window for every screen after login.
 * Made by: Mohd Dayan
 *
 * Gives each screen the same Menu Bar and Toolbar (Add, Update, Delete, View, Logout).
 * Screens put their components inside "body". Add/Update/Delete are for Admin only;
 * a normal "User" can only view records and change their own password.
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

public abstract class BaseFrame extends JFrame {

    protected final JPanel body = new JPanel(null);

    protected BaseFrame(String title, int width, int height) {
        super("Transport System - " + title);
        setSize(width, height);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setJMenuBar(buildMenuBar());
        add(buildToolBar(), BorderLayout.NORTH);
        add(body, BorderLayout.CENTER);
    }

    // ---------------- Menu bar ----------------

    private JMenuBar buildMenuBar() {
        JMenuBar bar = new JMenuBar();

        JMenu file = new JMenu("File");
        file.add(item("Dashboard", KeyEvent.VK_D, e -> openDashboard(), false));
        file.add(item("Change Password", KeyEvent.VK_P, e -> openChangePassword(), false));
        file.addSeparator();
        file.add(item("Logout", KeyEvent.VK_L, e -> logout(), false));
        file.add(item("Exit", KeyEvent.VK_Q, e -> System.exit(0), false));

        JMenu records = new JMenu("Records");
        records.add(item("Add Student", KeyEvent.VK_N, e -> openAddStudent(), true));
        records.add(item("Add Route / Bus", KeyEvent.VK_B, e -> openRouteBus(), true));
        records.add(item("Add Fee Record", KeyEvent.VK_E, e -> openFee(), true));
        records.addSeparator();
        records.add(item("View All Records", KeyEvent.VK_R, e -> openView(), false));
        records.add(item("Update / Delete", KeyEvent.VK_U, e -> openUpdateDelete(), true));

        JMenu help = new JMenu("Help");
        JMenuItem about = new JMenuItem("About");
        about.addActionListener(e -> Util.info(this,
                "Student Transport Fee & Route Management System\n"
                        + "Made by: Mohd Dayan (PIET25CR034)\n"
                        + "Java Swing + JDBC + MySQL"));
        help.add(about);

        bar.add(file);
        bar.add(records);
        bar.add(help);
        return bar;
    }

    private JMenuItem item(String text, int key, ActionListener action, boolean adminOnly) {
        JMenuItem mi = new JMenuItem(text);
        mi.setAccelerator(KeyStroke.getKeyStroke(key, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
        mi.addActionListener(action);
        if (adminOnly && !Session.isAdmin()) {
            mi.setEnabled(false);
        }
        return mi;
    }

    // ---------------- Toolbar ----------------

    private JToolBar buildToolBar() {
        JToolBar tb = new JToolBar();
        tb.setFloatable(false);
        tb.add(toolButton("Add", "Add a new student", e -> openAddStudent(), true));
        tb.add(toolButton("Update", "Update a record", e -> openUpdateDelete(), true));
        tb.add(toolButton("Delete", "Delete a record", e -> openUpdateDelete(), true));
        tb.add(toolButton("View All", "View all records", e -> openView(), false));
        tb.addSeparator();
        tb.add(toolButton("Dashboard", "Go to dashboard", e -> openDashboard(), false));
        tb.add(toolButton("Logout", "Logout", e -> logout(), false));
        return tb;
    }

    private JButton toolButton(String text, String tip, ActionListener action, boolean adminOnly) {
        JButton b = new JButton(text);
        b.setToolTipText(tip);
        b.setFocusable(false);
        b.addActionListener(action);
        if (adminOnly && !Session.isAdmin()) {
            b.setEnabled(false);
        }
        return b;
    }

    // ---------------- Navigation ----------------

    protected void openDashboard()      { new Dashboard(); dispose(); }
    protected void openAddStudent()     { new AddStudent(); dispose(); }
    protected void openRouteBus()       { new RouteBus(); dispose(); }
    protected void openFee()            { new TransportFee(); dispose(); }
    protected void openView()           { new SearchView(); dispose(); }
    protected void openUpdateDelete()   { new UpdateDelete(); dispose(); }
    protected void openChangePassword() { new ChangePassword(); dispose(); }

    protected void logout() {
        int ok = JOptionPane.showConfirmDialog(this, "Do you want to logout?", "Logout",
                JOptionPane.YES_NO_OPTION);
        if (ok == JOptionPane.YES_OPTION) {
            Session.clear();
            new Login();
            dispose();
        }
    }

    /**
     * Screens that change data call this first. Returns false (and goes back to the
     * dashboard) if the logged-in person is not an Admin.
     */
    protected boolean requireAdmin() {
        if (Session.isAdmin()) {
            return true;
        }
        Util.error(this, "Only an Admin can use this page.");
        new Dashboard();
        dispose();
        return false;
    }
}
