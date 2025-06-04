package org.example.game_library.networking;

import org.example.game_library.database.model.User;
import org.example.game_library.database.repository.UserRepository;
import org.example.game_library.utils.jpa.JPAUtils;
import org.example.game_library.utils.loggers.AppLogger;

import java.io.*;
import java.net.*;
import java.util.List;
import java.util.logging.*;


import jakarta.persistence.PersistenceException;
import javax.security.auth.login.LoginException;

public class ThreadCreator extends Thread {
    private final Socket clientSocket;

    private ObjectInputStream input;
    private ObjectOutputStream output;

    private static final Logger logger = AppLogger.getLogger();
    private final long threadId;

    private boolean logged = false;
    private int currentUserId = -1; // Ptr utilizator mai tarziu
    private String currentUserName;

    public ThreadCreator(Socket socket) {
        this.clientSocket = socket;
        this.threadId = this.threadId();

        try {
            output = new ObjectOutputStream(clientSocket.getOutputStream());
            output.flush();
            input = new ObjectInputStream(clientSocket.getInputStream());
            logger.log(Level.INFO, "Streams created for thread {0}", threadId);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error setting up input stream: {0}",  e.getMessage());
        }
    }

    @Override
    public void run() {
        logger.log(Level.INFO, "Thread {0} started successfully!", threadId);

        try {

            while (true) {

                Object obj;
                try{
                    obj = input.readObject();
                } catch (EOFException e) {
                    logger.log(Level.INFO, "Thread {0} received EOF – closing connection.", threadId);
                    break;
                }

                if(!(obj instanceof List<?> list)){
                    output.writeObject("Invalid message format!");
                    continue;
                }

                //Safe cast
                @SuppressWarnings("unchecked")
                List<String> request = (List<String>) list;

                logger.log(Level.INFO, "Thread {0} received {1}", new Object[]{threadId, request});

                if(request.isEmpty()){
                    output.writeObject("Empty request!");
                    continue;
                }

                String command = request.get(0).toLowerCase();
                Command commandEnum = Command.fromString(command);

                if (commandEnum == null) {
                    output.writeObject("Invalid command!");
                    continue;
                }

                if(!logged){
                    handleUnauthenticatedCommand(commandEnum, request);
                } else {
                    handleAuthenticatedCommand(commandEnum, request);
                }

            }
        } catch (IOException | ClassNotFoundException e){
            logger.log(Level.WARNING, "Thread {0} connection error: {1}", new Object[]{threadId, e.getMessage()});
        } finally {
            try{
                if(output != null){
                    output.close();
                }
                if(input != null){
                    input.close();
                }
                clientSocket.close();
                logger.log(Level.INFO, "Thread {0} connection closed!", threadId);
            } catch(IOException e){
                logger.log(Level.SEVERE, "Thread {0} error closing streams: {1}", new Object[]{threadId, e.getMessage()});
            }
        }
    }

    private void handleUnauthenticatedCommand(Command commandEnum, List<String> request) throws IOException {
        switch(commandEnum){
            case LOGIN -> handleLogin(request);
            case REGISTER -> handleRegister(request);
            case EXIT -> handleExit(request);
            default -> output.writeObject("Command " + request.get(0) + " not yet implemented!");
        }
    }

    private void handleAuthenticatedCommand(Command commandEnum, List<String> request) throws IOException {
        switch(commandEnum){
            case LOGOUT -> handleLogout(request);
            case DELETE -> handleDelete();
            case EXIT -> handleExit(request);
            case TICTACTOE -> handleTicTacToe(request);
            case MINESWEEPER -> handleMinesweeper(request);
            default -> output.writeObject("Command " + request.get(0) + " not yet implemented!");
        }
    }

    private void handleRegister(List<String> request) throws IOException {
        if (request.size() < 4) {
            output.writeObject("Not enough arguments for REGISTER");
            return;
        }

        String email = request.get(1);
        String username = request.get(2);
        String password = request.get(3);

        UserRepository repo = new UserRepository(JPAUtils.getEntityManager());
        User user = repo.registration(email, username, password);

        if (user != null) {
            output.writeObject("SUCCESS");
        } else {
            output.writeObject("FAILURE");
        }
    }

    private void handleLogin(List<String> request) throws IOException {
        if(request.size() < 3){
            output.writeObject("Not enough arguments for LOGIN");
            return;
        }

        String username = request.get(1);
        String password = request.get(2);

        UserRepository userRepo = new UserRepository(JPAUtils.getEntityManager());
        try {
            User user = userRepo.authenticate(username, password);
            logged = true; // Marchează sesiunea ca autentificată
            currentUserId = user.getUser_id(); // Stochează ID-ul utilizatorului
            currentUserName = user.getUsername();
            output.writeObject("SUCCESS");
        } catch (org.example.game_library.utils.exceptions.LoginException e) {
            // Prindem excepția specifică de login (inclusiv "deja conectat")
            logger.log(Level.INFO, "Login failed for user {0}: {1}", new Object[]{username, e.getMessage()});
            output.writeObject(e.getMessage()); // Trimite mesajul de eroare clientului
        } catch (PersistenceException e) {
            // Prindem alte erori de persistență care nu sunt direct legate de login logică (e.g., probleme cu DB)
            logger.log(Level.SEVERE, "Database error during login for user {0}: {1}", new Object[]{username, e.getMessage()});
            output.writeObject("Eroare de bază de date la autentificare.");
        }
    }

    private void handleDelete() throws IOException {
        if (!logged || currentUserName == null) {
            output.writeObject("Nu sunteți autentificat pentru a șterge contul.");
            logger.log(Level.WARNING, "Attempted delete by unauthenticated thread {0}.", threadId);
            return;
        }

        UserRepository userRepo = new UserRepository(JPAUtils.getEntityManager());
        try {
            boolean success = userRepo.deleteUserByUsername(currentUserName);
            if (success) {
                logged = false;
                currentUserId = -1;
                currentUserName = null;
                output.writeObject("SUCCESS");
                logger.log(Level.INFO, "User {0} successfully deleted account.", currentUserName);
            } else {
                output.writeObject("Eroare la ștergerea contului. Utilizatorul nu a putut fi găsit sau șters.");
                logger.log(Level.WARNING, "Failed to delete user {0}.", currentUserName);
            }
        } catch (PersistenceException e) {
            logger.log(Level.SEVERE, "Database error during user deletion for {0}: {1}", new Object[]{currentUserName, e.getMessage()});
            output.writeObject("Eroare de bază de date la ștergerea contului: " + e.getMessage());
        }
    }

    private void handleLogout(List<String> request) throws IOException {

        if (!logged || currentUserName == null) {
            output.writeObject("Nu sunteți autentificat pentru a vă deconecta.");
            logger.log(Level.WARNING, "Attempted logout by unauthenticated thread {0}.", threadId);
            return;
        }

        UserRepository userRepo = new UserRepository(JPAUtils.getEntityManager());
        try {
            boolean success = userRepo.updateUserLoggedInStatus(currentUserName, false); // Setează logged_in la FALSE
            if (success) {
                logged = false; // Marchează sesiunea ca deconectată
                currentUserId = -1; // Resetează ID-ul utilizatorului
                currentUserName = null; // Resetează username-ul
                output.writeObject("SUCCESS");
                logger.log(Level.INFO, "User {0} successfully logged out.", currentUserName);
            } else {
                output.writeObject("Eroare la deconectare. Vă rugăm să încercați din nou.");
                logger.log(Level.WARNING, "Failed to update logged_in status to FALSE for user {0}.", currentUserName);
            }
        } catch (PersistenceException e) {
            logger.log(Level.SEVERE, "Database error during logout for user {0}: {1}", new Object[]{currentUserName, e.getMessage()});
            // Trigger-ul de logout nu ar trebui să arunce excepții, dar prindem orice eroare de DB
            output.writeObject("Eroare de bază de date la deconectare: " + e.getMessage());
        }
    }


    private void handleExit(List<String> request) throws IOException {
        handleLogout(request);
        output.writeObject("User pressed exit!");
        throw new EOFException();
    }

    private void handleMinesweeper(List<String> request) throws IOException {
        //TODO
    }

    private void handleTicTacToe(List<String> request) throws IOException {
        //TODO
    }
}
