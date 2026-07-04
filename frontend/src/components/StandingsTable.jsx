import { computeStandings } from "../utils/standings";

function StandingsTable({ games }) {
  const standings = computeStandings(games);

  if (standings.length === 0) {
    return null; // nema zavrsenih utakmica jos
  }

  return (
    <table className="standings-table">
      <thead>
        <tr>
          <th>Team</th>
          <th>GP</th>
          <th>W</th>
          <th>L</th>
          <th>T</th>
          <th>GF</th>
          <th>GA</th>
          <th>PTS</th>
        </tr>
      </thead>
      <tbody>
        {standings.map((team) => (
          <tr key={team.teamId}>
            <td>{team.teamName}</td>
            <td>{team.gp}</td>
            <td>{team.w}</td>
            <td>{team.l}</td>
            <td>{team.t}</td>
            <td>{team.gf}</td>
            <td>{team.ga}</td>
            <td>{team.pts}</td>
          </tr>
        ))}
      </tbody>
    </table>
  );
}

export default StandingsTable;
