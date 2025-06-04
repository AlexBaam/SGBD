package org.example.game_library.utils.loggers;
import java.io.File;
import java.io.IOException;
import java.util.logging.*;

public class AppLogger {

    private static final Logger logger = Logger.getLogger("GameLibraryLogger");

    static {
        try {
            LogManager.getLogManager().reset();
            logger.setLevel(Level.ALL);

            File logDir = new File("logs");
            if(!logDir.exists()){
                logDir.mkdirs();
            }

            // Use colored console output
            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setLevel(Level.ALL);
            consoleHandler.setFormatter(new LoggerColor()); // Aici setez ca loggerul meu sa fie setat dupa propriile reguli
            logger.addHandler(consoleHandler);

            FileHandler fileHandler = new FileHandler("logs/game_library.log", true);
            fileHandler.setLevel(Level.ALL);
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);

        } catch (IOException e) {
            logger.severe("Failed to initialize logger: " + e.getMessage());
        }
    }

    public static Logger getLogger() {
        return logger;
    }
}