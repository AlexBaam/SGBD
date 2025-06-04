package org.example.game_library.networking;

import org.example.game_library.utils.loggers.AppLogger;

import java.io.*;
import java.net.*;
import java.util.logging.*;

public class ServerMain {
    private static final Logger logger = AppLogger.getLogger();

    public static void main(String[] args) {
        int port = 5000;

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            logger.log(Level.INFO, "Server started! Listening on port: {0}", port);

            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();

                    logger.log(Level.INFO, "Accepted new client successfully!");

                    // Create and start a new thread for the client
                    ThreadCreator clientHandler = new ThreadCreator(clientSocket);
                    clientHandler.start();
                } catch (IOException e) {
                    logger.log(Level.WARNING, "Failed to accept client connection!");
                    logger.log(Level.WARNING, "Exception: {0}", e.getMessage());
                }
            }

        } catch (IOException e) {
            logger.log(Level.SEVERE, "Could not start the server on port: {0}", port);
            logger.log(Level.SEVERE, "Exception: {0}", e.getMessage());
        }
    }
}