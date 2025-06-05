package org.example.game_library.networking.enums;

public enum CommandTicTacToe {

    NEWGAME("newgame"),
    LOADGAME("load"),
    SCORE("score"),
    EXIT("exit"),
    FORFEIT("forfeit"),
    MOVE("move"),
    SAVEGAME("save");

    private final String commandTicTacToe;

    CommandTicTacToe(String command) {
        this.commandTicTacToe = command;
    }

    public static CommandTicTacToe fromString(String value) {
        for(CommandTicTacToe c : CommandTicTacToe.values()) {
            if(c.commandTicTacToe.equalsIgnoreCase(value)) {
                return c;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return this.commandTicTacToe;
    }
}
