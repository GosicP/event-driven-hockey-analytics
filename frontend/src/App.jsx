import { createBrowserRouter } from "react-router-dom";
import GamesPage from "./pages/GamesPage";
import GameDetailsPage from "./pages/GameDetailsPage";

const router = createBrowserRouter([
  { path: "/", element: <GamesPage /> },
  { path: "/games/:gameId", element: <GameDetailsPage /> },
]);

export default router;
