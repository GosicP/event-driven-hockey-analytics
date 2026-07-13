function PeriodScoreTable({ details }) {
  const periods = [1, 2, 3];
  const scoresByPeriod = new Map(details.periodScores.map((p) => [p.period, p]));

  return (
    <table className="period-score-table">
      <thead>
        <tr>
          <th>Team</th>
          {periods.map((period) => (
            <th key={period}>P{period}</th>
          ))}
          <th>Total</th>
        </tr>
      </thead>
      <tbody>
        <tr>
          <td>{details.homeTeam.name}</td>
          {periods.map((period) => {
            const score = scoresByPeriod.get(period);
            return <td key={period}>{score ? score.homeScore : "–"}</td>;
          })}
          <td>{details.homeTeam.score}</td>
        </tr>
        <tr>
          <td>{details.awayTeam.name}</td>
          {periods.map((period) => {
            const score = scoresByPeriod.get(period);
            return <td key={period}>{score ? score.awayScore : "–"}</td>;
          })}
          <td>{details.awayTeam.score}</td>
        </tr>
      </tbody>
    </table>
  );
}

export default PeriodScoreTable;
