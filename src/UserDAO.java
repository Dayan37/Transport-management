/*
 * Database operations for the "users" table (login + change password).
 * Made by: Mohd Dayan
 */

import java.sql.SQLException;

public class UserDAO {

    /** True if username + password + role match a row in the users table. */
    public static boolean authenticate(String username, String password, String role) throws SQLException {
        Object[][] rows = DBConnection.query(
                "SELECT user_id FROM users WHERE username = ? AND password = ? AND role = ?",
                username, Util.hash(password), role);
        return rows.length == 1;
    }

    /** Changes the password only if the old password is correct. Returns false if it was wrong. */
    public static boolean changePassword(String username, String oldPassword, String newPassword) throws SQLException {
        int changed = DBConnection.update(
                "UPDATE users SET password = ? WHERE username = ? AND password = ?",
                Util.hash(newPassword), username, Util.hash(oldPassword));
        return changed == 1;
    }
}
