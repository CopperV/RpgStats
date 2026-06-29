package me.vark123.dsrpg.rpgStats.storage.implementations;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.vark123.dsrpg.rpgStats.RpgStats;
import me.vark123.dsrpg.rpgStats.playerLogic.RpgPlayerStats;
import me.vark123.dsrpg.rpgStats.playerLogic.dto.PlayerStatsDTO;
import me.vark123.dsrpg.rpgStats.storage.IStatStorageService;

import java.io.File;
import java.sql.*;
import java.util.Optional;
import java.util.UUID;

public class SQLiteStatStorage implements IStatStorageService {
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private Connection connection;

    @Override
    public void init() {
        File dataFolder = new File(RpgStats.getInstance().getDataFolder(), "stats.db");
        try{
            Class.forName("org.sqlite.JDBC");
            this.connection = DriverManager.getConnection("jdbc:sqlite:" + dataFolder.getAbsolutePath());

            try (Statement statement = this.connection.createStatement()) {
                statement.execute("""
                    CREATE TABLE IF NOT EXISTS stats (
                        uuid VARCHAR(36) PRIMARY KEY,
                        data TEXT NOT NULL
                    );
                """);
            }
        }catch (Exception e) {
            RpgStats.getInstance().getLogger().severe("Błąd podczas inicjalizacji bazy SQLite dla RpgStats!");
            e.printStackTrace();
        }
    }

    @Override
    public Optional<PlayerStatsDTO> loadStats(UUID uuid) {
        var query = "SELECT data FROM stats WHERE uuid = ?";
        try(PreparedStatement ps = connection.prepareStatement(query)){
            ps.setString(1, uuid.toString());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String json = rs.getString("data");
                    PlayerStatsDTO stats = gson.fromJson(json, PlayerStatsDTO.class);
                    return Optional.ofNullable(stats);
                }
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public void saveStats(UUID uuid, PlayerStatsDTO stats) {
        var query = "INSERT INTO stats(uuid, data) VALUES(?, ?) ON CONFLICT(uuid) DO UPDATE SET data = ?";

        String json = gson.toJson(stats);
        try(PreparedStatement ps = connection.prepareStatement(query)){
            ps.setString(1, uuid.toString());
            ps.setString(2, json);
            ps.setString(3, json);
            ps.executeUpdate();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    @Override
    public void deleteStats(UUID uuid) {
        var query = "DELETE FROM stats WHERE uuid = ?";

        try(PreparedStatement ps = connection.prepareStatement(query)){
            ps.setString(1, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    @Override
    public void shutdown() {
        try {
            if(connection != null && !connection.isClosed())
                connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
