package me.vark123.dsrpg.rpgStats.statLogic;

import io.lumine.mythic.bukkit.MythicBukkit;
import lombok.Getter;
import me.vark123.dsrpg.rpgStats.statLogic.managers.MiscEntityStatsManager;
import me.vark123.dsrpg.rpgStats.statLogic.managers.MythicEntityStatsManager;
import me.vark123.dsrpg.rpgStats.statLogic.managers.RpgPlayerStatsManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;

public class RpgEntityStatManager implements IEntityStatManager {

    @Getter
    private static final RpgEntityStatManager instance = new RpgEntityStatManager();

    private final IEntityStatManager PLAYER_STATS_MANAGER = RpgPlayerStatsManager.getInstance();
    private final IEntityStatManager ENTITY_STATS_MANAGER = MythicEntityStatsManager.getInstance();
    private final IEntityStatManager MYTHIC_ENTITY_STATS_MANAGER = MiscEntityStatsManager.getInstance();

    private RpgEntityStatManager() {

    }

    @Override
    public Optional<RpgStatsHolder> tryGetStats(UUID uid) {
        return getManager(uid).tryGetStats(uid);
    }

    @Override
    public RpgStatsHolder getStats(UUID uid) {
        return getManager(uid).getStats(uid);
    }

    @Override
    public RpgStatsHolder loadStats(UUID uid) {
        return getManager(uid).loadStats(uid);
    }

    @Override
    public RpgStatsHolder removeStats(UUID uid) {
        return getManager(uid).removeStats(uid);
    }

    @Override
    public void shutdown() {
        PLAYER_STATS_MANAGER.shutdown();
        ENTITY_STATS_MANAGER.shutdown();
        MYTHIC_ENTITY_STATS_MANAGER.shutdown();
    }

    private IEntityStatManager getManager(UUID uid) {
        var entity = Bukkit.getEntity(uid);
        if(entity == null)
            return ENTITY_STATS_MANAGER;

        if(entity instanceof Player)
            return PLAYER_STATS_MANAGER;

        if(MythicBukkit.inst().getMobManager().isActiveMob(uid))
            return MYTHIC_ENTITY_STATS_MANAGER;

        return ENTITY_STATS_MANAGER;
    }
}
