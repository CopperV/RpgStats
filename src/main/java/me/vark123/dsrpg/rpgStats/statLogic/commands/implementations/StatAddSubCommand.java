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

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StatAddSubCommand implements SubCommand {
    @Override
    public String getName() {
        return "add";
    }

    @Override
    public String getDescription() {
        return "Adding stats to player";
    }

    @Override
    public String getSyntax() {
        return "/rpgstat add <player> <stat> <amount> [key] [duration] [unique]";
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

        var strValue = args[2];
        int value;
        try {
            value = Integer.parseInt(strValue);
        } catch (NumberFormatException e) {
            sender.sendMessage(Component.text(NamedTextColor.RED + "Cannot parse [" + args[2] + "] as number"));
            return false;
        }

        var key = args.length >= 4 ? args[3] : "default";
        var duration = args.length >= 5 ? Utils.parseInputToDuration(args[4]) : -1;
        var unique = args.length >= 6 && Boolean.parseBoolean(args[5]);

        Date now = new Date();
        long expiringAt = -1;
        if(duration >= 0){
            expiringAt = now.getTime() + duration;
        }

        String durationText = "NEVER";
        if(expiringAt >= 0){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
            LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(expiringAt), ZoneId.systemDefault());
            durationText = dateTime.format(formatter);
        }

        stat.addValue(value, key, unique, expiringAt);
        sender.sendMessage(Component.text(NamedTextColor.GREEN + "[" + player.getName() + "] Added " + value + " to stat " + stat.getStat().getId() + " as key [" + key + "] " +
                "for duration [" + durationText + "]. Unique [" + unique + "]"));
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
            case 3:
                var list = new ArrayList<String>();
                for (int i = -100; i <= 100; i += 5)
                    list.add(Integer.toString(i));
                return list;
            case 5:
                return List.of("5s", "10s", "30s", "1m", "2m", "5m", "10m", "15m", "30m", "1h", "2h");
            case 6:
                return List.of("true", "false");
        }
        return List.of();
    }
}
