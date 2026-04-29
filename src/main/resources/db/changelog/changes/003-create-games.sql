--liquibase formatted sql

--changeset pavle:003-create-games
CREATE TABLE games (
                       game_id UUID NOT NULL PRIMARY KEY,
                       home_team_id UUID NOT NULL,
                       away_team_id UUID NOT NULL,
                       game_date TIMESTAMP WITH TIME ZONE NOT NULL,

                       CONSTRAINT fk_games_home_team
                           FOREIGN KEY (home_team_id) REFERENCES teams(team_id),

                       CONSTRAINT fk_games_away_team
                           FOREIGN KEY (away_team_id) REFERENCES teams(team_id)
);