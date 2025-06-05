ALTER SEQUENCE users_user_id_seq RESTART WITH 1;
ALTER SEQUENCE game_types_game_type_id_seq RESTART WITH 1;
ALTER SEQUENCE saved_games_save_id_seq RESTART WITH 1;
ALTER SEQUENCE user_deletion_log_log_id_seq RESTART WITH 1;

DELETE FROM saved_games;
DELETE FROM tictactoe_scores;
DELETE FROM minesweeper_scores;
DELETE FROM user_deletion_log;
DELETE FROM users;
DELETE FROM game_types;

INSERT INTO game_types (name) VALUES ('tictactoe');
INSERT INTO game_types (name) VALUES ('minesweeper');

DO $$
DECLARE
lista_nume VARCHAR[] := ARRAY['Popescu','Ionescu','Dumitrescu','Georgescu','Popa','Stan','Stoica','Rusu','Mihai','Gheorghe','Dobre','Petrescu'];
    lista_prenume_fete VARCHAR[] := ARRAY['Maria','Elena','Andreea','Alexandra','Ioana','Ana','Cristina','Larisa'];
    lista_prenume_baieti VARCHAR[] := ARRAY['Andrei','Alexandru','Mihai','Ionut','Adrian','George','Bogdan','Cristian'];

    v_nume VARCHAR(50);
    v_prenume VARCHAR(50);
    v_username VARCHAR(50);
    v_email VARCHAR(100);
    v_password TEXT;
    v_temp_count INTEGER;
BEGIN
FOR i IN 1..100 LOOP
        v_nume := lista_nume[1 + floor(random() * array_length(lista_nume, 1))];
        IF random() < 0.5 THEN
            v_prenume := lista_prenume_fete[1 + floor(random() * array_length(lista_prenume_fete, 1))];
ELSE
            v_prenume := lista_prenume_baieti[1 + floor(random() * array_length(lista_prenume_baieti, 1))];
END IF;

        LOOP
v_username := lower(v_prenume || '_' || v_nume || floor(random() * 999)::TEXT);
SELECT COUNT(*) INTO v_temp_count FROM users WHERE username = v_username;
EXIT WHEN v_temp_count = 0;
END LOOP;

        LOOP
v_email := lower(v_prenume || '.' || v_nume || floor(random() * 999)::TEXT || '@example.com');
SELECT COUNT(*) INTO v_temp_count FROM users WHERE email = v_email;
EXIT WHEN v_temp_count = 0;
END LOOP;

        v_password := 'pass_' || i;

INSERT INTO users (username, email, password, logged_in)
VALUES (v_username, v_email, v_password, FALSE);
END LOOP;
END $$;

DO $$
DECLARE
v_user_id INTEGER;
BEGIN
FOR v_user_id IN 1..100 LOOP
UPDATE minesweeper_scores
SET total_wins = floor(random() * 50)::INTEGER,
            best_score = floor(random() * 49001 + 1000)::INTEGER
WHERE user_id = v_user_id;

UPDATE tictactoe_scores
SET total_wins = floor(random() * 100)::INTEGER
WHERE user_id = v_user_id;
END LOOP;
END $$;

DO $$
DECLARE
v_user_id INTEGER;
    v_game_type_id INTEGER;
    v_game_state JSONB;
BEGIN
FOR i IN 1..50 LOOP
        v_user_id := floor(random() * 100) + 1;
        v_game_type_id := floor(random() * 2) + 1;

        IF v_game_type_id = 1 THEN
            v_game_state := '{"board": ["X", null, "O", null, "X", null, "O", null, "X"], "turn": "O", "status": "finished"}';
ELSE
            v_game_state := '{"board_size": "10x10", "mines_count": 10, "revealed": [[0,0],[0,1]], "flags": [[1,1]], "status": "in_progress"}';
END IF;

INSERT INTO saved_games (user_id, game_type_id, game_state, saved_at)
VALUES (v_user_id, v_game_type_id, v_game_state, NOW() - (random() * 365 || ' days')::INTERVAL);
END LOOP;
END $$;

DELETE FROM users WHERE user_id IN (
    SELECT user_id FROM users ORDER BY random() FETCH FIRST 3 ROWS ONLY
);

SELECT 'Total users: ' || COUNT(*)::TEXT FROM users;
SELECT 'Total game types: ' || COUNT(*)::TEXT FROM game_types;
SELECT 'Total tictactoe scores: ' || COUNT(*)::TEXT FROM tictactoe_scores;
SELECT 'Total minesweeper scores: ' || COUNT(*)::TEXT FROM minesweeper_scores;
SELECT 'Total saved games: ' || COUNT(*)::TEXT FROM saved_games;
SELECT 'Total user deletion logs: ' || COUNT(*)::TEXT FROM user_deletion_log;

SELECT * FROM top10_minesweeper;

SELECT * FROM get_tictactoe_top_ranked_players(5);