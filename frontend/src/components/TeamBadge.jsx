import { getTeamColor, getTeamInitials } from "../utils/teamBadge";

function TeamBadge({ teamName, size = "small" }) {
  return (
    <span
      className={`team-badge team-badge-${size}`}
      style={{ backgroundColor: getTeamColor(teamName) }}
    >
      {getTeamInitials(teamName)}
    </span>
  );
}

export default TeamBadge;
