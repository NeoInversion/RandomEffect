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

    private int parseArgument(Player sender, String setting, String cmdArg) {
        try {
             return Integer.parseInt(cmdArg);
        }
        catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&l&cError! Invalid value provided, value reset."));
            switch (setting) {
                case "frequency":
                    return 1200;
                case "duration":
                    return ThreadLocalRandom.current().nextInt(100, 600 + 1);
                    break;
                case "level":
                    return ThreadLocalRandom.current().nextInt(0, 2 + 1);
                    break;
            }
        }
        return 0;
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
                            switch(args[1].toLowerCase()) {
                                case "frequency":
                                    this.loopFrequency = parseArgument(player, "frequency", args[2]);
                                    break;
                                case "duration":
                                    this.effectDuration = parseArgument(player, "duration", args[2]);
                                    break;
                                case "level":
                                    this.effectLevel = parseArgument(player, "level", args[2]);
                                    break;
                            }
                        default:
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&l&cInvalid command."));
                    }
                }
            }
        }
        return true;
    }
}
