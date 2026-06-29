package me.vark123.dsrpg.rpgStats.statLogic.commands.implementations;

import me.vark123.dsrpg.commands.SubCommand;
import me.vark123.dsrpg.rpgStats.playerLogic.RpgPlayerStatsManager;
import me.vark123.dsrpg.rpgStats.statLogic.RpgStat;
import me.vark123.dsrpg.rpgStats.statLogic.RpgStatManager;
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

        var player = getPlayerByNickOrUUID(args[0]);
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
        var duration = args.length >= 5 ? parseDuration(args[4]) : -1;
        var unique = args.length >= 6 && Boolean.parseBoolean(args[5]);

        stat.addValue(value, key, unique, duration);
        sender.sendMessage(Component.text(NamedTextColor.GREEN + "[" + player.getName() + "] Added " + value + " to stat " + stat.getStat().getId() + " as key [" + key + "] for duration [" + duration + "]. Unique [" + unique + "]"));
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

    private Player getPlayerByNickOrUUID(String input) {
        try {
            UUID uid = UUID.fromString(input);
            return Bukkit.getPlayer(uid);
        } catch (IllegalArgumentException e) {
            return Bukkit.getPlayerExact(input);
        }
    }

    private long parseDuration(String input) {
        if (input == null || input.isEmpty() || input.equalsIgnoreCase("true") || input.equalsIgnoreCase("-1"))
            return -1;

        var cleanInput = input.toLowerCase().trim();
        if (cleanInput.endsWith("t")) {
            try {
                long ticks = Long.parseLong(cleanInput.replace("t", ""));
                return ticks * 50; // 1 tick = 50ms
            } catch (NumberFormatException e) {
                return -1;
            }
        }

        Pattern pattern = Pattern.compile("(\\d+)(mo|y|w|d|h|m|s)");
        Matcher matcher = pattern.matcher(cleanInput);

        long totalMillis = 0;
        boolean found = false;

        while (matcher.find()) {
            found = true;
            long value = Long.parseLong(matcher.group(1));
            String unit = matcher.group(2);

            switch (unit) {
                case "s" -> totalMillis += value * 1000L;
                case "m" -> totalMillis += value * 60L * 1000L;
                case "h" -> totalMillis += value * 60L * 60L * 1000L;
                case "d" -> totalMillis += value * 24L * 60L * 60L * 1000L;
                case "w" -> totalMillis += value * 7L * 24L * 60L * 60L * 1000L;
                case "mo" -> totalMillis += value * 30L * 24L * 60L * 60L * 1000L; // Przybliżenie: 30 dni
                case "y" -> totalMillis += value * 365L * 24L * 60L * 60L * 1000L; // Przybliżenie: 365 dni
            }
        }

        if (!found) {
            try {
                return Long.parseLong(cleanInput) * 1000L;
            } catch (NumberFormatException e) {
                return -1;
            }
        }

        return totalMillis;
    }
}
