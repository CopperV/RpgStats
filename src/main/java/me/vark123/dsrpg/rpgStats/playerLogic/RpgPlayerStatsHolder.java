package me.vark123.dsrpg.rpgStats.playerLogic;

import me.vark123.dsrpg.rpgStats.statLogic.RpgStatManager;
import me.vark123.dsrpg.rpgStats.statLogic.RpgStatsHolder;
import me.vark123.dsrpg.rpgStats.statLogic.StatData;

public class RpgPlayerStatsHolder extends RpgStatsHolder {

    @Override
    protected void loadRegisteredStats() {
        RpgStatManager.getInstance().getStats().forEach(stat -> {
            var statData = new StatData(stat);
            statData.addValue(stat.getDefaultValue(), "start");
            stats.put(stat.getId(), statData);
        });
    }
}
