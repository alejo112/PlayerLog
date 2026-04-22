package me.alejo112;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Map;
import java.util.UUID;

public class ConnectionListener implements Listener {

    private final Map<UUID, SessionData> activeSessions;
    private final CsvStorage csvStorage;

    public ConnectionListener(Map<UUID, SessionData> activeSessions,
                              CsvStorage csvStorage) {
        this.activeSessions = activeSessions;
        this.csvStorage = csvStorage;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        String ip = "UNKNOWN";
        if (player.getAddress() != null
                && player.getAddress().getAddress() != null) {
            ip = player.getAddress().getAddress().getHostAddress();
        }

        SessionData sessionData = new SessionData(
                System.currentTimeMillis(),
                ip,
                player.getName()
        );

        activeSessions.put(player.getUniqueId(), sessionData);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event) {
        saveSession(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onKick(PlayerKickEvent event) {
        saveSession(event.getPlayer());
    }

    public void saveSession(Player player) {
        UUID uuid = player.getUniqueId();
        SessionData sessionData = activeSessions.remove(uuid);

        if (sessionData == null) {
            return;
        }

        long logoutMillis = System.currentTimeMillis();
        long onlineTimeSeconds = (logoutMillis - sessionData.getLoginMillis()) / 1000L;

        csvStorage.appendLog(
                uuid,
                sessionData.getPlayerName(),
                sessionData.getLoginMillis(),
                sessionData.getIpAddress(),
                onlineTimeSeconds
        );
    }
}