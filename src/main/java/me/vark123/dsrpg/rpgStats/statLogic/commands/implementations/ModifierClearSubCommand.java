package me.vark123.dsrpg.rpgStats.statLogic.commands.implementations;

import me.vark123.dsrpg.commands.SubCommand;
import me.vark123.dsrpg.rpgStats.playerLogic.RpgPlayerStatsManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class ModifierClearSubCommand implements SubCommand {
    @Override
    public String getName() {
        return "clear";
    }

    @Override
    public String getDescription() {
        return "Removing all modifiers from player with given key";
    }

    @Override
    public String getSyntax() {
        return "/rpgmod clear <player> <key>";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(Component.text(NamedTextColor.RED + "Correct usage:" + NamedTextColor.YELLOW + getSyntax()));
            return true;
        }

        var player = getPlayerByNickOrUUID(args[0]);
        if (player == null) {
            sender.sendMessage(Component.text(NamedTextColor.RED + "Cannot find provided player as argument [" + args[0] + "]"));
            return true;
        }

        var stats = RpgPlayerStatsManager.getInstance().getPlayerStats(player.getUniqueId());
        var key = args[1].toLowerCase();

        stats.getStats().forEach(stat -> stat.removeModifier(key));
        sender.sendMessage(Component.text(NamedTextColor.GREEN + "[" + player.getName() + "] Cleared all modifiers with given key " + key));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        switch (args.length) {
            case 1:
                return null;
        }
        return List.of();
    }

    private Player getPlayerByNickOrUUID(String input) {
        try {
            UUID uid = UUID.fromString(input);
            return Bukkit.getPlayer(uid);
        } catch (IllegalArgumentException e) {
            return Bukkit.getPlayerExact(input);
        }
    }
}
