/*
 * Form validation rules used by every screen.
 * Made by: Mohd Dayan
 *
 * Each method returns null when everything is fine,
 * otherwise it returns the error message to show to the user.
 */

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class Validator {

    public static String student(String roll, String name, String course) {
        if (Util.isBlank(roll) || Util.isBlank(name) || Util.isBlank(course)) {
            return "Roll number, name and course are required.";
        }
        if (!roll.matches("[A-Za-z0-9/-]{3,20}")) {
            return "Roll number must be 3-20 letters/digits (example: PIET25CR034).";
        }
        if (!name.matches("[A-Za-z][A-Za-z .'-]{1,49}")) {
            return "Name may contain only letters and spaces (2-50 characters).";
        }
        if (course.length() > 50) {
            return "Course name is too long (maximum 50 characters).";
        }
        return null;
    }

    public static String route(String name, String source, String dest, String stops) {
        if (Util.isBlank(name) || Util.isBlank(source) || Util.isBlank(dest)) {
            return "Route name, source and destination are required.";
        }
        if (name.length() > 50 || source.length() > 60 || dest.length() > 60) {
            return "Route name (max 50), source and destination (max 60) are too long.";
        }
        if (stops != null && stops.length() > 255) {
            return "Stops text is too long (maximum 255 characters).";
        }
        return null;
    }

    public static String bus(String busNo, String capacity) {
        if (Util.isBlank(busNo) || Util.isBlank(capacity)) {
            return "Bus number and capacity are required.";
        }
        if (!busNo.matches("[A-Za-z0-9 -]{4,15}")) {
            return "Bus number must be 4-15 letters/digits (example: RJ14-2201).";
        }
        int cap;
        try {
            cap = Integer.parseInt(capacity.trim());
        } catch (NumberFormatException e) {
            return "Capacity must be a whole number.";
        }
        if (cap < 1 || cap > 120) {
            return "Capacity must be between 1 and 120.";
        }
        return null;
    }

    public static String fee(String amount, String dueDate) {
        if (Util.isBlank(amount) || Util.isBlank(dueDate)) {
            return "Amount and due date are required.";
        }
        BigDecimal value;
        try {
            value = new BigDecimal(amount.trim());
        } catch (NumberFormatException e) {
            return "Amount must be a number (example: 12000 or 12000.50).";
        }
        if (value.signum() <= 0 || value.compareTo(new BigDecimal("999999.99")) > 0) {
            return "Amount must be greater than 0 and at most 999999.99.";
        }
        if (value.scale() > 2) {
            return "Amount can have at most 2 decimal places.";
        }
        try {
            LocalDate d = LocalDate.parse(dueDate.trim());
            if (d.getYear() < 2000) {
                return "Due date year must be 2000 or later.";
            }
        } catch (DateTimeParseException e) {
            return "Due date must be a valid date in yyyy-MM-dd format (example: 2026-12-10).";
        }
        return null;
    }

    public static String password(String pass) {
        if (pass.length() < 6) {
            return "New password must be at least 6 characters long.";
        }
        return null;
    }
}
