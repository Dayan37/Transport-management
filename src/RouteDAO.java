/*
 * Database operations for the "routes" and "buses" tables.
 * Made by: Mohd Dayan
 */

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RouteDAO {

    // ---------------- Routes ----------------

    public static int insertRoute(String name, String source, String dest, String stops) throws SQLException {
        return DBConnection.update(
                "INSERT INTO routes (route_name, source, destination, stops) VALUES (?, ?, ?, ?)",
                name, source, dest, Util.isBlank(stops) ? null : stops);
    }

    /** Columns: id, route name, source, destination, stops */
    public static Object[][] searchRoutes(String q) throws SQLException {
        String like = "%" + q + "%";
        return DBConnection.query(
                "SELECT route_id, route_name, source, destination, IFNULL(stops, '') FROM routes "
                        + "WHERE route_name LIKE ? OR source LIKE ? OR destination LIKE ? OR stops LIKE ? "
                        + "ORDER BY route_name", like, like, like, like);
    }

    public static int updateRoute(int id, String name, String source, String dest, String stops) throws SQLException {
        return DBConnection.update(
                "UPDATE routes SET route_name = ?, source = ?, destination = ?, stops = ? WHERE route_id = ?",
                name, source, dest, Util.isBlank(stops) ? null : stops, id);
    }

    public static int deleteRoute(int id) throws SQLException {
        return DBConnection.update("DELETE FROM routes WHERE route_id = ?", id);
    }

    /** All routes for a combo box. The first entry (id 0) means "no route". */
    public static List<Item> routeItems() throws SQLException {
        List<Item> items = new ArrayList<>();
        items.add(new Item(0, "-- Select Route --"));
        for (Object[] r : DBConnection.query("SELECT route_id, route_name FROM routes ORDER BY route_name")) {
            items.add(new Item(((Number) r[0]).intValue(), String.valueOf(r[1])));
        }
        return items;
    }

    // ---------------- Buses ----------------

    public static int insertBus(String busNo, int capacity, Integer routeId) throws SQLException {
        return DBConnection.update(
                "INSERT INTO buses (bus_no, capacity, route_id) VALUES (?, ?, ?)", busNo, capacity, routeId);
    }

    /** Columns: id, bus no, capacity, route name */
    public static Object[][] searchBuses(String q) throws SQLException {
        String like = "%" + q + "%";
        return DBConnection.query(
                "SELECT b.bus_id, b.bus_no, b.capacity, IFNULL(r.route_name, '-') FROM buses b "
                        + "LEFT JOIN routes r ON r.route_id = b.route_id "
                        + "WHERE b.bus_no LIKE ? OR r.route_name LIKE ? ORDER BY b.bus_no", like, like);
    }

    public static int updateBus(int id, String busNo, int capacity, Integer routeId) throws SQLException {
        return DBConnection.update(
                "UPDATE buses SET bus_no = ?, capacity = ?, route_id = ? WHERE bus_id = ?",
                busNo, capacity, routeId, id);
    }

    public static int deleteBus(int id) throws SQLException {
        return DBConnection.update("DELETE FROM buses WHERE bus_id = ?", id);
    }
}
