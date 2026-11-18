/*
 * Remembers who is logged in (username + role) while the program is running.
 * Made by: Mohd Dayan
 */
public class Session {
    private static String username;
    private static String role;

    public static void start(String user, String userRole) {
        username = user;
        role = userRole;
    }

    public static void clear() {
        username = null;
        role = null;
    }

    public static String getUsername() { return username; }
    public static String getRole() { return role; }
    public static boolean isAdmin() { return "Admin".equals(role); }
}
