import { useEffect, useRef, useState } from "react";
import { Link, useParams } from "react-router-dom";
import { getGameDetails, getGameEvents, getGameStats } from "../api/gamesApi";
import Scoreboard from "../components/Scoreboard";
import PeriodScoreTable from "../components/PeriodScoreTable";
import MatchTabs from "../components/MatchTabs";
import EventTimeline from "../components/EventTimeline";
import TeamStatistics from "../components/TeamStatistics";
import PlayerStatistics from "../components/PlayerStatistics";

const POLL_INTERVAL_MS = 1000;

// tabovi ispod scoreboard-a
const TABS = [
  { key: "details", label: "DETAILS" },
  { key: "statistics", label: "STATISTICS" },
  { key: "players", label: "PLAYERS" },
];

function GameDetailsPage() {
  const { gameId } = useParams(); // iz URL-a /games/:gameId

  const [details, setDetails] = useState(null);
  const [events, setEvents] = useState([]);
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [activeTab, setActiveTab] = useState("details");

  // pamti poslednji vidjeni sequenceNumber - da trazimo samo NOVE evente, ne sve iznova
  const lastSequenceRef = useRef(null);

  useEffect(() => {
    let cancelled = false;
    let intervalId = null;

    setLoading(true);
    setError(null);
    setDetails(null);
    setEvents([]);
    setStats(null);
    lastSequenceRef.current = null;

    function poll() {
      const afterSequenceNumber = lastSequenceRef.current;

      // sva tri poziva paralelno, ne jedan pa drugi
      Promise.all([
        getGameDetails(gameId),
        getGameEvents(gameId, afterSequenceNumber),
        getGameStats(gameId),
      ])
        .then(([detailsData, newEvents, statsData]) => {
          if (cancelled) {
            return;
          }

          setDetails(detailsData);
          setStats(statsData);

          if (newEvents.length > 0) {
            // dodaj samo nove evente na kraj, ne zameni celu listu
            setEvents((prev) => [...prev, ...newEvents]);
            lastSequenceRef.current = newEvents[newEvents.length - 1].sequenceNumber;
          }

          setLoading(false);

          // utakmica gotova - vec smo dobili finalno stanje u ovom pollu, dalje ne treba
          if (detailsData.status === "FINISHED" && intervalId) {
            clearInterval(intervalId);
          }
        })
        .catch((err) => {
          // bilo koji od tri poziva puca ovde - npr 404 na nepostojecu utakmicu
          if (!cancelled) {
            setError(err.message);
            setLoading(false);
          }
        });
    }

    poll(); // prvo ucitavanje
    intervalId = setInterval(poll, POLL_INTERVAL_MS);

    return () => {
      cancelled = true;
      clearInterval(intervalId); // prekini polling kad se stranica napusti ili se promeni gameId
    };
  }, [gameId]); // reset i restart ako se predje na drugu utakmicu

  return (
    <div className="game-details-transition">
      <Link to="/" viewTransition className="back-link">
        ← All games
      </Link>

      {loading && <p className="state-message">Loading game...</p>}
      {!loading && error && <p className="state-message state-error">Error: {error}</p>}

      {!loading && !error && details && (
        <>
          <Scoreboard details={details} />
          <PeriodScoreTable details={details} />
          <MatchTabs tabs={TABS} activeTab={activeTab} onChange={setActiveTab} />

          {/* prikazi samo aktivni tab */}
          {activeTab === "details" && (
            <EventTimeline
              events={events}
              periodScores={details.periodScores}
              homeTeamId={details.homeTeam.id}
            />
          )}
          {activeTab === "statistics" && stats && (
            <TeamStatistics
              teamStats={stats.teamStats}
              homeTeamId={details.homeTeam.id}
              awayTeamId={details.awayTeam.id}
            />
          )}
          {activeTab === "players" && stats && (
            <PlayerStatistics playerStats={stats.playerStats} />
          )}
        </>
      )}
    </div>
  );
}

export default GameDetailsPage;
