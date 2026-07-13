function hashString(str) {
  let hash = 0;
  for (let i = 0; i < str.length; i++) {
    hash = (hash << 5) - hash + str.charCodeAt(i);
    hash |= 0;
  }
  return hash;
}

const KNOWN_TEAM_COLORS = {
  "Toronto Maple Leafs": "#00205B",
  "Montreal Canadiens": "#AF1E2D",
  "Boston Bruins": "#FFB81C",
  "Chicago Blackhawks": "#CF0A2C",
  "Colorado Avalanche": "#6F263D",
  "Edmonton Oilers": "#FF4C00",
  "Pittsburgh Penguins": "#000000",
  "Washington Capitals": "#C8102E",
};

const KNOWN_TEAM_ABBREVIATIONS = {
  "Toronto Maple Leafs": "TOR",
  "Montreal Canadiens": "MTL",
  "Boston Bruins": "BOS",
  "Chicago Blackhawks": "CHI",
  "Colorado Avalanche": "COL",
  "Edmonton Oilers": "EDM",
  "Pittsburgh Penguins": "PIT",
  "Washington Capitals": "WSH",
};

export function getTeamColor(teamName) {
  if (KNOWN_TEAM_COLORS[teamName]) {
    return KNOWN_TEAM_COLORS[teamName];
  }
  const hue = Math.abs(hashString(teamName)) % 360;
  return `hsl(${hue}, 45%, 45%)`;
}

export function getTeamInitials(teamName) {
  if (KNOWN_TEAM_ABBREVIATIONS[teamName]) {
    return KNOWN_TEAM_ABBREVIATIONS[teamName];
  }
  const words = teamName.trim().split(/\s+/);
  const initials = words.map((word) => word[0]).join("").toUpperCase();
  return initials.slice(0, 3);
}
