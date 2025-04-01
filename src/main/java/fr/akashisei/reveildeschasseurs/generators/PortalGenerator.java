package fr.akashisei.reveildeschasseurs.generators;

import fr.akashisei.reveildeschasseurs.ReveilDesChasseurs;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;

public class PortalGenerator {
    private final ReveilDesChasseurs plugin;
    private final Map<Location, String> activePortals;

    public PortalGenerator(ReveilDesChasseurs plugin) {
        this.plugin = plugin;
        this.activePortals = new HashMap<>();
    }

    public void generatePortal(Location location, String dungeonRank) {
        // Déterminer le type de portail basé sur le rang
        boolean isRankPortal = "S".equals(dungeonRank) || "A".equals(dungeonRank);
        
        // Dimensions du portail ovale
        int height = 5;
        int width = 4;
        
        // Créer la structure du portail
        createPortalFrame(location, height, width, isRankPortal);
        
        // Ajouter les effets de particules
        startPortalEffects(location, height, width, isRankPortal);
        
        // Enregistrer le portail
        activePortals.put(location, dungeonRank);
    }

    private void createPortalFrame(Location center, int height, int width, boolean isRankPortal) {
        World world = center.getWorld();
        Material frameMaterial = isRankPortal ? Material.OBSIDIAN : Material.CRYING_OBSIDIAN;
        
        // Créer le cadre ovale
        for (double angle = 0; angle < 2 * Math.PI; angle += 0.2) {
            double x = width * Math.cos(angle);
            double y = height * Math.sin(angle);
            
            Location blockLoc = center.clone().add(x, y, 0);
            Block block = blockLoc.getBlock();
            block.setType(frameMaterial);
        }
        
        // Ajouter des runes sur le cadre
        addRuneBlocks(center, height, width, isRankPortal);
    }

    private void addRuneBlocks(Location center, int height, int width, boolean isRankPortal) {
        Material runeMaterial = isRankPortal ? Material.REDSTONE_BLOCK : Material.LAPIS_BLOCK;
        
        // Placer des runes à intervalles réguliers
        for (double angle = 0; angle < 2 * Math.PI; angle += Math.PI / 4) {
            double x = (width + 0.5) * Math.cos(angle);
            double y = (height + 0.5) * Math.sin(angle);
            
            Location runeLoc = center.clone().add(x, y, 0);
            Block runeBlock = runeLoc.getBlock();
            runeBlock.setType(runeMaterial);
        }
    }

    private void startPortalEffects(Location center, int height, int width, boolean isRankPortal) {
        new BukkitRunnable() {
            double rotation = 0;
            
            @Override
            public void run() {
                if (!activePortals.containsKey(center)) {
                    this.cancel();
                    return;
                }
                
                // Effet de vortex
                createVortexEffect(center, height, width, rotation, isRankPortal);
                
                // Effet de runes
                createRuneEffect(center, height, width, rotation, isRankPortal);
                
                // Effet d'aura
                createAuraEffect(center, height, width, isRankPortal);
                
                rotation += 0.1;
                if (rotation >= 2 * Math.PI) rotation = 0;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    private void createVortexEffect(Location center, int height, int width, double rotation, boolean isRankPortal) {
        Particle.DustOptions dustOptions;
        if (isRankPortal) {
            // Rouge pour les portails de rang
            dustOptions = new Particle.DustOptions(org.bukkit.Color.fromRGB(255, 0, 0), 1);
        } else {
            // Bleu pour les portails normaux
            dustOptions = new Particle.DustOptions(org.bukkit.Color.fromRGB(0, 0, 255), 1);
        }
        
        for (double t = 0; t < 2 * Math.PI; t += Math.PI / 16) {
            double spiralX = width * Math.cos(t + rotation) * (1 - t / (2 * Math.PI));
            double spiralY = height * Math.sin(t + rotation) * (1 - t / (2 * Math.PI));
            
            Location particleLoc = center.clone().add(spiralX, spiralY, 0);
            center.getWorld().spawnParticle(Particle.REDSTONE, particleLoc, 1, 0, 0, 0, 0, dustOptions);
        }
    }

    private void createRuneEffect(Location center, int height, int width, double rotation, boolean isRankPortal) {
        Particle.DustOptions runeOptions;
        if (isRankPortal) {
            // Orange pour les runes des portails de rang
            runeOptions = new Particle.DustOptions(org.bukkit.Color.fromRGB(255, 140, 0), 1);
        } else {
            // Bleu ciel pour les runes des portails normaux
            runeOptions = new Particle.DustOptions(org.bukkit.Color.fromRGB(0, 191, 255), 1);
        }
        
        // Créer des runes flottantes
        for (int i = 0; i < 8; i++) {
            double angle = (Math.PI * i / 4) + rotation;
            double x = (width + 0.5) * Math.cos(angle);
            double y = (height + 0.5) * Math.sin(angle);
            
            Location runeLoc = center.clone().add(x, y, 0);
            center.getWorld().spawnParticle(Particle.REDSTONE, runeLoc, 3, 0.1, 0.1, 0.1, 0, runeOptions);
        }
    }

    private void createAuraEffect(Location center, int height, int width, boolean isRankPortal) {
        Particle.DustOptions auraOptions;
        if (isRankPortal) {
            // Rouge foncé pour l'aura des portails de rang
            auraOptions = new Particle.DustOptions(org.bukkit.Color.fromRGB(139, 0, 0), 1);
        } else {
            // Bleu foncé pour l'aura des portails normaux
            auraOptions = new Particle.DustOptions(org.bukkit.Color.fromRGB(0, 0, 128), 1);
        }
        
        // Créer une aura autour du portail
        for (double angle = 0; angle < 2 * Math.PI; angle += Math.PI / 16) {
            double x = (width + 1) * Math.cos(angle);
            double y = (height + 1) * Math.sin(angle);
            
            Location auraLoc = center.clone().add(x, y, 0);
            Vector direction = auraLoc.toVector().subtract(center.toVector()).normalize();
            
            center.getWorld().spawnParticle(Particle.REDSTONE, auraLoc, 1, 
                direction.getX() * 0.1, 
                direction.getY() * 0.1, 
                direction.getZ() * 0.1, 
                0, auraOptions);
        }
    }

    public void removePortal(Location location) {
        if (!activePortals.containsKey(location)) return;
        
        // Supprimer la structure physique
        int height = 5;
        int width = 4;
        
        for (double angle = 0; angle < 2 * Math.PI; angle += 0.2) {
            double x = width * Math.cos(angle);
            double y = height * Math.sin(angle);
            
            Location blockLoc = location.clone().add(x, y, 0);
            blockLoc.getBlock().setType(Material.AIR);
        }
        
        // Supprimer les runes
        for (double angle = 0; angle < 2 * Math.PI; angle += Math.PI / 4) {
            double x = (width + 0.5) * Math.cos(angle);
            double y = (height + 0.5) * Math.sin(angle);
            
            Location runeLoc = location.clone().add(x, y, 0);
            runeLoc.getBlock().setType(Material.AIR);
        }
        
        // Retirer de la liste des portails actifs
        activePortals.remove(location);
    }

    public boolean isPortalLocation(Location location) {
        return activePortals.containsKey(location);
    }

    public String getPortalRank(Location location) {
        return activePortals.get(location);
    }
} 