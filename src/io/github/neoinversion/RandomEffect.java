package io.github.neoinversion;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import io.github.neoinversion.commands.Toggle;

public class RandomEffect extends JavaPlugin {

    private Toggle plugin;
    @Override
    public void onEnable() {
        Bukkit.getLogger().info("[RandomEffect] Starting...");
        plugin = new Toggle(this);
        this.getCommand("randomeffect").setExecutor(plugin);
    }
    public void onDisable() {
        Bukkit.getLogger().info("[RandomEffect] Stopping...");
        this.getCommand("randomeffect").setExecutor(this);
        plugin.stop();
        plugin = null;
    }
}