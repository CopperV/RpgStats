package me.vark123.dsrpg.rpgStats.playerLogic;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.vark123.dsrpg.rpgStats.statLogic.RpgStat;
import me.vark123.dsrpg.rpgStats.statLogic.RpgStatManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Debug;

import java.util.*;

public class PlayerStatData {

    @Getter
    private RpgStat stat;
    @Getter
    @Setter
    private int currentValue;

    private final List<StatEntry> values = new ArrayList<>();
    private final List<ModifierEntry> modifiers = new ArrayList<>();

    public PlayerStatData(RpgStat stat) {
        this.stat = stat;
        addValue(stat.getDefaultValue(), "start");
        this.currentValue = getCurrentValue();
    }

    public String getStatId() {
        return stat.getId();
    }

    public void addCurrentValue(int value) {
        this.currentValue = value;
        this.currentValue = Math.clamp(this.currentValue, 0, getTotalValue());
    }

    public void addValue(int value) {
        addValue(value, "default");
    }

    public void addValue(int value, String key) {
        addValue(value, key, false);
    }

    public void addValue(int value, String key, boolean unique) {
        addValue(value, key, unique, -1);
    }

    public void addValue(int value, String key, boolean unique, long expirinigAt) {
        var cleanKey = key.toLowerCase();
        cleanExpired();

        if (unique) {
            values.removeIf(entry -> entry.getKey().equals(cleanKey));
        } else {
            values.removeIf(entry -> entry.getKey().equals(cleanKey) && entry.isUnique());
        }

        values.add(new StatEntry(cleanKey, value, unique, expirinigAt));
    }

    public void removeValue(String key){
        var cleanKey = key.toLowerCase();
        values.removeIf(entry -> entry.getKey().equals(cleanKey));
    }

    public void addModifier(double value) {
        addModifier(value, "default");
    }

    public void addModifier(double value, String key) {
        addModifier(value, key, false);
    }

    public void addModifier(double value, String key, boolean unique) {
        addModifier(value, key, unique, -1);
    }

    public void addModifier(double value, String key, boolean unique, long expirinigAt) {
        var cleanKey = key.toLowerCase();
        cleanExpired();

        if (unique) {
            modifiers.removeIf(entry -> entry.getKey().equals(cleanKey));
        } else {
            modifiers.removeIf(entry -> entry.getKey().equals(cleanKey) && entry.isUnique());
        }

        modifiers.add(new ModifierEntry(cleanKey, value, unique, expirinigAt));
    }

    public void removeModifier(String key){
        var cleanKey = key.toLowerCase();
        modifiers.removeIf(entry -> entry.getKey().equals(cleanKey));
    }

    public int getValue() {
        return getValue("default");
    }

    public int getValue(String key) {
        cleanExpired();
        String cleanKey = key.toLowerCase();

        var sum = values.stream()
                .filter(entry -> entry.getKey().equals(cleanKey))
                .mapToInt(StatEntry::getValue)
                .sum();
        return sum;
    }

    public int getCurrentValue() {
        if (!stat.isRenewable())
            return getTotalValue();

        return Math.min(currentValue, getTotalValue());
    }

    public int getFinalValue(){
        cleanExpired();

        return values.stream()
                .mapToInt(StatEntry::getValue)
                .sum();
    }

    public int getTotalValue() {
        cleanExpired();

        int totalSum = getFinalValue();
        double totalModifiers = modifiers.stream()
                .mapToDouble(ModifierEntry::getValue)
                .sum() + 1;

        return (int) (totalSum * totalModifiers);
    }

    public Collection<StatEntry> getStatEntries() {
        return Collections.unmodifiableCollection(values);
    }

    public Collection<ModifierEntry> getModifiers() {
        return Collections.unmodifiableCollection(modifiers);
    }

    private void cleanExpired() {
        long now = System.currentTimeMillis();

        values.removeIf(entry -> entry.getExpiringAt() >= 0 && now > entry.getExpiringAt());
        modifiers.removeIf(entry -> entry.getExpiringAt() >= 0 && now > entry.getExpiringAt());
    }

    public void clear(boolean all) {
        if (all) {
            values.clear();
            modifiers.clear();
        } else {
            var statManager = RpgStatManager.getInstance();
            values.removeIf(entry -> !statManager.isSaveableKey(entry.key));
            modifiers.removeIf(entry -> !statManager.isSaveableKey(entry.key));
        }

        addValue(stat.getDefaultValue(), "start");
        this.currentValue = getCurrentValue();
    }

    public void clear(String statKey) {
        var formatedKey = statKey.toLowerCase();
        values.removeIf(entry -> entry.getKey().equals(formatedKey));
        modifiers.removeIf(entry -> entry.getKey().equals(formatedKey));
    }

    @Getter
    @AllArgsConstructor
    public static class StatEntry {
        private final String key;
        private final int value;
        private final boolean unique;
        private final long expiringAt;
    }

    @Getter
    @AllArgsConstructor
    public static class ModifierEntry {
        private final String key;
        private final double value;
        private final boolean unique;
        private final long expiringAt;
    }

}
