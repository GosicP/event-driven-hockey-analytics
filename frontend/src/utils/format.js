// tekst koji se prikazuje desno u redu/scoreboard-u, zavisi od statusa utakmice
export function getStatusLabel(game) {
  switch (game.status) {
    case "SCHEDULED":
      return formatScheduledTime(game.scheduledAt);
    case "IN_PROGRESS":
      return `${game.currentPeriod}${ordinalSuffix(game.currentPeriod)} PERIOD`;
    case "FINISHED":
      return "FINAL";
    default:
      return "";
  }
}

// css klasa za boju statusa - crveno live, sivo zavrseno, plavo zakazano
export function getStatusClassName(game) {
  switch (game.status) {
    case "IN_PROGRESS":
      return "status-live";
    case "FINISHED":
      return "status-finished";
    case "SCHEDULED":
      return "status-scheduled";
    default:
      return "";
  }
}

// 24h format, npr 19:00 - ne AM/PM
export function formatScheduledTime(isoString) {
  if (!isoString) {
    return "";
  }
  const date = new Date(isoString);
  return date.toLocaleTimeString("en-GB", { hour: "2-digit", minute: "2-digit" });
}

// 1st/2nd/3rd - dovoljno za 3 trecine, ne treba puna ordinal logika
function ordinalSuffix(period) {
  if (period === 1) return "st";
  if (period === 2) return "nd";
  if (period === 3) return "rd";
  return "th";
}

// golovi/sutevi * 100 - cist prikazni racun (gol ne uvecava sutove na backend-u)
export function formatShootingPercentage(goals, shots) {
  if (!shots) {
    return "0.0%";
  }
  return `${((goals / shots) * 100).toFixed(1)}%`;
}
