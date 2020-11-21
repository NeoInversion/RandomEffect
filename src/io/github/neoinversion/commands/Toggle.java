package io.github.neoinversion.commands;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import io.github.neoinversion.RandomEffect;

public class Toggle implements CommandExecutor{
    @SuppressWarnings({"unused", "FieldMayBeFinal"})
    private RandomEffect plugin;

    public Toggle(RandomEffect plugin) {
        this.plugin = plugin;
        plugin.getCommand("randomeffect").setExecutor(this);
    }

    private BukkitTask task;

    private void start() {
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&l&aStarting RandomEffect!"));
        List<PotionEffectType> effects = new ArrayList<>(Arrays.asList(PotionEffectType.values()));
        effects.remove(PotionEffectType.HARM);
        this.task = (new BukkitRunnable() {
            @Override
            public void run() {
                for (Player plr : Bukkit.getServer().getOnlinePlayers()) {
                    int random = ThreadLocalRandom.current().nextInt(PotionEffectType.values().length + 1);
                    int duration = ThreadLocalRandom.current().nextInt(100, 600 + 1);
                    int level = ThreadLocalRandom.current().nextInt(0, 2 + 1);
                    plr.addPotionEffect(new PotionEffect(effects.get(random), duration, level));
                }
            }
        }).runTaskTimer(plugin, 0, 1200);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (player.hasPermission("randomeffect.use")) {
                if (cmd.getName().equalsIgnoreCase("randomeffect")) {
                    if (args[0].equalsIgnoreCase("start")) {
                        this.start();
                    }
                    else if (args[0].equalsIgnoreCase("stop")) {
                        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&l&cStopping RandomEffect!"));
                        if (this.task != null) {
                            this.task.cancel();
                            this.task = null;
                        }
                    }
                }
            }
        }
        return true;
    }
}