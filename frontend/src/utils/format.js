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

export function formatScheduledTime(isoString) {
  if (!isoString) {
    return "";
  }
  const date = new Date(isoString);
  return date.toLocaleTimeString("en-GB", { hour: "2-digit", minute: "2-digit" });
}

function ordinalSuffix(period) {
  if (period === 1) return "st";
  if (period === 2) return "nd";
  if (period === 3) return "rd";
  return "th";
}

export function formatShootingPercentage(goals, shots) {
  if (!shots) {
    return "0.0%";
  }
  return `${((goals / shots) * 100).toFixed(1)}%`;
}
