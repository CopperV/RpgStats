package me.vark123.dsrpg.rpgStats.statLogic.commands.implementations;

import me.vark123.dsrpg.rpgStats.statLogic.RpgStat;
import me.vark123.dsrpg.rpgStats.statLogic.RpgStatManager;
import me.vark123.dsrpg.rpgStats.statLogic.RpgStatsHolder;
import me.vark123.dsrpg.rpgStats.statLogic.managers.RpgPlayerStatsManager;
import me.vark123.dsrpg.utility.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RpgCurrentStatModifyCommand implements CommandExecutor, TabCompleter {
    private final String permission = "rpgstats.admin";

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!sender.hasPermission(permission)) {
            sender.sendMessage(Component.text(NamedTextColor.RED + "You do not have permission to run this command!"));
            return false;
        }

        if (args.length < 4) {
            sendCorrectUsage(sender);
            return false;
        }

        var action = args[0].toLowerCase();
        var targetName = args[1];
        var statId = args[2].toLowerCase();
        var strValue = args[3];

        var target = Utils.getPlayerByNickOrUUID(targetName);
        if (target == null) {
            sender.sendMessage(Component.text(NamedTextColor.RED + "Cannot find provided player as argument [" + targetName + "]"));
            return false;
        }

        var stat = RpgPlayerStatsManager.getInstance().getStats(target.getUniqueId()).getStat(statId);
        if (stat == null) {
            sender.sendMessage(Component.text(NamedTextColor.RED + "Cannot find provided stat as argument [" + statId + "]"));
            return false;
        }
        if (!stat.getStat().isRenewable()) {
            sender.sendMessage(Component.text(NamedTextColor.RED + "Provided stat is not renewable [" + statId + "]"));
            return false;
        }

        int value;
        try {
            value = Integer.parseInt(strValue);
        } catch (NumberFormatException e) {
            sender.sendMessage(Component.text(NamedTextColor.RED + "Cannot parse [" + strValue + "] as number"));
            return false;
        }


        switch (action) {
            case "add":
                stat.addCurrentValue(value);
                sender.sendMessage(Component.text(NamedTextColor.GREEN + "[" + target.getName() + "] " + stat.getStat().getDisplayName() + " " + (value >= 0 ? "+" + value : value)));
                return true;
        }

        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!sender.hasPermission(permission)) {
            return Collections.emptyList();
        }

        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            List<String> actions = List.of("add");
            return StringUtil.copyPartialMatches(args[0], actions, completions);
        }

        if (args.length == 2) {
            return null;
        }

        if (args.length == 3) {
            var stats = RpgStatManager.getInstance().getStats()
                    .stream()
                    .filter(RpgStat::isRenewable)
                    .map(RpgStat::getId)
                    .toList();
            return StringUtil.copyPartialMatches(args[2], stats, completions);
        }

        if (args.length == 4) {
            List<String> suggestions = new ArrayList<>();
            for (int i = -50; i <= 50; i += 5) {
                suggestions.add(String.valueOf(i));
            }
            return StringUtil.copyPartialMatches(args[3], suggestions, completions);
        }

        return Collections.emptyList();
    }

    private void sendCorrectUsage(CommandSender sender) {
        sender.sendMessage("§cCorrect usage: /rpgcurrentvalue <add> <player> <stat> <amount>");
    }
}
