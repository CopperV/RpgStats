package me.vark123.dsrpg.rpgStats.statLogic.managers;

import lombok.Getter;
import me.vark123.dsrpg.rpgStats.RpgStats;
import me.vark123.dsrpg.rpgStats.statLogic.RpgStatsHolder;
import me.vark123.dsrpg.rpgStats.statLogic.IEntityStatManager;
import me.vark123.dsrpg.rpgStats.statLogic.RpgStatManager;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.world.EntitiesUnloadEvent;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class MiscEntityStatsManager implements IEntityStatManager, Listener {

    @Getter
    public static final MiscEntityStatsManager instance = new MiscEntityStatsManager();

    private final Map<UUID, RpgStatsHolder> miscStatsContainer = new ConcurrentHashMap<>();

    private MiscEntityStatsManager() {

    }

    @Override
    public Optional<RpgStatsHolder> tryGetStats(UUID uid) {
        if(miscStatsContainer.containsKey(uid)) {
            return Optional.of(miscStatsContainer.get(uid));
        }

        var entity = Bukkit.getEntity(uid);
        if(entity == null || entity.isDead())
            return Optional.empty();

        var stats = loadStats(entity);
        miscStatsContainer.put(uid, stats);
        return Optional.of(stats);
    }

    @Override
    public RpgStatsHolder getStats(UUID uid) {
        return tryGetStats(uid).orElse(null);
    }

    @Override
    public RpgStatsHolder loadStats(UUID uid) {Entity entity = Bukkit.getEntity(uid);
        if (entity == null)
            return null;

        RpgStatsHolder stats = loadStats(entity);
        miscStatsContainer.put(uid, stats);
        return stats;
    }

    @Override
    public RpgStatsHolder removeStats(UUID uid) {
        return miscStatsContainer.remove(uid);
    }

    @Override
    public void shutdown() {
        miscStatsContainer.clear();
    }

    private RpgStatsHolder loadStats(Entity entity) {
        RpgStatsHolder stats = new RpgStatsHolder();

        if(entity == null)
            return stats;

        var pdc = entity.getPersistentDataContainer();
        RpgStatManager.getInstance().getStats().forEach(stat -> {
            var key = new NamespacedKey(RpgStats.getInstance(), "stat_"+stat.getId());

            if(pdc.has(key, PersistentDataType.INTEGER)) {
                var savedValue = pdc.get(key, PersistentDataType.INTEGER);
                if(savedValue != null) {
                    stats.tryGetStat(stat.getId()).ifPresent(statData -> statData.addValue(savedValue));
                }
            }
        });

        return stats;
    }

    @EventHandler
    private void onDeath(EntityDeathEvent event) {
        miscStatsContainer.remove(event.getEntity().getUniqueId());
    }

    @EventHandler
    private void onUnload(EntitiesUnloadEvent event) {
        event.getEntities().stream()
                .map(Entity::getUniqueId)
                .forEach(miscStatsContainer::remove);
    }
}
