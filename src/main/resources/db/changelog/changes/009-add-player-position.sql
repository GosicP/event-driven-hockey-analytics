--liquibase formatted sql

--changeset pavle:009-add-player-position
ALTER TABLE players
    ADD COLUMN position VARCHAR(30) NOT NULL DEFAULT 'FORWARD';