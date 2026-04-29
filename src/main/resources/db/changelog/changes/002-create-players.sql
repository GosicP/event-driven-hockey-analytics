--liquibase formatted sql

--changeset pavle:002-create-players
CREATE TABLE players (
                         player_id UUID NOT NULL PRIMARY KEY,
                         name VARCHAR(255) NOT NULL,
                         team_id UUID NOT NULL,
                         CONSTRAINT fk_players_team
                             FOREIGN KEY (team_id) REFERENCES teams(team_id)
);