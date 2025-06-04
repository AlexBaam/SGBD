package org.example.game_library.networking;

public enum CommandTicTacToe {

    NEWGAME("newgame"),
    LOADGAME("loadgame"),
    SCORE("score"),
    EXIT("exit"),
    FORFEIT("forfeit"),
    MOVE("move");

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
