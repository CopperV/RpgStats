package me.vark123.dsrpg.rpgStats.playerLogic.listeners;

import me.vark123.dsrpg.players.events.RpgPlayerJoinEvent;
import me.vark123.dsrpg.players.events.RpgPlayerLeaveEvent;
import me.vark123.dsrpg.rpgStats.playerLogic.RpgPlayerStatsManager;
import me.vark123.dsrpg.rpgStats.playerLogic.events.PlayerStatsLoadEvent;
import me.vark123.dsrpg.rpgStats.playerLogic.events.PlayerStatsUnloadEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlayerLoginStateListener implements Listener {

    @EventHandler
    private void onJoin(RpgPlayerJoinEvent e){
        var uuid = e.getRpgPlayer().getUuid();
        var stats = RpgPlayerStatsManager.getInstance().loadStats(uuid);

        if(stats != null){
            var player = e.getPlayer();
            var profile = e.getProfile();
            var rpgPlayer = e.getRpgPlayer();
            var event = new PlayerStatsLoadEvent(player, profile, rpgPlayer, stats);
            Bukkit.getPluginManager().callEvent(event);
        }
    }

    @EventHandler
    private void onLeave(RpgPlayerLeaveEvent e){
        var uuid = e.getRpgPlayer().getUuid();
        RpgPlayerStatsManager.getInstance().saveStats(uuid);
        var stats = RpgPlayerStatsManager.getInstance().removeStats(uuid);

        if(stats != null){
            var player = e.getPlayer();
            var profile = e.getProfile();
            var rpgPlayer = e.getRpgPlayer();
            var event = new PlayerStatsUnloadEvent(player, profile, rpgPlayer, stats);
            Bukkit.getPluginManager().callEvent(event);
        }
    }

}
