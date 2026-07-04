import { useState } from "react";
import { formatShootingPercentage } from "../utils/format";

// samo G/S/PIM su sortabilne, ostalo je tekst
const COLUMNS = [
  { key: "playerName", label: "Player", sortable: false },
  { key: "teamName", label: "Team", sortable: false },
  { key: "position", label: "Pos", sortable: false },
  { key: "goals", label: "G", sortable: true },
  { key: "shots", label: "S", sortable: true },
  { key: "penalties", label: "PIM", sortable: true },
];

function PlayerStatistics({ playerStats }) {
  const [sortKey, setSortKey] = useState(null);
  const [sortDirection, setSortDirection] = useState("desc");

  // kopija niza pre sort() - ne diraj originalni prop
  const sortedStats = [...playerStats].sort((a, b) => {
    if (!sortKey) {
      return 0;
    }
    const direction = sortDirection === "asc" ? 1 : -1;
    return (a[sortKey] - b[sortKey]) * direction;
  });

  function handleSort(column) {
    if (!column.sortable) {
      return;
    }
    if (sortKey === column.key) {
      // isti klik drugi put - obrni smer
      setSortDirection(sortDirection === "asc" ? "desc" : "asc");
    } else {
      setSortKey(column.key);
      setSortDirection("desc");
    }
  }

  if (playerStats.length === 0) {
    return <p className="state-message">No player statistics available.</p>;
  }

  return (
    <table className="player-statistics-table">
      <thead>
        <tr>
          {COLUMNS.map((column) => (
            <th
              key={column.key}
              onClick={() => handleSort(column)}
              className={column.sortable ? "sortable" : ""}
            >
              {column.label}
              {sortKey === column.key && (sortDirection === "asc" ? " ▲" : " ▼")}
            </th>
          ))}
          <th>Shooting %</th>
        </tr>
      </thead>
      <tbody>
        {sortedStats.map((player) => (
          <tr key={player.playerId}>
            <td>{player.playerName}</td>
            <td>{player.teamName}</td>
            <td>{player.position}</td>
            <td>{player.goals}</td>
            <td>{player.shots}</td>
            <td>{player.penalties}</td>
            <td>{formatShootingPercentage(player.goals, player.shots)}</td>
          </tr>
        ))}
      </tbody>
    </table>
  );
}

export default PlayerStatistics;
