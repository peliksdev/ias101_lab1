
 package com.ias101.lab1.utils;

import com.ias101.lab1.database.util.DBUtil;
import com.ias101.lab1.model.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Utility class for performing CRUD operations on user data in the database.
 */
public class Crud {
    private static final String DB_URL = "jdbc:sqlite:src/main/resources/database/sample.db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "root";

    private static final Pattern SAFE_INPUT_PATTERN = Pattern.compile("^[a-zA-Z0-9!@#$%^&*()_+=-{}\\[\\]:;\"'<>,.?/~`|\\\\]+$");

    public static List<User> getAll() {
        List<User> users = new ArrayList<>();
        try (Connection connection = DBUtil.connect(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM user_data")) {

            while (rs.next()) {
                users.add(extractUserFromResultSet(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching all users", e);
        }
        return users;
    }

    public static User searchByUsername(String username) {
        if (!isValidInput(username)) {
            throw new IllegalArgumentException("Invalid input detected.");
        }
        username = sanitize(username);
        User user = null;
        try (Connection connection = DBUtil.connect(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM user_data WHERE username = '" + username + "'")) {

            if (rs.next()) {
                user = extractUserFromResultSet(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error searching for user: " + username, e);
        }
        return user;
    }

    public static void deleteUserByUsername(String username) {
        if (!isValidInput(username)) {
            throw new IllegalArgumentException("Invalid input detected.");
        }
        username = sanitize(username);

        try (Connection connection = DBUtil.connect(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = connection.createStatement()) {

            String query = "DELETE FROM user_data WHERE username = '" + username + "'";
            int affectedRows = stmt.executeUpdate(query);

            if (affectedRows > 0) {
                System.out.println("User deleted successfully.");
            } else {
                System.out.println("User not found.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting user: " + username, e);
        }
    }

    private static boolean isValidInput(String input) {
        return SAFE_INPUT_PATTERN.matcher(input).matches();
    }

    private static String sanitize(String input) {
        return input.replace("'", "''"); // Escaping single quotes to prevent SQL injection
    }

    private static User extractUserFromResultSet(ResultSet rs) throws SQLException {
        return new User(rs.getString("username"), rs.getString("password"));
    }
}
