/*
 * Database operations for the "students" table.
 * Made by: Mohd Dayan
 */

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {

    public static int insert(String roll, String name, String course, Integer routeId) throws SQLException {
        return DBConnection.update(
                "INSERT INTO students (roll_no, name, course, route_id) VALUES (?, ?, ?, ?)",
                roll, name, course, routeId);
    }

    /** Columns for the View All screen: roll, name, course, route, bus numbers, fee status */
    public static Object[][] searchForView(String q) throws SQLException {
        String like = "%" + q + "%";
        return DBConnection.query(
                "SELECT s.roll_no, s.name, s.course, IFNULL(r.route_name, '-'), "
                        + "IFNULL((SELECT GROUP_CONCAT(b.bus_no SEPARATOR ', ') FROM buses b "
                        + "        WHERE b.route_id = s.route_id), '-'), "
                        + "CASE WHEN EXISTS (SELECT 1 FROM transport_fees f WHERE f.student_id = s.student_id "
                        + "                  AND f.status = 'Pending') THEN 'Pending' "
                        + "     WHEN EXISTS (SELECT 1 FROM transport_fees f WHERE f.student_id = s.student_id) "
                        + "          THEN 'Paid' ELSE 'No Fee Record' END "
                        + "FROM students s LEFT JOIN routes r ON r.route_id = s.route_id "
                        + "WHERE s.roll_no LIKE ? OR s.name LIKE ? OR s.course LIKE ? OR r.route_name LIKE ? "
                        + "ORDER BY s.roll_no", like, like, like, like);
    }

    /** Columns for the Update/Delete screen: id, roll, name, course, route name */
    public static Object[][] searchForEdit(String q) throws SQLException {
        String like = "%" + q + "%";
        return DBConnection.query(
                "SELECT s.student_id, s.roll_no, s.name, s.course, IFNULL(r.route_name, '-') "
                        + "FROM students s LEFT JOIN routes r ON r.route_id = s.route_id "
                        + "WHERE s.roll_no LIKE ? OR s.name LIKE ? OR s.course LIKE ? OR r.route_name LIKE ? "
                        + "ORDER BY s.roll_no", like, like, like, like);
    }

    public static int update(int id, String roll, String name, String course, Integer routeId) throws SQLException {
        return DBConnection.update(
                "UPDATE students SET roll_no = ?, name = ?, course = ?, route_id = ? WHERE student_id = ?",
                roll, name, course, routeId, id);
    }

    /** Deleting a student also deletes that student's fee records (ON DELETE CASCADE). */
    public static int delete(int id) throws SQLException {
        return DBConnection.update("DELETE FROM students WHERE student_id = ?", id);
    }

    /** Students for a combo box, shown as "roll - name". First entry (id 0) = nothing selected. */
    public static List<Item> studentItems() throws SQLException {
        List<Item> items = new ArrayList<>();
        items.add(new Item(0, "-- Select Student --"));
        for (Object[] r : DBConnection.query("SELECT student_id, roll_no, name FROM students ORDER BY roll_no")) {
            items.add(new Item(((Number) r[0]).intValue(), r[1] + " - " + r[2]));
        }
        return items;
    }

    public static String routeNameOf(int studentId) throws SQLException {
        Object[][] rows = DBConnection.query(
                "SELECT IFNULL(r.route_name, 'No route assigned') FROM students s "
                        + "LEFT JOIN routes r ON r.route_id = s.route_id WHERE s.student_id = ?", studentId);
        return rows.length == 0 ? "" : String.valueOf(rows[0][0]);
    }
}
