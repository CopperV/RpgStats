package me.vark123.dsrpg.rpgStats;

import lombok.Getter;
import me.vark123.dsrpg.rpgStats.placeholders.RpgStatsPlaceholders;
import me.vark123.dsrpg.rpgStats.playerLogic.RpgPlayerStatsManager;
import me.vark123.dsrpg.rpgStats.playerLogic.commands.StatCommand;
import me.vark123.dsrpg.rpgStats.playerLogic.listeners.PlayerLoginStateListener;
import me.vark123.dsrpg.rpgStats.statLogic.RpgStatManager;
import me.vark123.dsrpg.rpgStats.statLogic.commands.RpgStatModifierCommand;
import me.vark123.dsrpg.rpgStats.statLogic.commands.RpgStatModifyCommand;
import me.vark123.dsrpg.rpgStats.statLogic.commands.implementations.RpgCurrentStatModifyCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

public final class RpgStats extends JavaPlugin {

    @Getter
    private static RpgStats instance;

    @Override
    public void onEnable() {
        initialize();
        registerListeners();
        registerCommands();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        RpgPlayerStatsManager.getInstance().shutdown();
    }

    private void initialize() {
        instance = this;

        saveDefaultConfig();
        var config = getConfig();

        RpgStatManager.getInstance().load(config.getConfigurationSection("stats-config"));
        RpgPlayerStatsManager.getInstance();

        new RpgStatsPlaceholders().register();
    }

    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new PlayerLoginStateListener(), this);
    }

    private void registerCommands() {
        registerCommand("staty", new StatCommand());
        registerCommand("rpgstat", new RpgStatModifyCommand());
        registerCommand("rpgmod", new RpgStatModifierCommand());
        registerCommand("rpgcurrentvalue", new RpgCurrentStatModifyCommand());
    }

    private void registerCommand(String cmd, CommandExecutor executor) {
        var pluginCmd = getCommand(cmd);
        if(pluginCmd != null){
            pluginCmd.setExecutor(executor);

            if(executor instanceof TabCompleter tabCompleter)
                pluginCmd.setTabCompleter(tabCompleter);
        }
    }
}
