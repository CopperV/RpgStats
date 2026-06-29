package me.vark123.dsrpg.rpgStats.statLogic;

import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;

public final class RpgStatManager {
    @Getter
    private static final RpgStatManager instance = new RpgStatManager();

    private final Map<String, RpgStat> registeredStats = new HashMap<>();
    private final Set<String> saveableKeys = new HashSet<>();

    private RpgStatManager() {

    }

    public void load(ConfigurationSection config){
        registeredStats.clear();
        saveableKeys.clear();

        if(config == null)
            return;

        var keys = config.getStringList("saveable-keys");
        keys.stream()
                .map(String::toLowerCase)
                .forEach(saveableKeys::add);

        if(config.isConfigurationSection("stat-list")){
            var section = config.getConfigurationSection("stat-list");
            section.getKeys(false).stream()
                    .filter(section::isConfigurationSection)
                    .map(section::getConfigurationSection)
                    .forEach(statSection -> {
                        var id = statSection.getString("id", statSection.getName()).toLowerCase();
                        var display = statSection.getString("display", id);
                        var defaultValue = statSection.getInt("default-value", 0);
                        var renewable = statSection.getBoolean("renewable", false);

                        var stat = new RpgStat(id, display, defaultValue, renewable);
                        registeredStats.put(id, stat);
                    });
        }
    }

    public Optional<RpgStat> getStat(String id){
        return Optional.ofNullable(registeredStats.get(id.toLowerCase()));
    }

    public Collection<RpgStat> getStats(){
        return registeredStats.values();
    }

    public boolean isSaveableKey(String id){
        return saveableKeys.contains(id.toLowerCase());
    }

    public Collection<String> getSaveableKeys(){
        return Collections.unmodifiableCollection(saveableKeys);
    }
}
