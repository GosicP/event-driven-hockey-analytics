import { getStatusLabel, getStatusClassName } from "../utils/format";
import TeamBadge from "./TeamBadge";

function Scoreboard({ details }) {
  const isScheduled = details.status === "SCHEDULED";

  return (
    <div className="scoreboard">
      <div className="scoreboard-team">
        <TeamBadge teamName={details.homeTeam.name} size="large" />
        <span>{details.homeTeam.name}</span>
      </div>
      {/* pre pocetka nema rezultata, samo "vs" */}
      <div className="scoreboard-score">
        {isScheduled ? "vs" : `${details.homeTeam.score} - ${details.awayTeam.score}`}
      </div>
      <div className="scoreboard-team">
        <TeamBadge teamName={details.awayTeam.name} size="large" />
        <span>{details.awayTeam.name}</span>
      </div>
      <div className={`scoreboard-status ${getStatusClassName(details)}`}>
        {getStatusLabel(details)}
      </div>
    </div>
  );
}

export default Scoreboard;
