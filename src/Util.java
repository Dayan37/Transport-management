/*
 * Common helper methods: password hashing, message boxes, database error messages.
 * Made by: Mohd Dayan
 */

import javax.swing.*;
import java.awt.Component;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.SQLException;

public class Util {

    /** SHA-256 hash of a password as 64 lowercase hex characters (same as MySQL SHA2(x,256)). */
    public static String hash(String text) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(text.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }

    public static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    public static void info(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Information", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void error(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /** Shows a friendly message for a database problem instead of a raw stack trace. */
    public static void dbError(Component parent, Exception ex) {
        String msg;
        if (ex instanceof SQLException) {
            SQLException sql = (SQLException) ex;
            String state = sql.getSQLState() == null ? "" : sql.getSQLState();
            if (sql.getErrorCode() == 1062) {
                msg = "This record already exists (duplicate roll number / name / bus number / fee entry).";
            } else if (sql.getErrorCode() == 1451) {
                msg = "This record cannot be deleted because other records depend on it.";
            } else if (sql.getErrorCode() == 1452) {
                msg = "The selected route/student does not exist any more. Please refresh and try again.";
            } else if (sql.getErrorCode() == 3819 || sql.getErrorCode() == 4025) {
                msg = "A value is outside the allowed range (check amount / capacity).";
            } else if (state.startsWith("08") || sql.getErrorCode() == 1045 || sql.getErrorCode() == 1049) {
                msg = "Cannot connect to the MySQL database.\n\n"
                        + "Check that MySQL is running, the script database/schema.sql has been run,\n"
                        + "and the user/password in db.properties are correct.\n\nDetails: " + sql.getMessage();
            } else {
                msg = "Database error: " + sql.getMessage();
            }
        } else {
            msg = "Unexpected error: " + ex.getMessage();
        }
        error(parent, msg);
    }
}
