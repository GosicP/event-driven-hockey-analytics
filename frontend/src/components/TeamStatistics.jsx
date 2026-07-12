import { formatShootingPercentage } from "../utils/format";

function TeamStatistics({ teamStats, homeTeamId, awayTeamId }) {
  const home = teamStats.find((t) => t.teamId === homeTeamId);
  const away = teamStats.find((t) => t.teamId === awayTeamId);

  if (!home || !away) {
    return <p className="state-message">No statistics available.</p>;
  }

  // golman "brani" sve suteve protivnika koji nisu zavrsili kao gol
  const homeSaves = away.shots - away.goals;
  const awaySaves = home.shots - home.goals;

  const rows = [
    { label: "Shots", homeValue: home.shots, awayValue: away.shots, homeDisplay: home.shots, awayDisplay: away.shots },
    { label: "Goals", homeValue: home.goals, awayValue: away.goals, homeDisplay: home.goals, awayDisplay: away.goals },
    { label: "Penalties", homeValue: home.penalties, awayValue: away.penalties, homeDisplay: home.penalties, awayDisplay: away.penalties },
    {
      label: "Shooting %",
      homeValue: percentValue(home.goals, home.shots),
      awayValue: percentValue(away.goals, away.shots),
      homeDisplay: formatShootingPercentage(home.goals, home.shots),
      awayDisplay: formatShootingPercentage(away.goals, away.shots),
    },
    { label: "Goalie Saves", homeValue: homeSaves, awayValue: awaySaves, homeDisplay: homeSaves, awayDisplay: awaySaves },
    {
      label: "Save %",
      homeValue: percentValue(homeSaves, away.shots),
      awayValue: percentValue(awaySaves, home.shots),
      homeDisplay: formatShootingPercentage(homeSaves, away.shots),
      awayDisplay: formatShootingPercentage(awaySaves, home.shots),
    },
  ];

  return (
    <div className="team-statistics">
      <div className="team-statistics-header">
        <span>{home.teamName}</span>
        <span>{away.teamName}</span>
      </div>
      {rows.map((row) => {
        const total = row.homeValue + row.awayValue;
        const homePercent = total > 0 ? (row.homeValue / total) * 100 : 50;

        return (
          <div key={row.label} className="team-statistics-row">
            <div className="team-statistics-numbers">
              <span className="team-statistics-value">{row.homeDisplay}</span>
              <span className="team-statistics-label">{row.label}</span>
              <span className="team-statistics-value">{row.awayDisplay}</span>
            </div>
            <div className="team-statistics-bar">
              <div className="team-statistics-bar-home" style={{ width: `${homePercent}%` }} />
              <div className="team-statistics-bar-away" style={{ width: `${100 - homePercent}%` }} />
            </div>
          </div>
        );
      })}
    </div>
  );
}

// zajednicka formula: udeo jedne vrednosti u zbiru dve, za sirinu bara
function percentValue(numerator, denominator) {
  if (!denominator) {
    return 0;
  }
  return (numerator / denominator) * 100;
}

export default TeamStatistics;
