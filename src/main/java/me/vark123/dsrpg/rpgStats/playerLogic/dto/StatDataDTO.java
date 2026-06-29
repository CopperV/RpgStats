package me.vark123.dsrpg.rpgStats.playerLogic.dto;

import java.util.List;

public record StatDataDTO(
    int currentValue,
    List<StatEntryDTO> stats,
    List<ModifierEntryDTO> modifiers
) { }
