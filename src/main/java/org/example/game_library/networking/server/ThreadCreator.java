package org.example.game_library.networking.server;

import org.example.game_library.database.model.User;
import org.example.game_library.database.dao.UserDAO;
import org.example.game_library.networking.enums.Command;
import org.example.game_library.networking.enums.CommandTicTacToe;
import org.example.game_library.networking.server.tictactoe_game_logic.TicTacToeGame;
import org.example.game_library.networking.server.tictactoe_game_logic.TicTacToeRequests;
import org.example.game_library.utils.loggers.AppLogger;

import java.io.*;
import java.net.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.*;

import jakarta.persistence.PersistenceException;
import org.example.game_library.networking.server.tictactoe_game_logic.ScoreEntry;

public class ThreadCreator extends Thread {
    private final Socket clientSocket;

    private ObjectInputStream input;
    private ObjectOutputStream output;

    private static final Logger logger = AppLogger.getLogger();
    private final long threadId;

    private boolean logged = false;
    private int currentUserId = -1;
    private String currentUserName;

    private TicTacToeGame ticTacToeGame;

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

        UserDAO userRepo = new UserDAO();
        User user = userRepo.registration(email, username, password);

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

        UserDAO userRepo = new UserDAO();
        try {
            User user = userRepo.authenticate(username, password);
            logged = true;
            currentUserId = user.getUser_id();
            currentUserName = user.getUsername();
            output.writeObject("SUCCESS");
        } catch (org.example.game_library.utils.exceptions.LoginException e) {

            logger.log(Level.INFO, "Login failed for user {0}: {1}", new Object[]{username, e.getMessage()});
            output.writeObject(e.getMessage());
        }
    }

    private void handleDelete() throws IOException {
        if (!logged || currentUserName == null) {
            output.writeObject("Nu sunteti autentificat pentru a sterge contul.");
            logger.log(Level.WARNING, "Attempted delete by unauthenticated thread {0}.", threadId);
            return;
        }

        UserDAO userRepo = new UserDAO();
        try {
            boolean success = userRepo.deleteUserByUsername(currentUserName);
            if (success) {
                logged = false;
                currentUserId = -1;
                currentUserName = null;
                output.writeObject("SUCCESS");
                logger.log(Level.INFO, "User {0} successfully deleted account.", currentUserName);
            } else {
                output.writeObject("Eroare la stergerea contului. Utilizatorul nu a putut fi gasit sau sters.");
                logger.log(Level.WARNING, "Failed to delete user {0}.", currentUserName);
            }
        } catch (PersistenceException e) {
            logger.log(Level.SEVERE, "Database error during user deletion for {0}: {1}", new Object[]{currentUserName, e.getMessage()});
            output.writeObject("Eroare de baza de date la stergerea contului: " + e.getMessage());
        }
    }

    private void handleLogout(List<String> request) throws IOException {

        if (!logged || currentUserName == null) {
            output.writeObject("Nu sunteti autentificat pentru a va deconecta.");
            logger.log(Level.WARNING, "Attempted logout by unauthenticated thread {0}.", threadId);
            return;
        }

        UserDAO userRepo = new UserDAO();
        try {
            boolean success = userRepo.updateUserLoggedInStatus(currentUserName, false);
            if (success) {
                logged = false;
                currentUserId = -1;
                currentUserName = null;
                output.writeObject("SUCCESS");
                logger.log(Level.INFO, "User {0} successfully logged out.", currentUserName);
            } else {
                output.writeObject("Eroare la deconectare. Va rugam sa incercati din nou.");
                logger.log(Level.WARNING, "Failed to update logged_in status to FALSE for user {0}.", currentUserName);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Database error during logout for user {0}: {1}", new Object[]{currentUserName, e.getMessage()});
            output.writeObject("Eroare de baza de date la deconectare: " + e.getMessage());
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
        if(request.size() == 1) {
            output.writeObject("SUCCESS");
        } else if(request.size() >= 2) {
            String commandTicTacToe = request.get(1);
            CommandTicTacToe cTTT = CommandTicTacToe.fromString(commandTicTacToe);

            if(cTTT == null) {
                output.writeObject("TicTacToe command is null! Command: " + commandTicTacToe);
                return;
            }

            switch (cTTT) {
                case NEWGAME -> TicTacToeRequests.handleNewGame(request, this, output, input);
                case LOADGAME -> TicTacToeRequests.handleLoadGame(request, this, output, input);
                case SAVEGAME -> TicTacToeRequests.handleSaveGame(request, this, output, input);
                case SCORE -> TicTacToeRequests.handleScore(request,this, output, input);
                case FORFEIT -> TicTacToeRequests.handleForfeit(request,this, output, input);
                case MOVE -> TicTacToeRequests.handleMove(request, this, output, input);
                case EXIT -> handleExit(request);
                default -> output.writeObject("Command " + request.get(1) + " not yet implemented!");
            }
        } else {
            output.writeObject("FAILURE");
        }
    }

    public void setTicTacToeGame(TicTacToeGame ticTacToeGame) {
        this.ticTacToeGame = ticTacToeGame;
    }

    public TicTacToeGame getTicTacToeGame() {
        return ticTacToeGame;
    }

    public int getCurrentUserId() {
        return currentUserId;
    }
}

