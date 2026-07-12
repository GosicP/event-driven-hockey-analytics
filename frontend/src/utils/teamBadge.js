// jednostavan string hash - isti naziv tima uvek daje isti broj
function hashString(str) {
  let hash = 0;
  for (let i = 0; i < str.length; i++) {
    hash = (hash << 5) - hash + str.charCodeAt(i);
    hash |= 0; // ogranici na 32-bitni ceo broj
  }
  return hash;
}

// prave boje poznatih timova - za bilo koji drugi tim pada na hash-boju ispod
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

// zvanicne NHL skracenice za poznate timove - za bilo koji drugi tim pada na generisane inicijale
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
  // rezerva za nepoznat tim - dosledna boja izvedena iz imena
  const hue = Math.abs(hashString(teamName)) % 360;
  return `hsl(${hue}, 45%, 45%)`;
}

export function getTeamInitials(teamName) {
  if (KNOWN_TEAM_ABBREVIATIONS[teamName]) {
    return KNOWN_TEAM_ABBREVIATIONS[teamName];
  }
  // rezerva za nepoznat tim - inicijali od svake reci u imenu, max 3 slova
  const words = teamName.trim().split(/\s+/);
  const initials = words.map((word) => word[0]).join("").toUpperCase();
  return initials.slice(0, 3);
}
