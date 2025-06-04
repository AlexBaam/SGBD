DROP VIEW IF EXISTS top10_tictactoe;
DROP VIEW IF EXISTS top10_minesweeper;

DROP TABLE IF EXISTS saved_games;
DROP TABLE IF EXISTS tictactoe_scores;
DROP TABLE IF EXISTS minesweeper_scores;
DROP TABLE IF EXISTS user_deletion_log;
DROP TABLE IF EXISTS users;

CREATE TABLE users (
                       user_id SERIAL PRIMARY KEY,
                       username VARCHAR(50) UNIQUE NOT NULL,
                       email VARCHAR(100) UNIQUE NOT NULL,
                       password TEXT NOT NULL,
                       logged_in BOOLEAN NOT NULL
);

CREATE TABLE tictactoe_scores (
                                  user_id INTEGER PRIMARY KEY,
                                  total_wins INTEGER DEFAULT 0,
                                  FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE TABLE minesweeper_scores (
                                    user_id INTEGER PRIMARY KEY,
                                    total_wins INTEGER DEFAULT 0,
                                    best_score INTEGER DEFAULT 0,
                                    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE TABLE saved_games (
                             save_id SERIAL PRIMARY KEY,
                             user_id INTEGER NOT NULL,
                             game_type VARCHAR(20),
                             game_state TEXT NOT NULL,
                             saved_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                             FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE VIEW top10_minesweeper AS
SELECT u.username, ms.best_score
FROM minesweeper_scores ms
         JOIN users u ON u.user_id = ms.user_id
WHERE ms.best_score > 0
ORDER BY ms.best_score ASC
    LIMIT 10;

CREATE VIEW top10_tictactoe AS
SELECT u.username, tts.total_wins
FROM tictactoe_scores tts
         JOIN users u ON u.user_id = tts.user_id
ORDER BY tts.total_wins DESC
    LIMIT 10;



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

-- Trigger pentru a loga stergerea unui user
CREATE TABLE user_deletion_log (
                                   log_id SERIAL PRIMARY KEY,
                                   user_id INTEGER,
                                   username VARCHAR(50),
                                   deleted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

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


CREATE OR REPLACE FUNCTION get_user_tictactoe_rank_manual(p_user_id INTEGER)
RETURNS INTEGER AS $$
DECLARE
v_user_wins INTEGER;
    v_rank INTEGER;
BEGIN
    -- 1. Obținem numărul de victorii al utilizatorului specificat
SELECT total_wins INTO v_user_wins
FROM tictactoe_scores
WHERE user_id = p_user_id;

-- Dacă utilizatorul nu are victorii sau nu există, îi dăm un rang "nedefinit" (sau 0, în funcție de logica ta)
-- sau putem arunca o excepție, dacă e cazul.
IF v_user_wins IS NULL THEN
        RETURN NULL; -- Sau o altă valoare care indică faptul că nu a fost găsit
END IF;

    -- 2. Calculăm rangul: numărăm câți utilizatori au mai multe victorii
    -- și adăugăm 1 la rezultat.
    -- (Acest lucru simulează comportamentul RANK() unde egalitățile primesc același loc,
    -- dar locul următor este sărit - ex: 1, 2, 2, 4)
SELECT COUNT(DISTINCT total_wins) + 1 INTO v_rank
FROM tictactoe_scores
WHERE total_wins > v_user_wins;

RETURN v_rank;
END;
$$ LANGUAGE plpgsql;


CREATE OR REPLACE FUNCTION manage_user_login_and_sessions()
RETURNS TRIGGER AS $$
DECLARE
BEGIN

    IF NEW.logged_in = TRUE AND OLD.logged_in = FALSE THEN
        RAISE NOTICE 'Utilizatorul "%" încearcă să se conecteze.', NEW.username;

    ELSIF NEW.logged_in = FALSE AND OLD.logged_in = TRUE THEN
        RAISE NOTICE 'Utilizatorul "%" încearcă să se deconecteze.', OLD.username;
        RAISE NOTICE 'Utilizatorul "%" s-a deconectat cu succes.', OLD.username;

    ELSIF OLD.logged_in = TRUE AND NEW.logged_in = TRUE THEN
        RAISE EXCEPTION 'Utilizatorul "%" este deja conectat!', OLD.username
        USING HINT = 'Nu poți seta logged_in la TRUE dacă este deja TRUE. Folosește o deconectare explicită (setând logged_in la FALSE) înainte de a te reconecta.';
END IF;
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE TRIGGER trg_manage_user_login_and_sessions
BEFORE UPDATE OF logged_in ON users
    FOR EACH ROW
    EXECUTE FUNCTION manage_user_login_and_sessions();
