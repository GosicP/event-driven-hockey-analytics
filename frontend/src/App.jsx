import { createBrowserRouter } from "react-router-dom";
import GamesPage from "./pages/GamesPage";
import GameDetailsPage from "./pages/GameDetailsPage";

// data router (ne <BrowserRouter>) - potrebno da viewTransition stvarno radi
const router = createBrowserRouter([
  { path: "/", element: <GamesPage /> },
  { path: "/games/:gameId", element: <GameDetailsPage /> },
]);

export default router;
