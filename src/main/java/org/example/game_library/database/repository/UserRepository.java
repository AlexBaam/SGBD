package org.example.game_library.database.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceException;
import org.example.game_library.database.model.User;
import org.example.game_library.utils.loggers.AppLogger;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.mindrot.jbcrypt.BCrypt;

import org.example.game_library.utils.exceptions.LoginException;


public class UserRepository {
    private static final Logger logger = AppLogger.getLogger();

    private final EntityManager em;

    public UserRepository(EntityManager em) {
        this.em = em;
    }

    public User findByUsername(String username) {
        try {
            return em.createQuery("SELECT u FROM User u WHERE u.username = :username", User.class)
                    .setParameter("username", username)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * Authenticates a user and updates their logged_in status.
     * @param username The username to authenticate.
     * @param password The password to check.
     * @return The User object if authentication and login status update are successful.
     * @throws LoginException If authentication fails (bad credentials) or login status update fails (e.g., user already logged in).
     * @throws PersistenceException For other database-related issues not directly from the trigger.
     */
    public User authenticate(String username, String password) throws LoginException, PersistenceException {
        User user = findByUsername(username);

        if (user == null) {
            logger.log(Level.WARNING, "No user with username {0} found in the database!", username);
            throw new LoginException("Credențiale invalide (utilizator inexistent).");
        }

        logger.log(Level.INFO, "User with username {0} found.", username);

        if (!BCrypt.checkpw(password, user.getPassword())) {
            logger.log(Level.WARNING, "Password mismatch for user {0}!", username);
            throw new LoginException("Credențiale invalide (parolă incorectă).");
        }

        logger.log(Level.INFO, "Password matched for user {0}. Attempting to set logged_in status.", username);

        try {
            // Încercăm să setăm starea de login.
            // Dacă trigger-ul din DB aruncă excepția "Utilizatorul este deja conectat!",
            // aceasta va fi prinsă aici ca o PersistenceException.
            boolean success = updateUserLoggedInStatus(user.getUsername(), true); // Folosim username pentru update

            if (success) {
                logger.log(Level.INFO, "User {0} logged in successfully!", username);
                return user;
            } else {
                // Această ramură ar trebui atinsă doar dacă updateUserLoggedInStatus returnează false
                // fără a arunca o excepție (situație improbabilă aici dacă user-ul e găsit inițial)
                logger.log(Level.WARNING, "Failed to update logged_in status for user {0} without an explicit exception.", username);
                throw new LoginException("A apărut o eroare necunoscută la actualizarea stării de login.");
            }
        } catch (PersistenceException e) {
            // Aici prindem excepțiile aruncate de trigger-ul PostgreSQL.
            Throwable cause = e.getCause();
            if (cause instanceof SQLException) {
                SQLException sqlException = (SQLException) cause;
                String dbMessage = sqlException.getMessage();
                logger.log(Level.WARNING, "SQL Exception during login status update for {0}: {1}", new Object[]{username, dbMessage});

                // Verificați dacă mesajul se potrivește cu cel aruncat de trigger
                if (dbMessage != null && dbMessage.contains("este deja conectat!")) {
                    throw new LoginException("Utilizatorul " + username + " este deja conectat!", sqlException);
                }
            }
            logger.log(Level.SEVERE, "Persistence error during login status update for {0}: {1}", new Object[]{username, e.getMessage()});
            throw e; // Re-aruncă eroarea JPA originală pentru alte probleme de persistență
        }
    }

    /**
     * Updates the logged_in status for a given user.
     * This method will trigger the PostgreSQL trigger for login/logout check.
     *
     * @param username The username of the user.
     * @param loggedInStatus The new logged in status (true for login, false for logout).
     * @return true if the status was successfully updated, false otherwise.
     * @throws PersistenceException if a database error occurs (e.g., trigger exception).
     */
    public boolean updateUserLoggedInStatus(String username, boolean loggedInStatus) throws PersistenceException {
        em.getTransaction().begin();
        try {
            User user = findByUsername(username); // Re-găsim user-ul în cadrul aceleiași tranzacții
            if (user == null) {
                logger.log(Level.WARNING, "User {0} not found for status update (inside transaction).", username);
                em.getTransaction().rollback();
                return false;
            }

            user.setLoggedIn(loggedInStatus);
            // Dacă ai adăugat last_login în User:
            // if (loggedInStatus) {
            //     user.setLastLogin(LocalDateTime.now());
            // } else {
            //     user.setLastLogin(null); // Sau lași ultima valoare, depinde de logică
            // }

            em.merge(user); // JPA va genera UPDATE statement-ul
            em.getTransaction().commit();
            logger.log(Level.INFO, "User {0} logged_in status set to {1} successfully.", new Object[]{username, loggedInStatus});
            return true;
        } catch (PersistenceException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            logger.log(Level.SEVERE, "Error updating logged_in status for {0}: {1}", new Object[]{username, e.getMessage()});
            // Re-aruncă excepția JPA, ea va fi prinsă în metoda 'authenticate' pentru o gestionare mai specifică
            throw e;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            logger.log(Level.SEVERE, "An unexpected error occurred during updateUserLoggedInStatus for {0}: {1}", new Object[]{username, e.getMessage()});
            throw new PersistenceException("An unexpected error occurred.", e);
        } finally {
            // Asigură-te că EntityManager-ul este închis/gestionat corect dacă e cazul
            // JPAUtils.closeEntityManager(em); // Sau cum gestionezi tu EntityManager-ul
        }
    }

    public User registration (String email, String username, String password) {

        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        User user = new User(username, email, hashedPassword);

        em.getTransaction().begin(); // Start a new transaction
        try {
            em.persist(user);
            em.getTransaction().commit();
            logger.log(Level.INFO, "User {0} registered successfully.", username);
            return user;
        } catch (PersistenceException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            logger.log(Level.SEVERE, "Error during user registration for {0}: {1}", new Object[]{username, e.getMessage()});

            // Attempt to extract the specific message from the PostgreSQL trigger
            Throwable cause = e.getCause();
            if (cause instanceof SQLException) {
                SQLException sqlException = (SQLException) cause;
                logger.log(Level.SEVERE, "SQL Exception details: SQLState={0}, ErrorCode={1}, Message={2}",
                        new Object[]{sqlException.getSQLState(), sqlException.getErrorCode(), sqlException.getMessage()});
                // The message from your PL/pgSQL RAISE EXCEPTION will be in sqlException.getMessage()
                logger.log(Level.SEVERE, "Database trigger message: {0}", sqlException.getMessage());
            } else if (cause != null) {
                logger.log(Level.SEVERE, "Underlying cause of PersistenceException: {0}", cause.getMessage());
            }

            return null;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            logger.log(Level.SEVERE, "An unexpected error occurred during user registration for {0}: {1}", new Object[]{username, e.getMessage()});
            return null;
        }
    }

    public boolean deleteUserByUsername(String username) throws PersistenceException {
        em.getTransaction().begin();
        try {
            User user = findByUsername(username);
            if (user == null) {
                logger.log(Level.WARNING, "User {0} not found for deletion.", username);
                em.getTransaction().rollback();
                return false;
            }

            em.remove(user); // Aceasta va declanșa trigger-ul log_user_deletion
            em.getTransaction().commit();
            logger.log(Level.INFO, "User {0} successfully deleted from the database.", username);
            return true;
        } catch (PersistenceException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            logger.log(Level.SEVERE, "Error deleting user {0}: {1}", new Object[]{username, e.getMessage()});
            // Propagăm excepția pentru gestionare ulterioară (e.g., afișare mesaj de eroare clientului)
            throw e;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            logger.log(Level.SEVERE, "An unexpected error occurred during user deletion for {0}: {1}", new Object[]{username, e.getMessage()});
            throw new PersistenceException("An unexpected error occurred during user deletion.", e);
        }
    }
}
