package me.vark123.dsrpg.rpgStats.playerLogic.dto;

public record ModifierEntryDTO(
    double value,
    String key,
    boolean unique,
    long expiringDelta
) { }
