import { Routes, Route } from "react-router-dom";
import GamesPage from "./pages/GamesPage";
import GameDetailsPage from "./pages/GameDetailsPage";

function App() {
  return (
    // / -> lista utakmica, /games/:id -> detalji jedne
    <Routes>
      <Route path="/" element={<GamesPage />} />
      <Route path="/games/:gameId" element={<GameDetailsPage />} />
    </Routes>
  );
}

export default App;
