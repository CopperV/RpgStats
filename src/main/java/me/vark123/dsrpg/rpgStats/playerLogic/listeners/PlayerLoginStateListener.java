package me.vark123.dsrpg.rpgStats.playerLogic.listeners;

import me.vark123.dsrpg.players.events.RpgPlayerJoinEvent;
import me.vark123.dsrpg.players.events.RpgPlayerLeaveEvent;
import me.vark123.dsrpg.rpgStats.playerLogic.RpgPlayerStatsManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlayerLoginStateListener implements Listener {

    @EventHandler
    private void onJoin(RpgPlayerJoinEvent e){
        var uuid = e.getRpgPlayer().getUuid();
        RpgPlayerStatsManager.getInstance().loadStats(uuid);
    }

    @EventHandler
    private void onLeave(RpgPlayerLeaveEvent e){
        var uuid = e.getRpgPlayer().getUuid();
        RpgPlayerStatsManager.getInstance().saveStats(uuid);
        RpgPlayerStatsManager.getInstance().removeStats(uuid);
    }

}
