/*
 * Search / View All Records Page
 * Made by: Mohd Dayan
 *
 * Shows every student's transport record (from MySQL) in a JTable.
 * - Type in the search box to filter (roll no, name, course or route).
 * - Click a column heading to sort.
 * - "Export CSV" saves the table you see as a report file (opens in Excel).
 */

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

public class SearchView extends BaseFrame implements ActionListener {

    private static final String[] COLUMNS = {"Roll No", "Name", "Course", "Route", "Bus No", "Fee Status"};

    JLabel searchLabel, countLabel;
    JTextField searchField;
    JTable table;
    DefaultTableModel model;
    JButton refreshButton, exportButton, updateDeleteButton, backButton;

    public SearchView() {

        super("View All Records", 760, 460);
        body.setLayout(new BorderLayout(8, 8));
        body.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));

        // ---- top: search ----
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        searchLabel = new JLabel("Search:");
        top.add(searchLabel);
        searchField = new JTextField(22);
        top.add(searchField);
        refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(this);
        top.add(refreshButton);
        exportButton = new JButton("Export CSV");
        exportButton.addActionListener(this);
        top.add(exportButton);
        countLabel = new JLabel(" ");
        top.add(countLabel);
        body.add(top, BorderLayout.NORTH);

        // live search while typing
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { loadData(); }
            public void removeUpdate(DocumentEvent e) { loadData(); }
            public void changedUpdate(DocumentEvent e) { loadData(); }
        });

        // ---- centre: table ----
        model = new DefaultTableModel(COLUMNS, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(model);
        table.setAutoCreateRowSorter(true);   // click headings to sort
        table.setFillsViewportHeight(true);
        body.add(new JScrollPane(table), BorderLayout.CENTER);

        // ---- bottom: buttons ----
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        backButton = new JButton("Back to Dashboard");
        backButton.addActionListener(this);
        bottom.add(backButton);
        updateDeleteButton = new JButton("Update / Delete Records");
        updateDeleteButton.addActionListener(this);
        updateDeleteButton.setEnabled(Session.isAdmin());
        bottom.add(updateDeleteButton);
        body.add(bottom, BorderLayout.SOUTH);

        loadData();
        setVisible(true);
    }

    private void loadData() {
        try {
            Object[][] rows = StudentDAO.searchForView(searchField.getText().trim());
            model.setRowCount(0);
            for (Object[] r : rows) {
                model.addRow(r);
            }
            countLabel.setText(rows.length + " record(s)");
        } catch (SQLException ex) {
            model.setRowCount(0);
            countLabel.setText("0 record(s)");
            Util.dbError(this, ex);
        }
    }

    private void exportCsv() {
        if (model.getRowCount() == 0) {
            Util.info(this, "There are no records to export.");
            return;
        }
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("transport_records.csv"));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        try (PrintWriter out = new PrintWriter(chooser.getSelectedFile(), StandardCharsets.UTF_8)) {
            out.println(String.join(",", COLUMNS));
            // export in the order currently shown on screen (respects sorting)
            for (int viewRow = 0; viewRow < table.getRowCount(); viewRow++) {
                int modelRow = table.convertRowIndexToModel(viewRow);
                StringBuilder line = new StringBuilder();
                for (int c = 0; c < COLUMNS.length; c++) {
                    if (c > 0) line.append(',');
                    String cell = String.valueOf(model.getValueAt(modelRow, c));
                    line.append('"').append(cell.replace("\"", "\"\"")).append('"');
                }
                out.println(line);
            }
            Util.info(this, "Exported to " + chooser.getSelectedFile().getName());
        } catch (Exception ex) {
            Util.error(this, "Could not save the file: " + ex.getMessage());
        }
    }

    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == refreshButton) {
            loadData();
        }

        else if (e.getSource() == exportButton) {
            exportCsv();
        }

        else if (e.getSource() == updateDeleteButton) {
            openUpdateDelete();
        }

        else if (e.getSource() == backButton) {
            openDashboard();
        }
    }
}
