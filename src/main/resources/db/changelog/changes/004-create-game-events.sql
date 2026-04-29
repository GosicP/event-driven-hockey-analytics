--liquibase formatted sql

--changeset pavle:004-create-game-events

CREATE TABLE game_events (
                             event_id UUID NOT NULL PRIMARY KEY,
                             game_id UUID NOT NULL,
                             event_type VARCHAR(100) NOT NULL,
                             event_time TIMESTAMP WITH TIME ZONE NOT NULL,
                             period_number INTEGER,
                             player_id UUID,
                             sequence_number INTEGER NOT NULL,
                             team_id UUID,

                             CONSTRAINT fk_game_events_game
                                 FOREIGN KEY (game_id) REFERENCES games(game_id),

                             CONSTRAINT fk_game_events_player
                                 FOREIGN KEY (player_id) REFERENCES players(player_id),

                             CONSTRAINT fk_game_events_team
                                 FOREIGN KEY (team_id) REFERENCES teams(team_id)
);