/*
 * Update / Delete Page
 * Made by: Mohd Dayan
 *
 * Choose what to manage (Students, Routes, Buses, Fee Records), search, select a row, then
 *   - Edit   -> opens a form with the current values; saved with a MySQL UPDATE
 *   - Delete -> asks for confirmation, then runs a MySQL DELETE
 * Admin only.
 */

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UpdateDelete extends BaseFrame implements ActionListener {

    private static final int STUDENTS = 0, ROUTES = 1, BUSES = 2, FEES = 3;

    private static final String[] ENTITY_NAMES = {"Students", "Routes", "Buses", "Fee Records"};
    private static final String[][] COLUMNS = {
            {"ID", "Roll No", "Name", "Course", "Route"},
            {"ID", "Route Name", "Source", "Destination", "Stops"},
            {"ID", "Bus No", "Capacity", "Route"},
            {"ID", "Roll No", "Student", "Amount", "Due Date", "Status"}
    };

    // field types for the edit form
    private static final int TEXT = 0, ROUTE = 1, STATUS = 2;

    /** Something that saves the edited values to the database. */
    private interface Saver {
        void save(Object[] values) throws SQLException;
    }

    /** Something that checks the edited values; returns an error message or null. */
    private interface Checker {
        String check(Object[] values);
    }

    JLabel manageLabel, searchLabel, countLabel;
    JComboBox<String> entityBox;
    JTextField searchField;
    JTable table;
    DefaultTableModel model;
    JButton searchButton, editButton, deleteButton, backButton;

    public UpdateDelete() {

        super("Update / Delete", 720, 470);
        if (!requireAdmin()) {
            return;
        }
        body.setLayout(new BorderLayout(8, 8));
        body.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));

        // ---- top: what to manage + search ----
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        manageLabel = new JLabel("Manage:");
        top.add(manageLabel);
        entityBox = new JComboBox<>(ENTITY_NAMES);
        entityBox.addActionListener(this);
        top.add(entityBox);
        searchLabel = new JLabel("Search:");
        top.add(searchLabel);
        searchField = new JTextField(16);
        top.add(searchField);
        searchButton = new JButton("Search");
        searchButton.addActionListener(this);
        top.add(searchButton);
        countLabel = new JLabel(" ");
        top.add(countLabel);
        body.add(top, BorderLayout.NORTH);
        getRootPane().setDefaultButton(searchButton);

        // ---- centre: table ----
        model = new DefaultTableModel();
        table = new JTable(model) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        table.setAutoCreateRowSorter(true);
        table.setFillsViewportHeight(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        body.add(new JScrollPane(table), BorderLayout.CENTER);

        // ---- bottom: buttons ----
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        editButton = new JButton("Edit / Update Selected");
        editButton.addActionListener(this);
        bottom.add(editButton);
        deleteButton = new JButton("Delete Selected");
        deleteButton.addActionListener(this);
        bottom.add(deleteButton);
        backButton = new JButton("Back to Dashboard");
        backButton.addActionListener(this);
        bottom.add(backButton);
        body.add(bottom, BorderLayout.SOUTH);

        loadData();
        setVisible(true);
    }

    // ---------------- loading ----------------

    private void loadData() {
        int entity = entityBox.getSelectedIndex();
        String q = searchField.getText().trim();
        try {
            Object[][] rows;
            switch (entity) {
                case STUDENTS: rows = StudentDAO.searchForEdit(q); break;
                case ROUTES:   rows = RouteDAO.searchRoutes(q); break;
                case BUSES:    rows = RouteDAO.searchBuses(q); break;
                default:       rows = FeeDAO.search(q); break;
            }
            model.setDataVector(rows, COLUMNS[entity]);
            countLabel.setText(rows.length + " record(s)");
        } catch (SQLException ex) {
            model.setDataVector(new Object[0][], COLUMNS[entity]);
            countLabel.setText("0 record(s)");
            Util.dbError(this, ex);
        }
    }

    /** Returns the selected row (in database order) or null after showing a message. */
    private Object[] selectedRow(String action) {
        int viewRow = table.getSelectedRow();
        if (viewRow == -1) {
            Util.error(this, "Please select a row to " + action + ".");
            return null;
        }
        int modelRow = table.convertRowIndexToModel(viewRow);
        Object[] row = new Object[model.getColumnCount()];
        for (int c = 0; c < row.length; c++) {
            row[c] = model.getValueAt(modelRow, c);
        }
        return row;
    }

    // ---------------- edit ----------------

    private void editSelected() {
        Object[] row = selectedRow("update");
        if (row == null) {
            return;
        }
        int id = ((Number) row[0]).intValue();

        switch (entityBox.getSelectedIndex()) {
            case STUDENTS:
                edit("Edit Student", new String[]{"Roll No", "Name", "Course", "Route"},
                        new int[]{TEXT, TEXT, TEXT, ROUTE},
                        new Object[]{row[1], row[2], row[3], row[4]},
                        v -> Validator.student(text(v[0]), text(v[1]), text(v[2])),
                        v -> StudentDAO.update(id, text(v[0]).toUpperCase(), text(v[1]), text(v[2]),
                                ((Item) v[3]).idOrNull()));
                break;

            case ROUTES:
                edit("Edit Route", new String[]{"Route Name", "Source", "Destination", "Stops"},
                        new int[]{TEXT, TEXT, TEXT, TEXT},
                        new Object[]{row[1], row[2], row[3], row[4]},
                        v -> Validator.route(text(v[0]), text(v[1]), text(v[2]), text(v[3])),
                        v -> RouteDAO.updateRoute(id, text(v[0]), text(v[1]), text(v[2]), text(v[3])));
                break;

            case BUSES:
                edit("Edit Bus", new String[]{"Bus No", "Capacity", "Route"},
                        new int[]{TEXT, TEXT, ROUTE},
                        new Object[]{row[1], row[2], row[3]},
                        v -> Validator.bus(text(v[0]), text(v[1])),
                        v -> RouteDAO.updateBus(id, text(v[0]).toUpperCase(), Integer.parseInt(text(v[1])),
                                ((Item) v[2]).idOrNull()));
                break;

            default:
                edit("Edit Fee Record  (" + row[1] + " - " + row[2] + ")",
                        new String[]{"Amount (Rs)", "Due Date (yyyy-MM-dd)", "Status"},
                        new int[]{TEXT, TEXT, STATUS},
                        new Object[]{row[3], row[4], row[5]},
                        v -> Validator.fee(text(v[0]), text(v[1])),
                        v -> FeeDAO.update(id, text(v[0]), text(v[1]), String.valueOf(v[2])));
                break;
        }
    }

    private static String text(Object o) {
        return o == null ? "" : o.toString().trim();
    }

    /**
     * Shows the edit form again and again until the values are valid (or the user cancels),
     * then saves and refreshes the table.
     */
    private void edit(String title, String[] labels, int[] types, Object[] initial,
                      Checker checker, Saver saver) {
        Object[] current = initial;
        try {
            List<Item> routes = new ArrayList<>();
            for (int t : types) {
                if (t == ROUTE) {
                    routes = RouteDAO.routeItems();
                    break;
                }
            }
            while (true) {
                Object[] values = showForm(title, labels, types, current, routes);
                if (values == null) {
                    return;                      // cancelled
                }
                String error = checker.check(values);
                if (error == null) {
                    saver.save(values);
                    Util.info(this, "Record updated successfully!");
                    loadData();
                    return;
                }
                Util.error(this, error);
                current = values;                // keep what the user typed
            }
        } catch (SQLException ex) {
            Util.dbError(this, ex);
        }
    }

    /** Builds a small form dialog. Returns the entered values, or null if cancelled. */
    private Object[] showForm(String title, String[] labels, int[] types, Object[] initial, List<Item> routes) {
        JPanel panel = new JPanel(new GridLayout(labels.length, 2, 8, 8));
        List<JComponent> fields = new ArrayList<>();

        for (int i = 0; i < labels.length; i++) {
            panel.add(new JLabel(labels[i] + ":"));
            String start = initial[i] == null ? "" : initial[i].toString();
            JComponent field;

            if (types[i] == ROUTE) {
                JComboBox<Item> box = new JComboBox<>(routes.toArray(new Item[0]));
                for (Item it : routes) {
                    if (it.label.equals(start)) {
                        box.setSelectedItem(it);
                    }
                }
                field = box;
            } else if (types[i] == STATUS) {
                JComboBox<String> box = new JComboBox<>(new String[]{"Pending", "Paid"});
                box.setSelectedItem(start);
                field = box;
            } else {
                field = new JTextField(start, 18);
            }
            fields.add(field);
            panel.add(field);
        }

        int result = JOptionPane.showConfirmDialog(this, panel, title,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) {
            return null;
        }

        Object[] values = new Object[labels.length];
        for (int i = 0; i < values.length; i++) {
            JComponent f = fields.get(i);
            values[i] = (f instanceof JTextField) ? ((JTextField) f).getText().trim()
                    : ((JComboBox<?>) f).getSelectedItem();
        }
        return values;
    }

    // ---------------- delete ----------------

    private void deleteSelected() {
        Object[] row = selectedRow("delete");
        if (row == null) {
            return;
        }
        int id = ((Number) row[0]).intValue();
        int entity = entityBox.getSelectedIndex();

        String warning = "Are you sure you want to permanently delete this record?";
        if (entity == STUDENTS) {
            warning += "\n\nThis also deletes all fee records of this student.";
        } else if (entity == ROUTES) {
            warning += "\n\nStudents and buses on this route will be left without a route.";
        }

        int confirm = JOptionPane.showConfirmDialog(this, warning, "Confirm Delete",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            switch (entity) {
                case STUDENTS: StudentDAO.delete(id); break;
                case ROUTES:   RouteDAO.deleteRoute(id); break;
                case BUSES:    RouteDAO.deleteBus(id); break;
                default:       FeeDAO.delete(id); break;
            }
            Util.info(this, "Record deleted.");
            loadData();
        } catch (SQLException ex) {
            Util.dbError(this, ex);
        }
    }

    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == entityBox) {
            searchField.setText("");
            loadData();
        }

        else if (e.getSource() == searchButton) {
            loadData();
        }

        else if (e.getSource() == editButton) {
            editSelected();
        }

        else if (e.getSource() == deleteButton) {
            deleteSelected();
        }

        else if (e.getSource() == backButton) {
            openDashboard();
        }
    }
}
