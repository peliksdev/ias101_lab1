package com.ias101.lab1.security;

import com.ias101.lab1.database.util.DBUtil;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.regex.Pattern;

/**
 * Authentication class for user validation
 */
public class Authenticator {
    private static final Pattern SAFE_INPUT_PATTERN = Pattern.compile("^[a-zA-Z0-9!@#$%^&*()_+=-{}\\[\\]:;\"'<>,.?/~`|\\\\]+$");

    /**
     * Authenticates a user by checking username and password against database
     *
     * @param username The username to authenticate
     * @param password The password to authenticate
     * @return boolean Returns true if authentication is successful, false otherwise
     */
    public static boolean authenticateUser(String username, String password) {
        if (!isValidInput(username) || !isValidInput(password)) {
            System.err.println("SQL injection detected!");
            return false;
        }

        username = sanitize(username);
        password = sanitize(password);

        String query = "SELECT * FROM user_data WHERE username = '" + username + "' AND password = '" + password + "'";

        try (Connection conn = DBUtil.connect("jdbc:sqlite:src/main/resources/database/sample.db", "root", "root");
             Statement statement = conn.createStatement();
             ResultSet rs = statement.executeQuery(query)) {

            return rs.next();
        } catch (SQLException e) {
            throw new RuntimeException("Database error: " + e.getMessage(), e);
        }
    }

    private static boolean isValidInput(String input) {
        return SAFE_INPUT_PATTERN.matcher(input).matches();
    }

    private static String sanitize(String input) {
        return input.replace("'", "''"); // Escaping single quotes to prevent SQL injection
    }
}