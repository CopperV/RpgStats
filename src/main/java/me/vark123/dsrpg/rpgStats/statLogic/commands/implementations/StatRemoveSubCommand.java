package me.vark123.dsrpg.rpgStats.statLogic.commands.implementations;

import me.vark123.dsrpg.commands.SubCommand;
import me.vark123.dsrpg.rpgStats.playerLogic.RpgPlayerStatsManager;
import me.vark123.dsrpg.rpgStats.statLogic.RpgStat;
import me.vark123.dsrpg.rpgStats.statLogic.RpgStatManager;
import me.vark123.dsrpg.utility.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StatRemoveSubCommand implements SubCommand {
    @Override
    public String getName() {
        return "remove";
    }

    @Override
    public String getDescription() {
        return "Removing stats from player with given key";
    }

    @Override
    public String getSyntax() {
        return "/rpgstat remove <player> <stat> <key>";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(Component.text(NamedTextColor.RED + "Correct usage:" + NamedTextColor.YELLOW + getSyntax()));
            return true;
        }

        var player = Utils.getPlayerByNickOrUUID(args[0]);
        if (player == null) {
            sender.sendMessage(Component.text(NamedTextColor.RED + "Cannot find provided player as argument [" + args[0] + "]"));
            return true;
        }

        var stats = RpgPlayerStatsManager.getInstance().getPlayerStats(player.getUniqueId());
        var stat = stats.getStat(args[1]);
        if (stat == null) {
            sender.sendMessage(Component.text(NamedTextColor.RED + "Cannot find provided stat as argument [" + args[1] + "]"));
            return false;
        }

        var key = args[2].toLowerCase();
        stat.removeValue(key);
        sender.sendMessage(Component.text(NamedTextColor.GREEN + "[" + player.getName() + "] Removed stat " + stat.getStat().getId() + " with given key [" + key + "]"));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        switch (args.length) {
            case 1:
                return null;
            case 2:
                return RpgStatManager.getInstance().getStats().stream()
                        .map(RpgStat::getId)
                        .map(String::toLowerCase)
                        .toList();
        }
        return List.of();
    }
}
