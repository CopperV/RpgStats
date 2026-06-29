package me.vark123.dsrpg.rpgStats.storage;

import me.vark123.dsrpg.rpgStats.playerLogic.dto.PlayerStatsDTO;

import java.util.Optional;
import java.util.UUID;

public interface IStatStorageService {
    void init();

    Optional<PlayerStatsDTO> loadStats(UUID uuid);

    void saveStats(UUID uuid, PlayerStatsDTO stats);

    void deleteStats(UUID uuid);

    void shutdown();
}
