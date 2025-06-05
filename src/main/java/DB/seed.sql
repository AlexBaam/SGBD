DROP VIEW IF EXISTS top10_minesweeper;

DROP TABLE IF EXISTS saved_games;
DROP TABLE IF EXISTS tictactoe_scores;
DROP TABLE IF EXISTS minesweeper_scores;
DROP TABLE IF EXISTS user_deletion_log;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS game_types;

CREATE TABLE users (
                       user_id SERIAL PRIMARY KEY,
                       username VARCHAR(50) UNIQUE NOT NULL,
                       email VARCHAR(100) UNIQUE NOT NULL,
                       password TEXT NOT NULL,
                       logged_in BOOLEAN NOT NULL
);

CREATE TABLE game_types (
                            game_type_id SERIAL PRIMARY KEY,
                            name VARCHAR(50) UNIQUE NOT NULL
);

CREATE TABLE tictactoe_scores (
                                  user_id INTEGER PRIMARY KEY,
                                  total_wins INTEGER DEFAULT 0,
                                  FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE TABLE minesweeper_scores (
                                    user_id INTEGER PRIMARY KEY,
                                    total_wins INTEGER DEFAULT 0,
                                    best_score INTEGER DEFAULT 0,
                                    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE TABLE saved_games (
                             save_id SERIAL PRIMARY KEY,
                             user_id INTEGER NOT NULL,
                             game_type_id INTEGER NOT NULL,
                             game_state JSONB NOT NULL,
                             saved_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                             FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
                             FOREIGN KEY (game_type_id) REFERENCES game_types(game_type_id)
);

CREATE TABLE user_deletion_log (
                                   log_id SERIAL PRIMARY KEY,
                                   user_id INTEGER,
                                   username VARCHAR(50),
                                   deleted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE VIEW top10_minesweeper AS
SELECT u.username, ms.best_score
FROM minesweeper_scores ms
         JOIN users u ON u.user_id = ms.user_id
WHERE ms.best_score > 0
ORDER BY ms.best_score ASC
    LIMIT 10;

CREATE OR REPLACE FUNCTION get_user_tictactoe_rank_manual(p_user_id INTEGER)
RETURNS INTEGER AS $$
DECLARE
v_user_wins INTEGER;
    v_rank INTEGER;
BEGIN
SELECT total_wins INTO v_user_wins
FROM tictactoe_scores
WHERE user_id = p_user_id;

IF v_user_wins IS NULL THEN
        RETURN NULL;
END IF;

SELECT COUNT(DISTINCT total_wins) + 1 INTO v_rank
FROM tictactoe_scores
WHERE total_wins > v_user_wins;

RETURN v_rank;
END;
$$ LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION get_tictactoe_top_ranked_players(p_top_ranks INTEGER)
RETURNS TABLE (
    rank_nr BIGINT,
    username VARCHAR(50),
    games_played INTEGER
) AS $$
BEGIN
RETURN QUERY
    WITH RankedScores AS (
        SELECT
            u.username,
            tts.total_wins AS games_played,
            DENSE_RANK() OVER (ORDER BY tts.total_wins DESC) as current_rank
        FROM
            tictactoe_scores tts
        JOIN
            users u ON u.user_id = tts.user_id
    )
SELECT
    rs.current_rank,
    rs.username,
    rs.games_played
FROM
    RankedScores rs
WHERE
    rs.current_rank <= p_top_ranks
ORDER BY
    rs.current_rank ASC, rs.games_played DESC, rs.username ASC;
END;
$$ LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION check_email_and_username_uniqueness()
RETURNS TRIGGER AS $$
DECLARE
v_email_existent INTEGER;
    v_username_existent INTEGER;
BEGIN
SELECT COUNT(*) INTO v_email_existent FROM users WHERE email = NEW.email;
IF v_email_existent > 0 THEN
        RAISE EXCEPTION 'Email already exists: %', NEW.email;
END IF;

SELECT COUNT(*) INTO v_username_existent FROM users WHERE username = NEW.username;
IF v_username_existent > 0 THEN
        RAISE EXCEPTION 'Username already exists: %', NEW.username;
END IF;

RETURN NEW;
END;
$$ LANGUAGE plpgsql;







CREATE TRIGGER validate_user_insert
    BEFORE INSERT ON users
    FOR EACH ROW
    EXECUTE FUNCTION check_email_and_username_uniqueness();

CREATE OR REPLACE FUNCTION log_user_deletion()
RETURNS TRIGGER AS $$
BEGIN
INSERT INTO user_deletion_log (user_id, username)
VALUES (OLD.user_id, OLD.username);
RETURN OLD;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_log_user_deletion
    BEFORE DELETE ON users
    FOR EACH ROW
    EXECUTE FUNCTION log_user_deletion();






CREATE OR REPLACE FUNCTION manage_user_login_and_sessions()
RETURNS TRIGGER AS $$
DECLARE
BEGIN
    IF NEW.logged_in = TRUE AND OLD.logged_in = FALSE THEN
        RAISE NOTICE 'Utilizatorul "%" incearca sa se conecteze.', NEW.username;
    ELSIF NEW.logged_in = FALSE AND OLD.logged_in = TRUE THEN
        RAISE NOTICE 'Utilizatorul "%" incearca sa se deconecteze.', OLD.username;
        RAISE NOTICE 'Utilizatorul "%" s-a deconectat cu succes.', OLD.username;
    ELSIF OLD.logged_in = TRUE AND NEW.logged_in = TRUE THEN
        RAISE EXCEPTION 'Utilizatorul "%" este deja conectat!', OLD.username
        USING HINT = 'Nu poti seta logged_in la TRUE daca este deja TRUE. Foloseste o deconectare explicita (setand logged_in la FALSE) inainte de a te reconecta.';
END IF;
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER trg_manage_user_login_and_sessions
BEFORE UPDATE OF logged_in ON users
    FOR EACH ROW
    EXECUTE FUNCTION manage_user_login_and_sessions();

CREATE OR REPLACE FUNCTION create_score_entries_after_user_insert()
RETURNS TRIGGER AS $$
BEGIN
INSERT INTO tictactoe_scores(user_id) VALUES (NEW.user_id);
INSERT INTO minesweeper_scores(user_id) VALUES (NEW.user_id);
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_create_scores_after_user_insert
    AFTER INSERT ON users
    FOR EACH ROW
    EXECUTE FUNCTION create_score_entries_after_user_insert();