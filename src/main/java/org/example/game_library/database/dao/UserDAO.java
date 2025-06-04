package org.example.game_library.database.dao;

import org.example.game_library.database.model.User;
import org.example.game_library.utils.DBUtils;
import org.example.game_library.utils.exceptions.LoginException;
import org.example.game_library.utils.loggers.AppLogger;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserDAO {
    private static final Logger logger = AppLogger.getLogger();

    public User findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = DBUtils.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToUser(rs);
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Eroare la findByUsername: " + e.getMessage());
        }
        return null;
    }

    public User authenticate(String username, String password) throws LoginException {
        User user = findByUsername(username);
        if (user == null) {
            throw new LoginException("Utilizator inexistent.");
        }

        if (!BCrypt.checkpw(password, user.getPassword())) {
            throw new LoginException("Parolă incorectă.");
        }

        try {
            boolean updated = updateUserLoggedInStatus(username, true);
            if (!updated) {
                throw new LoginException("Eroare la actualizarea stării de login.");
            }
            return user;
        } catch (SQLException e) {
            if (e.getMessage() != null && e.getMessage().contains("este deja conectat")) {
                throw new LoginException("Utilizatorul este deja conectat!", e);
            }
            throw new LoginException("Eroare la login: " + e.getMessage(), e);
        }
    }

    public boolean updateUserLoggedInStatus(String username, boolean status) throws SQLException {
        String sql = "UPDATE users SET logged_in = ? WHERE username = ?";
        try (Connection conn = DBUtils.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBoolean(1, status);
            stmt.setString(2, username);
            int affected = stmt.executeUpdate();
            return affected > 0;
        }
    }

    public User registration(String email, String username, String password) {
        String sql = "INSERT INTO users (username, email, password, logged_in) VALUES (?, ?, ?, FALSE)";
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        try (Connection conn = DBUtils.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, username);
            stmt.setString(2, email);
            stmt.setString(3, hashedPassword);
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                int userId = rs.getInt(1);
                User newUser = new User(username, email, hashedPassword);
                newUser.setUser_id(userId);
                return newUser;
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Eroare la înregistrare: " + e.getMessage());
            if (e.getMessage().contains("already exists")) {
                logger.severe("Trigger a detectat un duplicat: " + e.getMessage());
            }
        }
        return null;
    }

    public boolean deleteUserByUsername(String username) {
        String sql = "DELETE FROM users WHERE username = ?";
        try (Connection conn = DBUtils.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            int affected = stmt.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Eroare la ștergerea userului {0}: {1}", new Object[]{username, e.getMessage()});
            return false;
        }
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUser_id(rs.getInt("user_id"));
        user.setUsername(rs.getString("username"));
        user.setEmail(rs.getString("email"));
        user.setPassword(rs.getString("password"));
        user.setLoggedIn(rs.getBoolean("logged_in"));
        return user;
    }
}
