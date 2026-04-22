package me.alejo112;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CsvStorage {

    private final JavaPlugin plugin;
    private final File logsFolder;

    public CsvStorage(JavaPlugin plugin) {
        this.plugin = plugin;
        this.logsFolder = new File(plugin.getDataFolder(), "logs");
    }

    public void createLogsFolder() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }

        if (!logsFolder.exists()) {
            logsFolder.mkdirs();
        }
    }

    public void appendPendingLog(UUID uuid, String playerName, long loginMillis,
                          String ip) {
        LocalDateTime loginDateTime = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(loginMillis),
                ZoneId.systemDefault()
        );

        File playerFile = new File(logsFolder, uuid.toString() + ".csv");
        boolean newFile = !playerFile.exists();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(playerFile, true))) {

            if (newFile) {
                writer.write("NAME,DATE,HOUR,IP,ONLINE_TIME");
                writer.newLine();
            }

            String line = buildLine(
                    playerName,
                    loginDateTime,
                    ip,
                    "PENDING"
            );

            writer.write(line);
            writer.newLine();

        } catch (IOException exception) {
            plugin.getLogger().severe("Could not write log for UUID "
                    + uuid + ": " + exception.getMessage());
        }
    }

    public void completeLastPendingLog(UUID uuid, String playerName,
                                       long loginMillis, String ip,
                                       long onlineTimeSeconds) {
        File playerFile = new File(logsFolder, uuid.toString() + ".csv");

        if (!playerFile.exists()) {
            plugin.getLogger().warning("Could not complete log for UUID "
                    + uuid + " because file does not exist.");
            return;
        }

        LocalDateTime loginDateTime = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(loginMillis),
                ZoneId.systemDefault()
        );

        String pendingLine = buildLine(
                playerName,
                loginDateTime,
                ip,
                "PENDING"
        );

        String completedLine = buildLine(
                playerName,
                loginDateTime,
                ip,
                formatOnlineTime(onlineTimeSeconds)
        );

        try {
            List<String> lines = Files.readAllLines(playerFile.toPath());
            List<String> updatedLines = new ArrayList<>(lines);

            for (int i = updatedLines.size() - 1; i >= 0; i--) {
                if (updatedLines.get(i).equals(pendingLine)) {
                    updatedLines.set(i, completedLine);
                    Files.write(playerFile.toPath(), updatedLines);
                    return;
                }
            }

            plugin.getLogger().warning("No pending entry found to complete for UUID " + uuid);

        } catch (IOException exception) {
            plugin.getLogger().severe("Could not complete log for UUID "
                    + uuid + ": " + exception.getMessage());
        }
    }

    private String buildLine(String playerName, LocalDateTime dateTime,
                             String ip, String onlineTime) {
        String date = twoDigits(dateTime.getDayOfMonth())
                + "/" + twoDigits(dateTime.getMonthValue())
                + "/" + twoDigits(dateTime.getYear() % 100);

        String hour = twoDigits(dateTime.getHour())
                + ":" + twoDigits(dateTime.getMinute())
                + ":" + twoDigits(dateTime.getSecond());

        return playerName + ","
                + date + ","
                + hour + ","
                + ip + ","
                + onlineTime;
    }

    private String twoDigits(int value) {
        if (value < 10) {
            return "0" + value;
        }
        return String.valueOf(value);
    }

    private String formatOnlineTime(long totalSeconds) {
        long days = totalSeconds / 86400;
        long remainingAfterDays = totalSeconds % 86400;

        long hours = remainingAfterDays / 3600;
        long remainingAfterHours = remainingAfterDays % 3600;

        long minutes = remainingAfterHours / 60;
        long seconds = remainingAfterHours % 60;

        return days + ":"
                + twoDigits((int) hours) + ":"
                + twoDigits((int) minutes) + ":"
                + twoDigits((int) seconds);
    }
}