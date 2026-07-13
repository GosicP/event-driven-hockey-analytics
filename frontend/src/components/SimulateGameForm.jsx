import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { simulateGame } from "../api/gamesApi";

function SimulateGameForm({ games }) {
  const navigate = useNavigate();
  const [gameId, setGameId] = useState("");

  function handleSubmit(event) {
    event.preventDefault();
    if (!gameId) {
      return;
    }

    simulateGame(gameId).catch((err) => console.error("Simulacija nije uspela:", err));
    navigate(`/games/${gameId}`, { viewTransition: true });
  }

  return (
    <form className="simulate-form" onSubmit={handleSubmit}>
      <label className="simulate-form-field">
        Game
        <select value={gameId} onChange={(e) => setGameId(e.target.value)}>
          <option value="">Select game</option>
          {games.map((game) => (
            <option key={game.id} value={game.id}>
              {game.homeTeam.name} vs {game.awayTeam.name}
            </option>
          ))}
        </select>
      </label>

      <button type="submit" disabled={!gameId}>
        Simulate game
      </button>
    </form>
  );
}

export default SimulateGameForm;
