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

public class Toggle implements CommandExecutor {
    @SuppressWarnings({"unused", "FieldMayBeFinal"})
    private RandomEffect plugin;

    private int loopFrequency = 1200;
    private int effectDuration;
    private int effectLevel;

    public Toggle(RandomEffect plugin) {
        this.plugin = plugin;
        plugin.getCommand("randomeffect").setExecutor(this);
    }

    private BukkitTask task;

    private void start() {
        List<PotionEffectType> effects = new ArrayList<>(Arrays.asList(PotionEffectType.values()));
        effects.remove(PotionEffectType.HARM);

        this.task = (new BukkitRunnable() {
            @Override
            public void run() {
                for (Player plr : Bukkit.getServer().getOnlinePlayers()) {
                    int random = ThreadLocalRandom.current().nextInt(effects.size() + 1);
                    if (effectDuration == 0 || effectLevel == 0) {
                        effectDuration = ThreadLocalRandom.current().nextInt(100, 600 + 1);
                        effectLevel = ThreadLocalRandom.current().nextInt(0, 2 + 1);
                    }
                    plr.addPotionEffect(new PotionEffect(effects.get(random), effectDuration, effectLevel));
                }
            }
        }).runTaskTimer(plugin, 0, loopFrequency);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (player.hasPermission("randomeffect.use")) {
                if (cmd.getName().equalsIgnoreCase("randomeffect")) {
                    switch (args[0].toLowerCase()) {
                        case "start":
                            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&l&aStarting RandomEffect!"));
                            this.start();
                            break;
                        case "stop":
                            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&l&cStopping RandomEffect!"));
                            if (this.task != null) {
                                this.task.cancel();
                                this.task = null;
                            }
                            break;
                        case "modify":
                            if (args[1] == null || args[2] == null) {
                                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&l&cError! Invalid setting or new value provided."));
                                break;
                            }
                            else if (args[1].equalsIgnoreCase("duration")) {
                                if (args[2].equalsIgnoreCase("random")) {
                                    this.effectDuration = 0;
                                }
                                else try {
                                    this.effectDuration = Integer.parseInt(args[2]);
                                }
                                catch (NumberFormatException e) {
                                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&l&cError! Invalid duration provided."));
                                }
                            }
                            else if (args[1].equalsIgnoreCase("frequency")) {
                                if (args[2].equalsIgnoreCase("default")) {
                                    this.loopFrequency = 1200;
                                }
                                else try {
                                    this.loopFrequency = Integer.parseInt(args[2]);
                                }
                                catch (NumberFormatException e) {
                                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&l&cError! Invalid duration provided."));
                                }
                            }
                            else if (args[1].equalsIgnoreCase("level")) {
                                if (args[2].equalsIgnoreCase("random")) {
                                    this.effectLevel = 0;
                                }
                                else try {
                                    this.effectLevel = Integer.parseInt(args[2]);
                                }
                                catch (NumberFormatException e) {
                                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&l&cError! Invalid level provided."));
                                }
                            }
                            break;
                        default:
                            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', "&l&cUnknown command!"));
                    }
                }
            }
        }
        return true;
    }
}