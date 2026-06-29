package me.vark123.dsrpg.rpgStats.storage;

import me.vark123.dsrpg.rpgStats.storage.implementations.SQLiteStatStorage;
import org.bukkit.configuration.ConfigurationSection;

public final class StatStorageFactory {
    public static IStatStorageService createStorage(ConfigurationSection config) {
        var type = config.getString("storage.type").toUpperCase();
        switch(type){
            case "MYSQL":
                throw new UnsupportedOperationException("MYSQL storage not yet supported");
            case "SQLITE":
                return new SQLiteStatStorage();
            case "MONGO":
                throw new UnsupportedOperationException("MONGO storage not yet supported");
            case "JSON":
                throw new UnsupportedOperationException("JSON storage not yet supported");
            case "YAML":
                throw new UnsupportedOperationException("YAML storage not yet supported");
            default:
                throw new UnsupportedOperationException("Unknown storage type: " + type);
        }
    }
}
