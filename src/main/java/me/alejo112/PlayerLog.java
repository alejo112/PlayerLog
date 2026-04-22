package me.alejo112;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Player;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerLog extends JavaPlugin {

    private final Map<UUID, SessionData> activeSessions = new HashMap<>();
    private CsvStorage csvStorage;
    private ConnectionListener connectionListener;


    @Override
    public void onEnable() {
        saveDefaultConfig();

        this.csvStorage = new CsvStorage(this);
        csvStorage.createLogsFolder();

        this.connectionListener = new ConnectionListener(
                activeSessions,
                csvStorage
        );

        getServer().getPluginManager().registerEvents(
                connectionListener,
                this
        );

        getLogger().info("PlayerLog enabled.");
    }

    @Override
    public void onDisable() {
        for (Player player : getServer().getOnlinePlayers()) {
            connectionListener.saveSession(player);
        }
        getLogger().info("PlayerLog disabled.");
    }

    public Map<UUID, SessionData> getActiveSessions() {
        return activeSessions;
    }

    public CsvStorage getCsvStorage() {
        return csvStorage;
    }
}