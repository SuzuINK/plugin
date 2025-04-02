package fr.akashisei.reveildeschasseurs.listeners;

import fr.akashisei.reveildeschasseurs.ReveilDesChasseurs;
import fr.akashisei.reveildeschasseurs.events.DungeonBossEvent;
import fr.akashisei.reveildeschasseurs.events.DungeonCompleteEvent;
import fr.akashisei.reveildeschasseurs.events.DungeonStartEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class DungeonEventListener implements Listener {
    private final ReveilDesChasseurs plugin;

    public DungeonEventListener(ReveilDesChasseurs plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onDungeonStart(DungeonStartEvent event) {
        List<Player> players = event.getPlayers();
        Location spawnPoint = event.getInstance().getSpawnPoint();

        // Effet sonore
        for (Player player : players) {
            player.playSound(spawnPoint, Sound.ENTITY_ENDER_DRAGON_GROWL, 1.0f, 1.0f);
        }

        // Effet visuel
        new BukkitRunnable() {
            int count = 0;

            @Override
            public void run() {
                if (count >= 20) {
                    this.cancel();
                    return;
                }

                for (Player player : players) {
                    player.spawnParticle(Particle.DRAGON_BREATH, spawnPoint, 50, 1, 1, 1, 0.1);
                }

                count++;
            }
        }.runTaskTimer(plugin, 0L, 1L);

        // Message
        for (Player player : players) {
            player.sendMessage("§6§l=== Début du donjon " + event.getDungeon().getName() + " ===");
            player.sendMessage("§7Niveau requis: §e" + event.getDungeon().getMinLevel());
            player.sendMessage("§7Temps limite: §e" + (event.getDungeon().getTimeLimit() / 60) + " minutes");
        }
    }

    @EventHandler
    public void onDungeonBoss(DungeonBossEvent event) {
        List<Player> players = event.getPlayers();
        Location spawnPoint = event.getInstance().getSpawnPoint();

        // Effet sonore
        for (Player player : players) {
            player.playSound(spawnPoint, Sound.ENTITY_WITHER_SPAWN, 1.0f, 1.0f);
        }

        // Effet visuel
        new BukkitRunnable() {
            int count = 0;

            @Override
            public void run() {
                if (count >= 40) {
                    this.cancel();
                    return;
                }

                for (Player player : players) {
                    player.spawnParticle(Particle.SMOKE_LARGE, spawnPoint, 100, 2, 2, 2, 0.1);
                }

                count++;
            }
        }.runTaskTimer(plugin, 0L, 1L);

        // Message
        for (Player player : players) {
            player.sendMessage("§c§l=== BOSS DÉTECTÉ ===");
            player.sendMessage("§7Nom: §c" + event.getBossName());
            player.sendMessage("§7Vie: §c" + event.getBossHealth());
        }
    }

    @EventHandler
    public void onDungeonComplete(DungeonCompleteEvent event) {
        List<Player> players = event.getPlayers();
        Location exitLocation = event.getInstance().getExitLocation();

        // Effet sonore
        for (Player player : players) {
            player.playSound(exitLocation, Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.0f);
        }

        // Effet visuel
        new BukkitRunnable() {
            int count = 0;

            @Override
            public void run() {
                if (count >= 30) {
                    this.cancel();
                    return;
                }

                for (Player player : players) {
                    player.spawnParticle(Particle.TOTEM, exitLocation, 100, 1, 1, 1, 0.1);
                }

                count++;
            }
        }.runTaskTimer(plugin, 0L, 1L);

        // Message
        for (Player player : players) {
            player.sendMessage("§a§l=== Donjon complété ! ===");
            player.sendMessage("§7Temps: §e" + (event.getCompletionTime() / 1000) + " secondes");
            player.sendMessage("§7Kills: §e" + event.getPlayerKills().get(player));
            player.sendMessage("§7Morts: §c" + event.getPlayerDeaths().get(player));
        }
    }
} 