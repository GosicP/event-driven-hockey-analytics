# Hockey Analytics - Event-Driven Simulation System

This project is a Spring Boot application for simulating hockey games and processing game statistics using an event-driven architecture.

The system generates hockey game events, sends them through Apache Kafka, processes them asynchronously, stores raw events as the source of truth, and maintains derived statistics for teams and players.

## Main Idea

The main goal of the project is to demonstrate an event-driven analytics system where:

- hockey games are simulated through domain events
- events are published to Apache Kafka
- a Kafka consumer processes events one by one
- raw events are stored in the `game_events` table
- team and player statistics are derived from those events
- statistics can be deleted and reconstructed using replay
- multiple games can be simulated in parallel without mixing statistics

## Architecture

Although the application is implemented as a single deployable Spring Boot application, it follows event-driven principles.

The simulation component is decoupled from statistics processing through Apache Kafka. This means that the simulator and analytics processor could later be extracted into separate microservices without changing the core data flow.

### High-level flow

```text
POST /api/games/{gameId}/simulate
        |
        v
GameSimulationService
        |
        v
GameEventProducer
        |
        v
Kafka topic: game-events
        |
        v
GameEventConsumer
        |
        v
GameEventProcessingService
        |
        v
game_events + team_game_stats + player_game_stats
```

## Main Components

### GameSimulationService

Responsible for simulating a hockey game by generating game events.

The simulation currently generates events for three periods. Each period contains a fixed number of generated events.

Example configuration:

```java
private static final int EVENTS_PER_PERIOD = 22;
private static final int GOAL_CHANCE_PERCENT = 8;
private static final int PENALTY_CHANCE_PERCENT = 8;
```

This produces more realistic hockey results, with approximately 25-40 shots per team and a reasonable number of goals.

### GameEventProducer

Publishes generated game events to Kafka.

### GameEventConsumer

Consumes game events from Kafka and forwards them to the event processing service.

### GameEventProcessingService

Processes a single game event and updates:

- `game_events`
- `team_game_stats`
- `player_game_stats`

### GameReplayService

Reconstructs statistics from stored events.

This proves that `game_events` acts as the source of truth and that statistics tables are derived/projection tables.

## Database Model

### `game_events`

Stores all events that happened during a game.

This table is the source of truth.

Important fields:

- `event_id`
- `game_id`
- `event_type`
- `event_time`
- `period_number`
- `sequence_number`
- `team_id`
- `player_id`

### `team_game_stats`

Stores derived team statistics for a single game.

Includes:

- `shots`
- `goals`
- `penalties`

### `player_game_stats`

Stores derived player statistics for a single game.

Includes:

- `shots`
- `goals`
- `penalties`

## Event Types

The simulation currently supports events such as:

- `SHOT`
- `GOAL`
- `PENALTY`

A `GOAL` is also counted as a shot, because in hockey every goal is also a shot on goal.

## API Endpoints

### Simulate game

```http
POST /api/games/{gameId}/simulate
```

Example:

```bash
curl -i -X POST http://localhost:8080/api/games/90000000-0000-0000-0000-000000000001/simulate
```

If the game has already been simulated, the API returns:

```http
409 Conflict
```

Example response:

```json
{
  "code": "GAME_ALREADY_SIMULATED",
  "message": "Game already simulated: 90000000-0000-0000-0000-000000000001"
}
```

### Replay game statistics

```http
POST /api/games/{gameId}/replay
```

Example:

```bash
curl -i -X POST http://localhost:8080/api/games/90000000-0000-0000-0000-000000000001/replay
```

The replay mechanism deletes existing derived statistics for the selected game and reconstructs them from `game_events`.

## Running the Project

### Start PostgreSQL

Example using Docker:

```bash
docker run -d \
  --name hockey-postgres \
  -e POSTGRES_USER=hockey \
  -e POSTGRES_PASSWORD=hockey \
  -e POSTGRES_DB=hockey_db \
  -p 5432:5432 \
  postgres:16
```

### Start Kafka

Kafka must be running before starting the application.

The application uses a Kafka topic named:

```text
game-events
```

### Start Spring Boot application

```bash
./gradlew bootRun
```

## Testing the System

### 1. Single game simulation test

Run:

```bash
curl -i -X POST http://localhost:8080/api/games/90000000-0000-0000-0000-000000000001/simulate
```

Verify events:

```sql
SELECT 
    game_id,
    COUNT(*) AS total_events,
    COUNT(*) FILTER (WHERE event_type = 'SHOT') AS shot_events,
    COUNT(*) FILTER (WHERE event_type = 'GOAL') AS goal_events,
    COUNT(*) FILTER (WHERE event_type = 'PENALTY') AS penalty_events
FROM game_events
WHERE game_id = '90000000-0000-0000-0000-000000000001'
GROUP BY game_id;
```

Expected result:

- approximately 66 events
- realistic number of shots
- realistic number of goals
- realistic number of penalties

### 2. Statistics validation

Compare stored team statistics with expected statistics calculated directly from events.

```sql
SELECT 
    game_id,
    team_id,
    COUNT(*) FILTER (WHERE event_type = 'SHOT')
      + COUNT(*) FILTER (WHERE event_type = 'GOAL') AS expected_shots,
    COUNT(*) FILTER (WHERE event_type = 'GOAL') AS expected_goals,
    COUNT(*) FILTER (WHERE event_type = 'PENALTY') AS expected_penalties
FROM game_events
WHERE game_id = '90000000-0000-0000-0000-000000000001'
GROUP BY game_id, team_id
ORDER BY game_id, team_id;
```

Then compare with:

```sql
SELECT 
    game_id,
    team_id,
    shots,
    goals,
    penalties
FROM team_game_stats
WHERE game_id = '90000000-0000-0000-0000-000000000001'
ORDER BY game_id, team_id;
```

The values should match.

### 3. Replay test

Delete derived statistics:

```sql
DELETE FROM player_game_stats
WHERE game_id = '90000000-0000-0000-0000-000000000001';

DELETE FROM team_game_stats
WHERE game_id = '90000000-0000-0000-0000-000000000001';
```

Keep events:

```sql
SELECT COUNT(*)
FROM game_events
WHERE game_id = '90000000-0000-0000-0000-000000000001';
```

Run replay:

```bash
curl -i -X POST http://localhost:8080/api/games/90000000-0000-0000-0000-000000000001/replay
```

Verify that `team_game_stats` and `player_game_stats` were reconstructed correctly.

### 4. Duplicate simulation protection

Run the same simulation twice:

```bash
curl -i -X POST http://localhost:8080/api/games/90000000-0000-0000-0000-000000000001/simulate
```

Expected result on the second request:

```http
409 Conflict
```

This prevents duplicate event generation and protects statistics from being corrupted.

### 5. Parallel simulation test

The final Kafka test simulates three games in parallel.

First clean previous data:

```sql
DELETE FROM player_game_stats
WHERE game_id IN (
    '90000000-0000-0000-0000-000000000001',
    '90000000-0000-0000-0000-000000000002',
    '90000000-0000-0000-0000-000000000003'
);

DELETE FROM team_game_stats
WHERE game_id IN (
    '90000000-0000-0000-0000-000000000001',
    '90000000-0000-0000-0000-000000000002',
    '90000000-0000-0000-0000-000000000003'
);

DELETE FROM game_events
WHERE game_id IN (
    '90000000-0000-0000-0000-000000000001',
    '90000000-0000-0000-0000-000000000002',
    '90000000-0000-0000-0000-000000000003'
);
```

Run three simulations in parallel:

```bash
curl -i -X POST http://localhost:8080/api/games/90000000-0000-0000-0000-000000000001/simulate > /tmp/game1.txt &

curl -i -X POST http://localhost:8080/api/games/90000000-0000-0000-0000-000000000002/simulate > /tmp/game2.txt &

curl -i -X POST http://localhost:8080/api/games/90000000-0000-0000-0000-000000000003/simulate > /tmp/game3.txt &

wait
```

Verify responses:

```bash
cat /tmp/game1.txt
cat /tmp/game2.txt
cat /tmp/game3.txt
```

Verify events:

```sql
SELECT 
    game_id,
    COUNT(*) AS total_events,
    COUNT(*) FILTER (WHERE event_type = 'SHOT') AS shot_events,
    COUNT(*) FILTER (WHERE event_type = 'GOAL') AS goal_events,
    COUNT(*) FILTER (WHERE event_type = 'PENALTY') AS penalty_events
FROM game_events
WHERE game_id IN (
    '90000000-0000-0000-0000-000000000001',
    '90000000-0000-0000-0000-000000000002',
    '90000000-0000-0000-0000-000000000003'
)
GROUP BY game_id
ORDER BY game_id;
```

Verify team statistics:

```sql
SELECT 
    game_id,
    team_id,
    shots,
    goals,
    penalties
FROM team_game_stats
WHERE game_id IN (
    '90000000-0000-0000-0000-000000000001',
    '90000000-0000-0000-0000-000000000002',
    '90000000-0000-0000-0000-000000000003'
)
ORDER BY game_id, team_id;
```

Verify expected statistics directly from events:

```sql
SELECT 
    game_id,
    team_id,
    COUNT(*) FILTER (WHERE event_type = 'SHOT')
      + COUNT(*) FILTER (WHERE event_type = 'GOAL') AS expected_shots,
    COUNT(*) FILTER (WHERE event_type = 'GOAL') AS expected_goals,
    COUNT(*) FILTER (WHERE event_type = 'PENALTY') AS expected_penalties
FROM game_events
WHERE game_id IN (
    '90000000-0000-0000-0000-000000000001',
    '90000000-0000-0000-0000-000000000002',
    '90000000-0000-0000-0000-000000000003'
)
GROUP BY game_id, team_id
ORDER BY game_id, team_id;
```

Expected result:

- events from different games are published to the same Kafka topic
- the consumer processes events one by one
- events are separated by `gameId`
- statistics for each game remain correct
- no statistics are mixed between games

## Visualization

The project can be connected to a BI tool such as Metabase or Apache Superset.

Metabase is the preferred option because it can connect directly to PostgreSQL and quickly create dashboards without implementing a custom frontend.

Possible dashboard widgets:

- game result
- shots by team
- goals by team
- penalties by team
- top players by goals
- top players by shots
- player penalties
- game summary table

## Future Improvements

Possible future improvements include:

- extracting the simulator and analytics processor into separate microservices
- adding more event types
- improving simulation realism
- adding authentication
- adding a frontend application
- deploying the system with Docker Compose or Kubernetes
- adding more advanced player and team analytics
