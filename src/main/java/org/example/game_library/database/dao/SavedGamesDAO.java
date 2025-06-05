package org.example.game_library.database.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.game_library.networking.server.tictactoe_game_logic.TicTacToeGame;
import org.example.game_library.utils.DBUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class SavedGamesDAO {

    private static final ObjectMapper mapper = new ObjectMapper();

    private SavedGamesDAO() {}

    public static void saveGame(int userId, String gameTypeName, TicTacToeGame game) throws Exception {
        String insertSQL = """
            INSERT INTO saved_games (user_id, game_type_id, game_state)
            VALUES (?, 
                (SELECT game_type_id FROM game_types WHERE name = ?), 
                ?::jsonb)
        """;

        try (Connection conn = DBUtils.getConnection();
             PreparedStatement stmt = conn.prepareStatement(insertSQL)) {

            String gameStateJson = mapper.writeValueAsString(game);

            stmt.setInt(1, userId);
            stmt.setString(2, gameTypeName.toLowerCase());
            stmt.setString(3, gameStateJson);
            stmt.executeUpdate();
        }
    }

    public static List<TicTacToeGame> loadGamesForUser(int userId, String gameTypeName) throws Exception {
        String selectSQL = """
            SELECT game_state FROM saved_games
            WHERE user_id = ? AND game_type_id = (
                SELECT game_type_id FROM game_types WHERE name = ?
            )
            ORDER BY saved_at DESC
        """;

        List<TicTacToeGame> games = new ArrayList<>();

        try (Connection conn = DBUtils.getConnection();
             PreparedStatement stmt = conn.prepareStatement(selectSQL)) {

            stmt.setInt(1, userId);
            stmt.setString(2, gameTypeName.toLowerCase());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String json = rs.getString("game_state");
                    TicTacToeGame game = mapper.readValue(json, TicTacToeGame.class);
                    games.add(game);
                }
            }
        }

        return games;
    }

    public static TicTacToeGame loadGameById(int saveId) throws Exception {
        String selectSQL = "SELECT game_state FROM saved_games WHERE save_id = ?";

        try (Connection conn = DBUtils.getConnection();
             PreparedStatement stmt = conn.prepareStatement(selectSQL)) {

            stmt.setInt(1, saveId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String json = rs.getString("game_state");
                    return mapper.readValue(json, TicTacToeGame.class);
                }
            }
        }

        return null;
    }
}
