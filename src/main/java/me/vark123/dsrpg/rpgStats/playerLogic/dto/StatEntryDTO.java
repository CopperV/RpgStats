package me.vark123.dsrpg.rpgStats.playerLogic.dto;

public record StatEntryDTO(
    int value,
    String key,
    boolean unique,
    long expiringDelta
) { }
