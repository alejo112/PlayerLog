package me.alejo112.playerlog;

import org.bukkit.plugin.java.JavaPlugin;

public class PlayerLog extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("PlayerLog enabled");
    }

    @Override
    public void onDisable() {
        getLogger().info("PlayerLog disabled");
    }
}