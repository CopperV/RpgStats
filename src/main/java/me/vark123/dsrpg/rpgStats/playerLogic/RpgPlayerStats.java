package me.vark123.dsrpg.rpgStats.playerLogic;

import me.vark123.dsrpg.rpgStats.statLogic.RpgStatManager;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class RpgPlayerStats {
    private Map<String, PlayerStatData> stats = new HashMap<>();

    public RpgPlayerStats() {
        loadRegisteredStats();
    }

    public RpgPlayerStats(Map<String, PlayerStatData> stats) {
        this();
        this.stats.putAll(stats);
    }

    private void loadRegisteredStats() {
        RpgStatManager.getInstance().getStats().forEach(stat -> {
            var statData = new PlayerStatData(stat);
            stats.put(stat.getId(), statData);
        });
    }

    public Collection<PlayerStatData> getStats() {
        return stats.values();
    }

    public @Nullable PlayerStatData getStat(String key) {
        return stats.get(key.toLowerCase());
    }

    public Optional<PlayerStatData> tryGetStat(String key) {
        return Optional.ofNullable(getStat(key));
    }
}
