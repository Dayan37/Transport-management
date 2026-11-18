/*
 * Route & Bus Management Page
 * Made by: Mohd Dayan
 *
 * Top part  : add a new route   -> MySQL "routes" table
 * Bottom part: add a new bus and attach it to a route -> MySQL "buses" table
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

public class RouteBus extends BaseFrame implements ActionListener {

    JLabel titleLabel, routeNameLabel, sourceLabel, destLabel, stopsLabel,
            busNoLabel, capacityLabel, busRouteLabel;
    JTextField routeNameField, sourceField, destField, stopsField, busNoField, capacityField;
    JComboBox<Item> busRouteBox;
    JButton saveRouteButton, saveBusButton, backButton;

    public RouteBus() {

        super("Route & Bus", 460, 560);
        if (!requireAdmin()) {
            return;
        }

        titleLabel = new JLabel("Route & Bus Management");
        titleLabel.setFont(new Font("Serif", Font.BOLD, 16));
        titleLabel.setBounds(50, 5, 350, 30);
        body.add(titleLabel);

        // ---------- Route section ----------
        JLabel routeHeading = new JLabel("1. Add Route");
        routeHeading.setFont(new Font("SansSerif", Font.BOLD, 13));
        routeHeading.setBounds(40, 40, 200, 22);
        body.add(routeHeading);

        routeNameLabel = new JLabel("Route Name:");
        routeNameLabel.setBounds(40, 68, 150, 25);
        body.add(routeNameLabel);
        routeNameField = new JTextField();
        routeNameField.setBounds(200, 68, 200, 25);
        body.add(routeNameField);

        sourceLabel = new JLabel("Source:");
        sourceLabel.setBounds(40, 100, 150, 25);
        body.add(sourceLabel);
        sourceField = new JTextField();
        sourceField.setBounds(200, 100, 200, 25);
        body.add(sourceField);

        destLabel = new JLabel("Destination:");
        destLabel.setBounds(40, 132, 150, 25);
        body.add(destLabel);
        destField = new JTextField();
        destField.setBounds(200, 132, 200, 25);
        body.add(destField);

        stopsLabel = new JLabel("Stops (optional):");
        stopsLabel.setBounds(40, 164, 150, 25);
        body.add(stopsLabel);
        stopsField = new JTextField();
        stopsField.setBounds(200, 164, 200, 25);
        body.add(stopsField);

        saveRouteButton = new JButton("Save Route");
        saveRouteButton.setBounds(200, 198, 200, 28);
        saveRouteButton.addActionListener(this);
        body.add(saveRouteButton);

        // ---------- Bus section ----------
        JLabel busHeading = new JLabel("2. Add Bus");
        busHeading.setFont(new Font("SansSerif", Font.BOLD, 13));
        busHeading.setBounds(40, 240, 200, 22);
        body.add(busHeading);

        busNoLabel = new JLabel("Bus Number:");
        busNoLabel.setBounds(40, 268, 150, 25);
        body.add(busNoLabel);
        busNoField = new JTextField();
        busNoField.setBounds(200, 268, 200, 25);
        body.add(busNoField);

        capacityLabel = new JLabel("Capacity:");
        capacityLabel.setBounds(40, 300, 150, 25);
        body.add(capacityLabel);
        capacityField = new JTextField();
        capacityField.setBounds(200, 300, 200, 25);
        body.add(capacityField);

        busRouteLabel = new JLabel("Runs on Route:");
        busRouteLabel.setBounds(40, 332, 150, 25);
        body.add(busRouteLabel);
        busRouteBox = new JComboBox<>();
        busRouteBox.setBounds(200, 332, 200, 25);
        body.add(busRouteBox);
        loadRoutes();

        saveBusButton = new JButton("Save Bus");
        saveBusButton.setBounds(200, 366, 200, 28);
        saveBusButton.addActionListener(this);
        body.add(saveBusButton);

        backButton = new JButton("Back to Dashboard");
        backButton.setBounds(40, 420, 360, 28);
        backButton.addActionListener(this);
        body.add(backButton);

        setVisible(true);
    }

    private void loadRoutes() {
        try {
            busRouteBox.removeAllItems();
            for (Item item : RouteDAO.routeItems()) {
                busRouteBox.addItem(item);
            }
        } catch (SQLException ex) {
            Util.dbError(this, ex);
        }
    }

    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == saveRouteButton) {
            String name = routeNameField.getText().trim();
            String source = sourceField.getText().trim();
            String dest = destField.getText().trim();
            String stops = stopsField.getText().trim();

            String error = Validator.route(name, source, dest, stops);
            if (error != null) {
                Util.error(this, error);
                return;
            }
            try {
                RouteDAO.insertRoute(name, source, dest, stops);
                Util.info(this, "Route saved successfully!");
                routeNameField.setText("");
                sourceField.setText("");
                destField.setText("");
                stopsField.setText("");
                loadRoutes();   // new route now appears in the bus list
            } catch (SQLException ex) {
                Util.dbError(this, ex);
            }
        }

        else if (e.getSource() == saveBusButton) {
            String busNo = busNoField.getText().trim().toUpperCase();
            String capacity = capacityField.getText().trim();

            String error = Validator.bus(busNo, capacity);
            if (error != null) {
                Util.error(this, error);
                return;
            }
            Item route = (Item) busRouteBox.getSelectedItem();
            try {
                RouteDAO.insertBus(busNo, Integer.parseInt(capacity), route == null ? null : route.idOrNull());
                Util.info(this, "Bus saved successfully!");
                busNoField.setText("");
                capacityField.setText("");
                busRouteBox.setSelectedIndex(0);
            } catch (SQLException ex) {
                Util.dbError(this, ex);
            }
        }

        else if (e.getSource() == backButton) {
            openDashboard();
        }
    }
}
