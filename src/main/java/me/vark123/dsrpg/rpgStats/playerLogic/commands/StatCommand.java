package me.vark123.dsrpg.rpgStats.playerLogic.commands;

import me.vark123.dsrpg.rpgStats.playerLogic.RpgPlayerStats;
import me.vark123.dsrpg.rpgStats.playerLogic.RpgPlayerStatsManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class StatCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        Player targetPlayer;
        if(args.length > 0){
            if(!sender.hasPermission("dsrpg.stats.other")) {
                sender.sendMessage("§cNie masz uprawnien do sprawdzania statystyk innych graczy");
                return false;
            }

            var player = Bukkit.getPlayerExact(args[0]);
            if(player == null) {
                sender.sendMessage("§cGracz o nicku "+args[0] + " jest offline lub nie istnieje!");
                return false;
            }

            targetPlayer = player;
        } else {
            if(!(sender instanceof Player player)){
                sender.sendMessage("§cUsage: /stat <player>");
                return false;
            }

            targetPlayer = player;
        }

        var stats = RpgPlayerStatsManager.getInstance().getPlayerStats(targetPlayer.getUniqueId());
        displayStats(sender, targetPlayer.getName(), stats);
        return true;
    }

    private void displayStats(@NotNull CommandSender sender, String owner, RpgPlayerStats stats) {
        sender.sendMessage("§8========== §6Statystyki gracza: §e"+owner+" §8==========");

        stats.getStats().forEach(stat -> {
            var statName = stat.getStat().getDisplayName();
            int total = stat.getTotalValue();

            if(stat.getStat().isRenewable()){
                int current = stat.getCurrentValue();
                sender.sendMessage("§7- §6"+statName+"§7: §e"+current+"§7/§e"+total);
            } else {
                sender.sendMessage("§7- §6"+statName+"§7: §e"+total);
            }
        });
        sender.sendMessage("§8=========================================");
    }

}
