package me.vark123.dsrpg.rpgStats.playerLogic.dto;

import java.util.Map;

public record PlayerStatsDTO(
        Map<String, StatDataDTO> stats
) { }
