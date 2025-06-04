package org.example.game_library.database.dao;

import org.example.game_library.utils.DBUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TicTacToeDAO {

    private TicTacToeDAO() {
    }

    public static void incrementWins(int userId) throws SQLException {
        String sql = """
            INSERT INTO tictactoe_scores (user_id, total_wins)
            VALUES (?, 1)
            ON CONFLICT (user_id)
            DO UPDATE SET total_wins = tictactoe_scores.total_wins + 1;
        """;

        try (Connection connection = DBUtils.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, userId);
            statement.executeUpdate();
        }
    }
}
