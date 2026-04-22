package me.alejo112;

public class SessionData {

    private final long loginMillis;
    private final String ipAddress;

    public SessionData(long loginMillis, String ipAddress) {
        this.loginMillis = loginMillis;
        this.ipAddress = ipAddress;
    }

    public long getLoginMillis() {
        return loginMillis;
    }

    public String getIpAddress() {
        return ipAddress;
    }
}
