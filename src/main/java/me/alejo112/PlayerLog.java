package me.alejo112;

import org.bukkit.plugin.java.JavaPlugin;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerLog extends JavaPlugin {

    private final Map<UUID, SessionData> activeSessions = new HashMap<>();
    private CsvStorage csvStorage;


    @Override
    public void onEnable() {
        saveDefaultConfig();

        this.csvStorage = new CsvStorage(this);
        csvStorage.createLogsFolder();

        getServer().getPluginManager().registerEvents(
                new ConnectionListener(activeSessions, csvStorage),
                this
        );

        getLogger().info("PlayerLog enabled.");
    }

    @Override
    public void onDisable() {
        getLogger().info("PlayerLog disabled.");
    }

    public Map<UUID, SessionData> getActiveSessions() {
        return activeSessions;
    }

    public CsvStorage getCsvStorage() {
        return csvStorage;
    }
}