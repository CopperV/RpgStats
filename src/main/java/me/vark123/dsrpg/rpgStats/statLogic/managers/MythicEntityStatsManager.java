package me.vark123.dsrpg.rpgStats.statLogic.managers;

import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.events.MythicMobDeathEvent;
import io.lumine.mythic.bukkit.events.MythicMobDespawnEvent;
import io.lumine.mythic.bukkit.events.MythicMobSpawnEvent;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.mobs.MobExecutor;
import io.lumine.mythic.core.skills.variables.VariableType;
import lombok.Getter;
import me.vark123.dsrpg.rpgStats.statLogic.RpgStatsHolder;
import me.vark123.dsrpg.rpgStats.statLogic.IEntityStatManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class MythicEntityStatsManager implements IEntityStatManager, Listener {

    @Getter
    public static final MythicEntityStatsManager instance = new MythicEntityStatsManager();

    private final Map<UUID, RpgStatsHolder> mobStats = new ConcurrentHashMap<>();

    private MobExecutor mobExecutor;

    private MythicEntityStatsManager() {
        mobExecutor = MythicBukkit.inst().getMobManager();
    }

    @Override
    public Optional<RpgStatsHolder> tryGetStats(UUID uid) {
        if(mobStats.containsKey(uid))
            return Optional.of(mobStats.get(uid));

        if (!mobExecutor.isActiveMob(uid))
            return Optional.empty();

        var entity = Bukkit.getEntity(uid);
        if(entity == null)
            return Optional.empty();

        var activeMob = mobExecutor.getMythicMobInstance(entity);
        var stats = parseMythicVariables(activeMob);
        return Optional.ofNullable(stats);
    }

    @Override
    public RpgStatsHolder getStats(UUID uid) {
        return tryGetStats(uid).orElse(null);
    }

    @Override
    public RpgStatsHolder loadStats(UUID uid) {
        if (!mobExecutor.isActiveMob(uid)) {
            return null;
        }

        Entity entity = Bukkit.getEntity(uid);
        if (entity == null) {
            return null;
        }

        ActiveMob activeMob = mobExecutor.getMythicMobInstance(entity);
        RpgStatsHolder stats = parseMythicVariables(activeMob);

        mobStats.put(uid, stats);
        return stats;
    }

    @Override
    public RpgStatsHolder removeStats(UUID uid) {
        return mobStats.remove(uid);
    }

    @Override
    public void shutdown() {
        mobStats.clear();
    }

    private RpgStatsHolder parseMythicVariables(ActiveMob mob) {
        RpgStatsHolder stats = new RpgStatsHolder();

        if(mob == null)
            return stats;

        mob.getVariables().asMap().entrySet().stream()
                .filter(entry -> entry.getKey().startsWith("stat_"))
                .filter(entry -> entry.getValue().getType().equals(VariableType.INTEGER))
                .forEach(entry -> {
                    var statId = entry.getKey().replace("stat_", "");
                    int value = (int) entry.getValue().get();

                    stats.tryGetStat(statId).ifPresent(stat -> {
                        stat.addValue(value);
                    });
                });

        return stats;
    }

    @EventHandler
    private void onSpawn(MythicMobSpawnEvent event) {
        loadStats(event.getEntity().getUniqueId());
    }

    @EventHandler
    private void onDeath(MythicMobDeathEvent event) {
        removeStats(event.getEntity().getUniqueId());
    }

    @EventHandler
    private void onDespawn(MythicMobDespawnEvent event) {
        removeStats(event.getEntity().getUniqueId());
    }
}
