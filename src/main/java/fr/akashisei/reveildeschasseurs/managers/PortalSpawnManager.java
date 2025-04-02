package fr.akashisei.reveildeschasseurs.managers;

import fr.akashisei.reveildeschasseurs.ReveilDesChasseurs;
import fr.akashisei.reveildeschasseurs.generators.PortalGenerator;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class PortalSpawnManager {
    private final ReveilDesChasseurs plugin;
    private final PortalGenerator portalGenerator;
    private final Map<Location, PortalInfo> activePortals;
    private static final int PORTAL_CHECK_INTERVAL = 24000; // 20 minutes en ticks
    private static final int PORTAL_EXPIRE_TIME = 3600000; // 1 heure en millisecondes
    private static final int MIN_DISTANCE_FROM_PLAYER = 50;

    private class PortalInfo {
        long spawnTime;
        boolean isInUse;
        
        PortalInfo() {
            this.spawnTime = System.currentTimeMillis();
            this.isInUse = false;
        }
    }

    public PortalSpawnManager(ReveilDesChasseurs plugin, PortalGenerator portalGenerator) {
        this.plugin = plugin;
        this.portalGenerator = portalGenerator;
        this.activePortals = new HashMap<>();
        startPortalSpawnScheduler();
    }

    private void startPortalSpawnScheduler() {
        // Vérifier toutes les 20 minutes pour le spawn de nouveaux portails
        new BukkitRunnable() {
            @Override
            public void run() {
                spawnPortalsNearPlayers();
                checkExpiredPortals();
            }
        }.runTaskTimer(plugin, 0L, PORTAL_CHECK_INTERVAL);
    }

    private void spawnPortalsNearPlayers() {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            if (shouldSpawnPortalNearPlayer()) {
                Location spawnLoc = findSuitableLocation(player.getLocation());
                if (spawnLoc != null) {
                    spawnPortal(spawnLoc);
                }
            }
        }
    }

    private boolean shouldSpawnPortalNearPlayer() {
        return ThreadLocalRandom.current().nextDouble() < 0.3; // 30% de chance par joueur
    }

    private Location findSuitableLocation(Location playerLoc) {
        World world = playerLoc.getWorld();
        int attempts = 0;
        int maxAttempts = 10;

        while (attempts < maxAttempts) {
            // Générer une position aléatoire dans un rayon de 50 à 100 blocs
            double angle = ThreadLocalRandom.current().nextDouble() * 2 * Math.PI;
            double distance = MIN_DISTANCE_FROM_PLAYER + ThreadLocalRandom.current().nextInt(50);
            
            int x = (int) (playerLoc.getX() + Math.cos(angle) * distance);
            int z = (int) (playerLoc.getZ() + Math.sin(angle) * distance);
            int y = world.getHighestBlockYAt(x, z);

            Location loc = new Location(world, x, y, z);
            
            // Vérifier si l'emplacement est valide
            if (isLocationSuitable(loc)) {
                return loc;
            }
            
            attempts++;
        }
        
        return null;
    }

    private boolean isLocationSuitable(Location loc) {
        // Vérifier si le portail ne serait pas trop proche d'autres portails
        for (Location portalLoc : activePortals.keySet()) {
            if (portalLoc.distance(loc) < MIN_DISTANCE_FROM_PLAYER) {
                return false;
            }
        }
        return true;
    }

    private void spawnPortal(Location location) {
        String rank = determinePortalRank();
        portalGenerator.generatePortal(location, rank);
        activePortals.put(location, new PortalInfo());
        announcePortalSpawn(location, rank);
    }

    private String determinePortalRank() {
        double random = ThreadLocalRandom.current().nextDouble();
        if (random < 0.02) return "S";      // 2% chance
        if (random < 0.07) return "A";      // 5% chance
        if (random < 0.20) return "B";      // 13% chance
        if (random < 0.40) return "C";      // 20% chance
        if (random < 0.65) return "D";      // 25% chance
        return "E";                         // 35% chance
    }

    private void announcePortalSpawn(Location loc, String rank) {
        String message;
        if ("S".equals(rank)) {
            message = String.format("§4[!!!] §c§lUn portail de rang S est apparu! §7(x: %d, y: %d, z: %d)",
                loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
        } else {
            message = String.format("§6[!] §eUn portail de rang %s est apparu! §7(x: %d, y: %d, z: %d)",
                rank, loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
        }
        
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            player.sendMessage(message);
        }
    }

    private void checkExpiredPortals() {
        long currentTime = System.currentTimeMillis();
        Iterator<Map.Entry<Location, PortalInfo>> iterator = activePortals.entrySet().iterator();
        
        while (iterator.hasNext()) {
            Map.Entry<Location, PortalInfo> entry = iterator.next();
            PortalInfo info = entry.getValue();
            
            // Si le portail n'est pas utilisé et existe depuis plus d'une heure
            if (!info.isInUse && (currentTime - info.spawnTime > PORTAL_EXPIRE_TIME)) {
                portalGenerator.removePortal(entry.getKey());
                iterator.remove();
            }
        }
    }

    public void markPortalAsInUse(Location location) {
        PortalInfo info = activePortals.get(location);
        if (info != null) {
            info.isInUse = true;
        }
    }

    public void removePortalAfterCompletion(Location location) {
        if (activePortals.containsKey(location)) {
            portalGenerator.removePortal(location);
            activePortals.remove(location);
        }
    }

    public boolean isPortalActive(Location location) {
        return activePortals.containsKey(location);
    }
} 