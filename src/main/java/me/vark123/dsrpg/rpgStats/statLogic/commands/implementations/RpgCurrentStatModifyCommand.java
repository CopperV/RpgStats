package me.vark123.dsrpg.rpgStats.statLogic.commands.implementations;

import me.vark123.dsrpg.rpgStats.playerLogic.RpgPlayerStatsManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class RpgCurrentStatModifyCommand implements CommandExecutor, TabCompleter {
    private final String permission = "rpgstats.admin";

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!sender.hasPermission(permission)) {
            sender.sendMessage(Component.text(NamedTextColor.RED+"You do not have permission to run this command!"));
            return false;
        }

        if(args.length < 4) {
            sendCorrectUsage(sender);
            return false;
        }

        var action = args[0].toLowerCase();
        var targetName = args[1];
        var statId = args[2].toLowerCase();
        var strValue = args[3];

        var target = getPlayerByNickOrUUID(targetName);
        if(target == null) {
            sender.sendMessage(Component.text(NamedTextColor.RED + "Cannot find provided player as argument [" + args[0] + "]"));
            return false;
        }

        var stat = RpgPlayerStatsManager.getInstance().getPlayerStats(target.getUniqueId()).getStat(statId);
        if(stat == null) {
            sender.sendMessage(Component.text(NamedTextColor.RED + "Cannot find provided stat as argument [" + statId + "]"));
            return false;
        }
        if(!stat.getStat().isRenewable()){
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


        switch(action) {
            case "add":
                stat.addCurrentValue(value);
                sender.sendMessage(Component.text(NamedTextColor.GREEN + "["+target.getName()+"] "+stat.getStat().getDisplayName()+" "+(value >= 0 ? "+"+value : value)));
                return true;
        }

        return false;
    }
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        return List.of();
    }

    private void sendCorrectUsage(CommandSender sender) {
        sender.sendMessage("§cCorrect usage: /rpgcurrentvalue <add> <player> <stat> <amount>");
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
