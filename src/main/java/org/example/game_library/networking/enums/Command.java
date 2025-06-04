package org.example.game_library.networking.enums;

public enum Command {

    LOGIN("login"),
    REGISTER("register"),
    LOGOUT("logout"),
    DELETE("delete"),
    EXIT("exit"),
    MINESWEEPER("minesweeper"),
    TICTACTOE("tictactoe");

    private final String command;

    Command(String command) {
        this.command = command;
    }

    public static Command fromString(String value) {
        for(Command c : Command.values()) {
            if(c.command.equalsIgnoreCase(value)) {
                return c;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return this.command;
    }
}
