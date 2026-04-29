--liquibase formatted sql

--changeset pavle:001-create-teams
CREATE TABLE teams (
                       team_id UUID NOT NULL PRIMARY KEY,
                       name VARCHAR(255) NOT NULL
);