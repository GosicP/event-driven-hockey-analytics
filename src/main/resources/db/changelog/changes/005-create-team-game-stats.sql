--liquibase formatted sql

--changeset pavle:005-create-team-game-stats
CREATE TABLE team_game_stats (
     id UUID NOT NULL PRIMARY KEY,
     game_id UUID NOT NULL,
     team_id UUID NOT NULL,
     shots INTEGER NOT NULL DEFAULT 0,
     goals INTEGER NOT NULL DEFAULT 0,
     penalties INTEGER NOT NULL DEFAULT 0,

     CONSTRAINT fk_team_game_stats_game
         FOREIGN KEY (game_id) REFERENCES games(game_id),

     CONSTRAINT fk_team_game_stats_team
         FOREIGN KEY (team_id) REFERENCES teams(team_id),

     CONSTRAINT uq_team_game_stats_game_team UNIQUE (game_id, team_id)
);