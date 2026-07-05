package me.vark123.dsrpg.rpgStats.statLogic;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class RpgStatsHolder {
    protected Map<String, StatData> stats = new HashMap<>();

    public RpgStatsHolder() {
        loadRegisteredStats();
    }

    public RpgStatsHolder(Map<String, StatData> stats) {
        this();
        this.stats.putAll(stats);
    }

    protected void loadRegisteredStats() {
        RpgStatManager.getInstance().getStats().forEach(stat -> {
            var statData = new StatData(stat);
            stats.put(stat.getId(), statData);
        });
    }

    public Collection<StatData> getStats() {
        return stats.values();
    }

    public @Nullable StatData getStat(String key) {
        return stats.get(key.toLowerCase());
    }

    public Optional<StatData> tryGetStat(String key) {
        return Optional.ofNullable(getStat(key));
    }
}
