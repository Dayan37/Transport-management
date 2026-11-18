/*
 * Database operations for the "transport_fees" table.
 * Made by: Mohd Dayan
 */

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;

public class FeeDAO {

    public static int insert(int studentId, String amount, String dueDate, String status) throws SQLException {
        return DBConnection.update(
                "INSERT INTO transport_fees (student_id, amount, due_date, status) VALUES (?, ?, ?, ?)",
                studentId, new BigDecimal(amount.trim()), Date.valueOf(LocalDate.parse(dueDate.trim())), status);
    }

    /** Columns: id, roll no, student name, amount, due date, status */
    public static Object[][] search(String q) throws SQLException {
        String like = "%" + q + "%";
        return DBConnection.query(
                "SELECT f.fee_id, s.roll_no, s.name, f.amount, f.due_date, f.status "
                        + "FROM transport_fees f JOIN students s ON s.student_id = f.student_id "
                        + "WHERE s.roll_no LIKE ? OR s.name LIKE ? OR f.status LIKE ? "
                        + "ORDER BY f.due_date DESC, s.roll_no", like, like, like);
    }

    public static int update(int id, String amount, String dueDate, String status) throws SQLException {
        return DBConnection.update(
                "UPDATE transport_fees SET amount = ?, due_date = ?, status = ? WHERE fee_id = ?",
                new BigDecimal(amount.trim()), Date.valueOf(LocalDate.parse(dueDate.trim())), status, id);
    }

    public static int delete(int id) throws SQLException {
        return DBConnection.update("DELETE FROM transport_fees WHERE fee_id = ?", id);
    }

    /** Dashboard numbers: students, routes, buses, pending fee records. */
    public static Object[] dashboardCounts() throws SQLException {
        return DBConnection.query(
                "SELECT (SELECT COUNT(*) FROM students), (SELECT COUNT(*) FROM routes), "
                        + "(SELECT COUNT(*) FROM buses), "
                        + "(SELECT COUNT(*) FROM transport_fees WHERE status = 'Pending')")[0];
    }
}
