package me.alejo112;

public class SessionData {

    private final long loginMillis;
    private final String ipAddress;
    private final String playerName;

    public SessionData(long loginMillis, String ipAddress, String playerName) {
        this.loginMillis = loginMillis;
        this.ipAddress = ipAddress;
        this.playerName = playerName;
    }

    public long getLoginMillis() {
        return loginMillis;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public String getPlayerName() {
        return playerName;
    }
}
