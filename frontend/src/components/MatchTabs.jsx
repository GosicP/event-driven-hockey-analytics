function MatchTabs({ tabs, activeTab, onChange }) {
  return (
    <div className="match-tabs">
      {tabs.map((tab) => (
        // button umesto div - tastatura (Tab + Enter) radi bez dodatnog koda
        <button
          key={tab.key}
          type="button"
          className={`match-tab ${tab.key === activeTab ? "match-tab-active" : ""}`}
          onClick={() => onChange(tab.key)}
        >
          {tab.label}
        </button>
      ))}
    </div>
  );
}

export default MatchTabs;
