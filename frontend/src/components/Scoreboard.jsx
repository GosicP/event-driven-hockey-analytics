import { getStatusLabel, getStatusClassName } from "../utils/format";

function Scoreboard({ details }) {
  const isScheduled = details.status === "SCHEDULED";

  return (
    <div className="scoreboard">
      <div className="scoreboard-team">{details.homeTeam.name}</div>
      {/* pre pocetka nema rezultata, samo "vs" */}
      <div className="scoreboard-score">
        {isScheduled ? "vs" : `${details.homeTeam.score} - ${details.awayTeam.score}`}
      </div>
      <div className="scoreboard-team">{details.awayTeam.name}</div>
      <div className={`scoreboard-status ${getStatusClassName(details)}`}>
        {getStatusLabel(details)}
      </div>
    </div>
  );
}

export default Scoreboard;
