package me.vark123.dsrpg.rpgStats.placeholders;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.vark123.dsrpg.rpgStats.RpgStats;
import me.vark123.dsrpg.rpgStats.playerLogic.PlayerStatData;
import me.vark123.dsrpg.rpgStats.playerLogic.RpgPlayerStats;
import me.vark123.dsrpg.rpgStats.playerLogic.RpgPlayerStatsManager;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RpgStatsPlaceholders extends PlaceholderExpansion {
    @Override
    public @NotNull String getIdentifier() {
        return "rpgstats";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Vark123";
    }

    @Override
    public @NotNull String getVersion() {
        return RpgStats.getInstance().getPluginMeta().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
        if (player == null) {
            return "";
        }

        // Pobieramy dane statystyk (loadStats obsługuje UUID, więc zadziała też dla OfflinePlayer)
        RpgPlayerStats playerStats = RpgPlayerStatsManager.getInstance().loadStats(player.getUniqueId());
        if (playerStats == null) {
            return "0";
        }

        String lowerParams = params.toLowerCase();
        String[] args = lowerParams.split("_");

        if (args.length < 2) {
            return null;
        }

        String type = args[0]; // stat, total, current, formattedcurrent

        // Obsługa %rpgstats_stat_statid% oraz %rpgstats_stat_statid_key%
        if (type.equals("stat")) {
            String statId = args[1];
            PlayerStatData statData = playerStats.getStat(statId);
            if (statData == null)
                return "0";

            if (args.length >= 3) {
                String key = params.substring(params.indexOf(args[2]));
                return String.valueOf(statData.getValue(key));
            }

            return String.valueOf(statData.getFinalValue());
        }

        // Obsługa %rpgstats_total_statid%
        if (type.equals("total")) {
            String statId = args[1];
            PlayerStatData statData = playerStats.getStat(statId);
            return statData != null ? String.valueOf(statData.getTotalValue()) : "0";
        }

        // Obsługa %rpgstats_current_statid%
        if (type.equals("current")) {
            String statId = args[1];
            PlayerStatData statData = playerStats.getStat(statId);
            return statData != null ? String.valueOf(statData.getCurrentValue()) : "0";
        }

        // Obsługa %rpgstats_formattedcurrent_statid%
        if (type.equals("formattedcurrent")) {
            String statId = args[1];
            PlayerStatData statData = playerStats.getStat(statId);
            if (statData == null) return "0";

            if (statData.getStat().isRenewable()) {
                return statData.getCurrentValue() + "/" + statData.getTotalValue();
            } else {
                return String.valueOf(statData.getTotalValue());
            }
        }

        return null;
    }
}
