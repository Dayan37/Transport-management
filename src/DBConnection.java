/*
 * Database connectivity (JDBC) for the whole project.
 * Made by: Mohd Dayan
 *
 * - getConnection() opens a connection to the MySQL database "transport_db".
 * - query()  runs a SELECT and returns all rows.
 * - update() runs INSERT / UPDATE / DELETE and returns the number of rows changed.
 * All SQL uses PreparedStatement (parameters "?") so user input can never break the query.
 *
 * Connection details are read from "db.properties" (next to the project folder).
 * If that file is missing, the defaults below are used.
 */

import java.io.File;
import java.io.FileInputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class DBConnection {

    private static String url = "jdbc:mysql://localhost:3306/transport_db"
            + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static String user = "root";
    private static String password = "root";

    static {
        // Optional settings file: db.url, db.user, db.password
        File propsFile = new File("db.properties");
        if (!propsFile.exists()) {
            propsFile = new File("transport_project/db.properties");
        }
        try (FileInputStream in = new FileInputStream(propsFile)) {
            Properties p = new Properties();
            p.load(in);
            url = p.getProperty("db.url", url);
            user = p.getProperty("db.user", user);
            password = p.getProperty("db.password", password);
        } catch (Exception ignored) {
            // no file -> keep defaults
        }
    }

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC driver not found. Add the mysql-connector-j JAR "
                    + "(from the lib folder) to the project libraries.", "08001");
        }
        return DriverManager.getConnection(url, user, password);
    }

    /** Runs a SELECT query and returns every row as an Object array. */
    public static Object[][] query(String sql, Object... params) throws SQLException {
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            bind(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                int cols = rs.getMetaData().getColumnCount();
                List<Object[]> rows = new ArrayList<>();
                while (rs.next()) {
                    Object[] row = new Object[cols];
                    for (int i = 0; i < cols; i++) {
                        row[i] = rs.getObject(i + 1);
                    }
                    rows.add(row);
                }
                return rows.toArray(new Object[0][]);
            }
        }
    }

    /** Runs INSERT / UPDATE / DELETE and returns the number of affected rows. */
    public static int update(String sql, Object... params) throws SQLException {
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            bind(ps, params);
            return ps.executeUpdate();
        }
    }

    private static void bind(PreparedStatement ps, Object[] params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            if (params[i] == null) {
                ps.setNull(i + 1, Types.NULL);
            } else {
                ps.setObject(i + 1, params[i]);
            }
        }
    }
}
