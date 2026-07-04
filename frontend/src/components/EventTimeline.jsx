import { useState } from "react";

function EventTimeline({ events, periodScores, homeTeamId }) {
  const [showShots, setShowShots] = useState(false); // sutevi sakriveni po difoltu

  const relevantEvents = events.filter((event) => {
    if (event.type === "SHOT") {
      return showShots;
    }
    return event.type === "GOAL" || event.type === "PENALTY";
  });

  const periods = [...new Set(relevantEvents.map((event) => event.period))].sort(
    (a, b) => a - b
  );

  const scoresByPeriod = new Map(periodScores.map((p) => [p.period, p]));

  return (
    <div className="event-timeline">
      {/* checkbox mora uvek biti vidljiv, ne sme da nestane i kad su svi eventi filtrirani */}
      <label className="timeline-toggle">
        <input
          type="checkbox"
          checked={showShots}
          onChange={(event) => setShowShots(event.target.checked)}
        />
        Show shots
      </label>

      {relevantEvents.length === 0 && (
        <p className="state-message">No events yet.</p>
      )}

      {periods.map((period) => {
        const periodEvents = relevantEvents
          .filter((event) => event.period === period)
          .sort((a, b) => a.sequenceNumber - b.sequenceNumber);

        const periodScore = scoresByPeriod.get(period);

        return (
          <div key={period} className="timeline-period">
            <div className="timeline-period-header">
              <span>PERIOD {period}</span>
              {periodScore && (
                <span>{periodScore.homeScore} - {periodScore.awayScore}</span>
              )}
            </div>
            {periodEvents.map((event) => (
              <EventRow key={event.id} event={event} isHome={event.teamId === homeTeamId} />
            ))}
          </div>
        );
      })}
    </div>
  );
}

function EventRow({ event, isHome }) {
  return (
    <div className={`timeline-row ${isHome ? "timeline-row-home" : "timeline-row-away"}`}>
      <div className="timeline-event">
        <span className="timeline-player">{event.playerName}</span>
        <span className={`timeline-type timeline-type-${event.type.toLowerCase()}`}>
          {formatEventLabel(event)}
        </span>
      </div>
    </div>
  );
}

// gol pokazuje rezultat posle sebe, faul/sut samo naziv - nema trajanja/razloga u bazi
function formatEventLabel(event) {
  if (event.type === "GOAL") {
    return `GOAL — ${event.homeScoreAfterEvent}:${event.awayScoreAfterEvent}`;
  }
  if (event.type === "PENALTY") {
    return "PENALTY";
  }
  return "SHOT";
}

export default EventTimeline;
