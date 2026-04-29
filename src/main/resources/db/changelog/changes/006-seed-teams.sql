--liquibase formatted sql

--changeset pavle:006-seed-teams
INSERT INTO teams (team_id, name) VALUES
('00000000-0000-0000-0000-000000000001', 'Toronto Maple Leafs'),
('00000000-0000-0000-0000-000000000002', 'Montreal Canadiens'),
('00000000-0000-0000-0000-000000000003', 'Boston Bruins'),
('00000000-0000-0000-0000-000000000004', 'Colorado Avalanche'),
('00000000-0000-0000-0000-000000000005', 'Chicago Blackhawks'),
('00000000-0000-0000-0000-000000000006', 'Edmonton Oilers'),
('00000000-0000-0000-0000-000000000007', 'Pittsburgh Penguins'),
('00000000-0000-0000-0000-000000000008', 'Washington Capitals');