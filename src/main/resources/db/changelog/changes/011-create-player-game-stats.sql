--liquibase formatted sql

--changeset pavle:011-create-player-game-stats
CREATE TABLE player_game_stats (
                                   id UUID NOT NULL PRIMARY KEY,
                                   game_id UUID NOT NULL,
                                   player_id UUID NOT NULL,
                                   team_id UUID NOT NULL,
                                   shots INTEGER NOT NULL DEFAULT 0,
                                   goals INTEGER NOT NULL DEFAULT 0,
                                   penalties INTEGER NOT NULL DEFAULT 0,

                                   CONSTRAINT fk_player_game_stats_game
                                       FOREIGN KEY (game_id) REFERENCES games(game_id),

                                   CONSTRAINT fk_player_game_stats_player
                                       FOREIGN KEY (player_id) REFERENCES players(player_id),

                                   CONSTRAINT fk_player_game_stats_team
                                       FOREIGN KEY (team_id) REFERENCES teams(team_id),

                                   CONSTRAINT uq_player_game_stats_game_player UNIQUE (game_id, player_id)
);