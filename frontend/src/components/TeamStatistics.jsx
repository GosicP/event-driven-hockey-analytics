import { formatShootingPercentage } from "../utils/format";

function TeamStatistics({ teamStats, homeTeamId, awayTeamId }) {
  // teamStats je lista bez oznake ko je domacin - trazim po id-u
  const home = teamStats.find((t) => t.teamId === homeTeamId);
  const away = teamStats.find((t) => t.teamId === awayTeamId);

  if (!home || !away) {
    return <p className="state-message">No statistics available.</p>;
  }

  const rows = [
    { label: "Shots", home: home.shots, away: away.shots },
    { label: "Goals", home: home.goals, away: away.goals },
    { label: "Penalties", home: home.penalties, away: away.penalties },
    {
      label: "Shooting %",
      home: formatShootingPercentage(home.goals, home.shots),
      away: formatShootingPercentage(away.goals, away.shots),
    },
  ];

  return (
    <div className="team-statistics">
      <div className="team-statistics-header">
        <span>{home.teamName}</span>
        <span>{away.teamName}</span>
      </div>
      {rows.map((row) => (
        <div key={row.label} className="team-statistics-row">
          <span className="team-statistics-value">{row.home}</span>
          <span className="team-statistics-label">{row.label}</span>
          <span className="team-statistics-value">{row.away}</span>
        </div>
      ))}
    </div>
  );
}

export default TeamStatistics;
