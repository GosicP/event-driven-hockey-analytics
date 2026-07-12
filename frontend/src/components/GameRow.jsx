import { Link } from "react-router-dom";
import { getStatusLabel, getStatusClassName } from "../utils/format";
import TeamBadge from "./TeamBadge";

function GameRow({ game }) {
  const isScheduled = game.status === "SCHEDULED";
  const isLive = game.status === "IN_PROGRESS";

  return (
    // viewTransition - klik okine slide animaciju ka detaljima
    <Link to={`/games/${game.id}`} viewTransition className="game-row">
      <div className="game-row-teams">
        <div className="game-row-team">
          <span className="game-row-team-info">
            <TeamBadge teamName={game.homeTeam.name} />
            <span className="team-name">{game.homeTeam.name}</span>
          </span>
          {/* zakazana utakmica - nema smisla prikazivati 0-0 */}
          {!isScheduled && (
            <span className={`team-score ${isLive ? "score-live" : ""}`}>
              {game.homeScore}
            </span>
          )}
        </div>
        <div className="game-row-team">
          <span className="game-row-team-info">
            <TeamBadge teamName={game.awayTeam.name} />
            <span className="team-name">{game.awayTeam.name}</span>
          </span>
          {!isScheduled && (
            <span className={`team-score ${isLive ? "score-live" : ""}`}>
              {game.awayScore}
            </span>
          )}
        </div>
      </div>
      <div className={`game-row-status ${getStatusClassName(game)}`}>
        {getStatusLabel(game)}
      </div>
    </Link>
  );
}

export default GameRow;
