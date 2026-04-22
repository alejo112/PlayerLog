package me.alejo112;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
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

    public void appendLog(UUID uuid, String playerName, long loginMillis,
                          String ip, long onlineTimeSeconds) {
        LocalDateTime loginDateTime = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(loginMillis),
                ZoneId.systemDefault()
        );

        File playerFile = new File(logsFolder, uuid.toString() + ".csv");
        boolean newFile = !playerFile.exists();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(playerFile, true))) {

            if (newFile) {
                writer.write("DD,MM,YY,Hour,IP,ONLINE_TIME");
                writer.newLine();
            }

            String date = twoDigits(loginDateTime.getDayOfMonth())
                    + "/" + twoDigits(loginDateTime.getMonthValue())
                    + "/" + twoDigits(loginDateTime.getYear() % 100);

            String hour = twoDigits(loginDateTime.getHour())
                    + ":" + twoDigits(loginDateTime.getMinute())
                    + ":" + twoDigits(loginDateTime.getSecond());

            String formattedOnlineTime = formatOnlineTime(onlineTimeSeconds);

            String line = playerName + ","
                    + date + ","
                    + hour + ","
                    + ip + ","
                    + formattedOnlineTime;

            writer.write(line);
            writer.newLine();

        } catch (IOException exception) {
            plugin.getLogger().severe("Could not write log for UUID "
                    + uuid + ": " + exception.getMessage());
        }
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