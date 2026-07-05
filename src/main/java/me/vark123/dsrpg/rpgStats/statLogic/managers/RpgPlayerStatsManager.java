package me.vark123.dsrpg.rpgStats.statLogic.managers;

import lombok.Getter;
import me.vark123.dsrpg.rpgStats.RpgStats;
import me.vark123.dsrpg.rpgStats.playerLogic.RpgPlayerStatsHolder;
import me.vark123.dsrpg.rpgStats.statLogic.RpgStatsHolder;
import me.vark123.dsrpg.rpgStats.playerLogic.dto.ModifierEntryDTO;
import me.vark123.dsrpg.rpgStats.playerLogic.dto.PlayerStatsDTO;
import me.vark123.dsrpg.rpgStats.playerLogic.dto.StatDataDTO;
import me.vark123.dsrpg.rpgStats.playerLogic.dto.StatEntryDTO;
import me.vark123.dsrpg.rpgStats.statLogic.IEntityStatManager;
import me.vark123.dsrpg.rpgStats.statLogic.RpgStatManager;
import me.vark123.dsrpg.rpgStats.storage.IStatStorageService;
import me.vark123.dsrpg.rpgStats.storage.StatStorageFactory;

import javax.annotation.Nullable;
import java.util.*;

public final class RpgPlayerStatsManager implements IEntityStatManager {
    @Getter
    private static final RpgPlayerStatsManager instance = new RpgPlayerStatsManager();

    private final IStatStorageService storageService;
    private final Map<UUID, RpgStatsHolder> playerStatsContainer = new HashMap<>();

    private RpgPlayerStatsManager() {
        storageService = StatStorageFactory.createStorage(RpgStats.getInstance().getConfig());
        storageService.init();
    }

    public Optional<RpgStatsHolder> tryGetStats(UUID uuid) {
        return Optional.ofNullable(playerStatsContainer.get(uuid));
    }

    public @Nullable RpgStatsHolder getStats(UUID uuid) {
        return playerStatsContainer.get(uuid);
    }

    public RpgStatsHolder loadStats(UUID uuid) {
        if (playerStatsContainer.containsKey(uuid)) {
            return playerStatsContainer.get(uuid);
        }

        var oDto = storageService.loadStats(uuid);
        RpgStatsHolder stats = oDto.map(this::fromDto).orElseGet(RpgStatsHolder::new);

        playerStatsContainer.put(uuid, stats);
        return stats;
    }

    public void saveStats(UUID uuid) {
        if (playerStatsContainer.containsKey(uuid))
            saveStats(uuid, playerStatsContainer.get(uuid));
    }

    public void saveStats(UUID uuid, RpgStatsHolder stats) {
        storageService.saveStats(uuid, toDto(stats));
    }

    public RpgStatsHolder removeStats(UUID uuid) {
        return playerStatsContainer.remove(uuid);
    }

    public void deleteStats(UUID uuid) {
        storageService.deleteStats(uuid);
    }

    public void shutdown() {
        playerStatsContainer.forEach(this::saveStats);
        playerStatsContainer.clear();
        storageService.shutdown();
    }

    private PlayerStatsDTO toDto(RpgStatsHolder stats) {
        RpgStatManager statManager = RpgStatManager.getInstance();
        Map<String, StatDataDTO> statDtoMap = new HashMap<>();

        stats.getStats().forEach(stat -> {
            var currentValue = stat.getCurrentValue();
            List<StatEntryDTO> addStats = new ArrayList<>();
            List<ModifierEntryDTO> modifiers = new ArrayList<>();

            stat.getStatEntries().stream()
                    .filter(entry -> statManager.isSaveableKey(entry.getKey()))
                    .map(entry -> new StatEntryDTO(
                            entry.getValue(),
                            entry.getKey(),
                            entry.isUnique(),
                            entry.getExpiringAt() >= 0 ?
                                    entry.getExpiringAt() - new Date().getTime() :
                                    -1
                    ))
                    .forEach(addStats::add);

            stat.getModifiers().stream()
                    .filter(entry -> statManager.isSaveableKey(entry.getKey()))
                    .map(entry -> new ModifierEntryDTO(
                            entry.getValue(),
                            entry.getKey(),
                            entry.isUnique(),
                            entry.getExpiringAt() >= 0 ?
                                    entry.getExpiringAt() - new Date().getTime() :
                                    -1
                    ))
                    .forEach(modifiers::add);

            statDtoMap.put(stat.getStatId(), new StatDataDTO(currentValue, addStats, modifiers));
        });

        return new PlayerStatsDTO(statDtoMap);
    }

    private RpgStatsHolder fromDto(PlayerStatsDTO dto) {
        RpgStatsHolder stats = new RpgPlayerStatsHolder();

        dto.stats().forEach((id, statDto) -> {
            stats.tryGetStat(id).ifPresent(stat -> {
                stat.setCurrentValue(statDto.currentValue());

                statDto.stats().forEach((addStatDto) -> stat.addValue(
                        addStatDto.value(),
                        addStatDto.key(),
                        addStatDto.unique(),
                        addStatDto.expiringDelta() >= 0 ? addStatDto.expiringDelta() + new Date().getTime() : -1)
                );
                statDto.modifiers().forEach((addStatDto) -> stat.addModifier(
                        addStatDto.value(),
                        addStatDto.key(),
                        addStatDto.unique(),
                        addStatDto.expiringDelta() >= 0 ? addStatDto.expiringDelta() + new Date().getTime() : -1)
                );
            });
        });

        return stats;
    }
}
