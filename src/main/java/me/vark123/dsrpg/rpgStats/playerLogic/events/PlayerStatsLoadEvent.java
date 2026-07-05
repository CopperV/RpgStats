package me.vark123.dsrpg.rpgStats.playerLogic.events;

import fr.phoenixdevt.profiles.PlayerProfile;
import lombok.Getter;
import me.vark123.dsrpg.players.RpgPlayer;
import me.vark123.dsrpg.players.events.APlayerActionEvent;
import me.vark123.dsrpg.rpgStats.statLogic.RpgStatsHolder;
import org.bukkit.entity.Player;

public class PlayerStatsLoadEvent extends APlayerActionEvent {
    @Getter
    private final RpgStatsHolder stats;

    public PlayerStatsLoadEvent(Player player, PlayerProfile profile, RpgPlayer rpgPlayer, RpgStatsHolder stats) {
        super(player, profile, rpgPlayer);
        this.stats = stats;
    }
}
