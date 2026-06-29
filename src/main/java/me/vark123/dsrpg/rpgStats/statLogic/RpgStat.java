package me.vark123.dsrpg.rpgStats.statLogic;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class RpgStat {
    private final String id;
    private final String displayName;
    private final int defaultValue;
    private final boolean renewable;
}
