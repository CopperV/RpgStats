package me.vark123.dsrpg.rpgStats.statLogic;

import java.util.Optional;
import java.util.UUID;

public interface IEntityStatManager {

    Optional<RpgStatsHolder> tryGetStats(UUID uid);
    RpgStatsHolder getStats(UUID uid);
    RpgStatsHolder loadStats(UUID uid);
    RpgStatsHolder removeStats(UUID uid);
    void shutdown();

}
