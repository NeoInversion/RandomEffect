package io.github.neoinversion;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import io.github.neoinversion.commands.Toggle;

public class RandomEffect extends JavaPlugin {
    @Override
    public void onEnable() {
        Bukkit.getLogger().info("Starting RandomEffect.");
        new Toggle(this);
    }
    public void onDisable() {
        Bukkit.getLogger().info("Stopping RandomEffect.");
    }
}