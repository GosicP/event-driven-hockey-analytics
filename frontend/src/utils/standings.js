export function computeStandings(games) {
  const teams = new Map();

  function getOrCreate(teamId, teamName) {
    if (!teams.has(teamId)) {
      teams.set(teamId, { teamId, teamName, gp: 0, w: 0, l: 0, t: 0, gf: 0, ga: 0, pts: 0 });
    }
    return teams.get(teamId);
  }

  for (const game of games) {
    if (game.status !== "FINISHED") {
      continue;
    }

    const home = getOrCreate(game.homeTeam.id, game.homeTeam.name);
    const away = getOrCreate(game.awayTeam.id, game.awayTeam.name);

    home.gp += 1;
    away.gp += 1;
    home.gf += game.homeScore;
    home.ga += game.awayScore;
    away.gf += game.awayScore;
    away.ga += game.homeScore;

    if (game.homeScore > game.awayScore) {
      home.w += 1;
      home.pts += 2;
      away.l += 1;
    } else if (game.homeScore < game.awayScore) {
      away.w += 1;
      away.pts += 2;
      home.l += 1;
    } else {
      home.t += 1;
      away.t += 1;
      home.pts += 1;
      away.pts += 1;
    }
  }

  return [...teams.values()].sort((a, b) => {
    if (b.pts !== a.pts) return b.pts - a.pts;
    return (b.gf - b.ga) - (a.gf - a.ga);
  });
}
