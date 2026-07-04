import { useEffect, useState } from "react";
import { listGames, getTopScorers } from "../api/gamesApi";
import GameRow from "../components/GameRow";
import StandingsTable from "../components/StandingsTable";
import PlayerStatistics from "../components/PlayerStatistics";

const POLL_INTERVAL_MS = 3000;

function GamesPage() {
  const [games, setGames] = useState([]);
  const [topScorers, setTopScorers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    let cancelled = false;

    function fetchData() {
      Promise.all([listGames(), getTopScorers()])
        .then(([gamesData, topScorersData]) => {
          if (!cancelled) {
            setGames(gamesData);
            setTopScorers(topScorersData);
            setLoading(false);
            setError(null);
          }
        })
        .catch((err) => {
          if (!cancelled) {
            setError(err.message);
            setLoading(false);
          }
        });
    }

    fetchData(); // prvo ucitavanje - loading spinner
    const intervalId = setInterval(fetchData, POLL_INTERVAL_MS); // tihi refresh

    return () => {
      cancelled = true;
      clearInterval(intervalId); // prekini polling kad se stranica napusti
    };
  }, []);

  return (
    <div className="games-page-transition">
      {loading && <p className="state-message">Loading games...</p>}
      {!loading && error && <p className="state-message state-error">Error: {error}</p>}
      {!loading && !error && games.length === 0 && (
        <p className="state-message">No games found.</p>
      )}
      {!loading && !error && games.length > 0 && (
        <>
          <div className="games-list">
            {games.map((game) => (
              <GameRow key={game.id} game={game} />
            ))}
          </div>

          <h2 className="section-title">Standings</h2>
          <StandingsTable games={games} />

          <h2 className="section-title">Top Scorers</h2>
          <PlayerStatistics playerStats={topScorers} />
        </>
      )}
    </div>
  );
}

export default GamesPage;
