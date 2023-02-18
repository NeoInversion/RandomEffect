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
    private final RandomEffect plugin;

    private int loopFrequency = 1200;
    private int effectDuration;
    private int effectLevel;

    public Toggle(RandomEffect plugin) {
        this.plugin = plugin;
    }

    private BukkitTask task;

    public void stop() {
        if (task != null) {
            task.cancel();
            task = null;
        }
    }

    private void start() {
        List<PotionEffectType> effects = new ArrayList<>(Arrays.asList(PotionEffectType.values()));
        effects.remove(PotionEffectType.HARM); // Instant death is no fun

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

    private static class ParseResult {
        int value;
        Boolean success;

        public ParseResult(int value, Boolean success) {
            this.value = value;
            this.success = success;
        }
    }
    private ParseResult parseArgument(String cmdArg) {
        try {
             int parsed = Integer.parseInt(cmdArg);
             return new ParseResult(parsed, true);
        }
        catch (NumberFormatException e) {
            return new ParseResult(0, false);
        }
    }

    private void broadcastMsg(String message) {
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', message));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (player.hasPermission("randomeffect.use")) {
                if (cmd.getName().equalsIgnoreCase("randomeffect")) {
                    switch (args[0].toLowerCase()) {
                        case "start":
                            broadcastMsg("&l&aStarting RandomEffect!");
                            this.start();
                            break;
                        case "stop":
                            if (this.task != null) {
                                broadcastMsg("&l&cStopping RandomEffect!");
                                this.task.cancel();
                                this.task = null;
                            } else {
                                broadcastMsg("&l&cRandomEffect hasn't started!");
                            }
                            break;
                        case "modify":
                            if (args.length < 2)
                                broadcastMsg("&l&cNot enough arguments; usage /modify <setting> <value>");
                            ParseResult parsed = parseArgument(args[2]);
                            if (parsed.success) {
                                switch (args[1].toLowerCase()) {
                                    case "frequency":
                                        this.loopFrequency = parsed.value;
                                        break;
                                    case "duration":
                                        this.effectDuration = parsed.value;
                                        break;
                                    case "level":
                                        this.effectLevel = parsed.value;
                                        break;
                                }
                            }
                            else {
                                broadcastMsg("&l&cInvalid value");
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
