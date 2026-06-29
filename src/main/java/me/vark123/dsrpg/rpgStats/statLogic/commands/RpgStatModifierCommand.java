package me.vark123.dsrpg.rpgStats.statLogic.commands;

import me.vark123.dsrpg.commands.SubCommand;
import me.vark123.dsrpg.rpgStats.statLogic.commands.implementations.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class RpgStatModifierCommand implements CommandExecutor, TabCompleter {

    private final String permission = "rpgstats.admin";
    private final Map<String, SubCommand> subCommands = new HashMap<>();

    public RpgStatModifierCommand() {
        registerSubCommand(new ModifierAddSubCommand());
        registerSubCommand(new ModifierRemoveSubCommand());
        registerSubCommand(new ModifierClearSubCommand());
    }

    private void registerSubCommand(@NotNull SubCommand subCommand) {
        subCommands.put(subCommand.getName().toLowerCase(), subCommand);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!sender.hasPermission(permission)) {
            sender.sendMessage(Component.text(NamedTextColor.RED+"You do not have permission to run this command!"));
            return false;
        }

        if(args.length == 0) {
            sendUsage(sender);
            return true;
        }

        var subcmd = subCommands.get(args[0].toLowerCase());
        if(subcmd == null) {
            sendUsage(sender);
            return true;
        }

        var subArgs = args.length < 2 ? new String[0] : Arrays.copyOfRange(args, 1, args.length);
        return subcmd.execute(sender, subArgs);
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if(!sender.hasPermission("rpgcore.admin")){
            return new ArrayList<>();
        }

        if(args.length < 1) {
            return new ArrayList<>();
        }

        if(args.length == 1) {
            return subCommands.keySet().stream()
                    .filter(name -> name.startsWith(args[0].toLowerCase()))
                    .toList();
        }

        var subcmd = subCommands.get(args[0].toLowerCase());
        if(subcmd != null) {
            var subArgs = args.length < 2 ? new String[0] : Arrays.copyOfRange(args, 1, args.length);
            return subcmd.onTabComplete(sender, subArgs);
        }

        return new ArrayList<>();
    }

    private void sendUsage(CommandSender sender) {
        sender.sendMessage(Component.text(NamedTextColor.RED +"Usage: /rpgcore <command>"));
        for(var subcmd : subCommands.values()) {
            sender.sendMessage(Component.text(NamedTextColor.GRAY + "- " + NamedTextColor.YELLOW + subcmd.getSyntax()));
        }
    }
}
