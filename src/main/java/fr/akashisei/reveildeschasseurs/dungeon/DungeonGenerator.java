package fr.akashisei.reveildeschasseurs.dungeon;

import fr.akashisei.reveildeschasseurs.ReveilDesChasseurs;
import fr.akashisei.reveildeschasseurs.models.Dungeon;
import fr.akashisei.reveildeschasseurs.models.DungeonTheme;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class DungeonGenerator {
    private final ReveilDesChasseurs plugin;
    private final Random random;
    private final Map<UUID, List<Location>> trapLocations;
    private final Map<UUID, List<Location>> mobSpawnPoints;
    private static final int MIN_ROOMS = 5;
    private static final int MAX_ROOMS = 10;
    private static final int MIN_TRAPS = 3;
    private static final int MAX_TRAPS = 7;
    private static final int MIN_MOBS = 5;
    private static final int MAX_MOBS = 15;

    public DungeonGenerator(ReveilDesChasseurs plugin) {
        this.plugin = plugin;
        this.random = new Random();
        this.trapLocations = new HashMap<>();
        this.mobSpawnPoints = new HashMap<>();
    }

    public void generateDungeonContent(DungeonInstance instance) {
        World world = instance.getSpawnPoint().getWorld();
        if (world == null) return;

        // Générer la structure de base
        generateBaseStructure(world);

        // Générer le labyrinthe
        generateMaze(instance);

        // Générer les mobs
        generateMobs(instance);

        // Générer les pièges
        generateTraps(instance);

        // Générer les coffres
        generateChests(instance);

        // Générer les portails
        generatePortals(instance);

        // Démarrer le système d'événements
        startDungeonEvents(instance);
    }

    private void generateBaseStructure(World world) {
        // Créer une plateforme de spawn plus élaborée
        Location spawnLoc = world.getSpawnLocation();
        for (int x = -7; x <= 7; x++) {
            for (int z = -7; z <= 7; z++) {
                for (int y = -1; y <= 2; y++) {
                    Location loc = spawnLoc.clone().add(x, y, z);
                    Block block = world.getBlockAt(loc);
                    
                    if (y == -1) {
                        block.setType(Material.STONE_BRICKS);
                    } else if (y == 0) {
                        block.setType(Material.OBSIDIAN);
                    } else if (y == 1) {
                        if (x == 0 && z == 0) {
                            block.setType(Material.END_PORTAL_FRAME);
                        } else {
                            block.setType(Material.PURPUR_BLOCK);
                        }
                    } else {
                        if (Math.abs(x) == 7 || Math.abs(z) == 7) {
                            block.setType(Material.GLASS);
                        }
                    }
                }
            }
        }
    }

    private void generateMaze(DungeonInstance instance) {
        // TODO: Implémenter un algorithme de génération de labyrinthe
        // Utiliser des blocs de pierre et d'obsidienne pour créer des murs
        // Ajouter des pièces secrètes et des passages
    }

    private void generateMobs(DungeonInstance instance) {
        Dungeon dungeon = instance.getDungeon();
        ConfigurationSection mobsSection = plugin.getConfigManager().getConfig()
            .getConfigurationSection("dungeons." + dungeon.getId() + ".mobs");

        if (mobsSection == null) return;

        List<Location> spawnPoints = new ArrayList<>();
        mobSpawnPoints.put(instance.getPlayer().getUniqueId(), spawnPoints);

        for (String key : mobsSection.getKeys(false)) {
            ConfigurationSection mobSection = mobsSection.getConfigurationSection(key);
            if (mobSection == null) continue;

            String type = mobSection.getString("type");
            int count = mobSection.getInt("count");
            double health = mobSection.getDouble("health");
            double damage = mobSection.getDouble("damage");
            boolean isBoss = mobSection.getBoolean("is_boss", false);

            for (int i = 0; i < count; i++) {
                Location spawnLoc = getRandomSpawnPoint(instance);
                spawnPoints.add(spawnLoc);
                spawnMob(instance, type, health, damage, isBoss, spawnLoc);
            }
        }
    }

    private Location getRandomSpawnPoint(DungeonInstance instance) {
        Location center = instance.getSpawnPoint();
        int radius = 50;
        return center.clone().add(
            random.nextInt(radius * 2) - radius,
            0,
            random.nextInt(radius * 2) - radius
        );
    }

    private void spawnMob(DungeonInstance instance, String type, double health, double damage, boolean isBoss, Location spawnLoc) {
        try {
            EntityType entityType = EntityType.valueOf(type);
            LivingEntity entity = (LivingEntity) instance.getSpawnPoint().getWorld()
                .spawnEntity(spawnLoc, entityType);
            
            entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(health);
            entity.setHealth(health);
            
            // Personnalisation du mob
            String name = isBoss ? "§c§l" + type : "§6" + type;
            entity.setCustomName(name);
            entity.setCustomNameVisible(true);

            // Ajouter des effets aléatoires
            if (random.nextDouble() < 0.3) {
                entity.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 60, 1));
            }
            if (random.nextDouble() < 0.2) {
                entity.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 60, 1));
            }

            // Si c'est un boss, ajouter des effets spéciaux
            if (isBoss) {
                entity.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 60, 2));
                entity.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20 * 60, 1));
            }
        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning("Type de mob invalide: " + type);
        }
    }

    private void generateTraps(DungeonInstance instance) {
        Dungeon dungeon = instance.getDungeon();
        ConfigurationSection trapsSection = plugin.getConfigManager().getConfig()
            .getConfigurationSection("dungeons." + dungeon.getId() + ".traps");

        if (trapsSection == null) return;

        List<Location> trapLocs = new ArrayList<>();
        trapLocations.put(instance.getPlayer().getUniqueId(), trapLocs);

        for (String key : trapsSection.getKeys(false)) {
            ConfigurationSection trapSection = trapsSection.getConfigurationSection(key);
            if (trapSection == null) continue;

            String type = trapSection.getString("type");
            String location = trapSection.getString("location");
            double damage = trapSection.getDouble("damage");

            Location trapLoc = createTrap(instance, type, location, damage);
            if (trapLoc != null) {
                trapLocs.add(trapLoc);
            }
        }
    }

    private Location createTrap(DungeonInstance instance, String type, String location, double damage) {
        String[] coords = location.split(",");
        if (coords.length != 3) return null;

        try {
            int x = Integer.parseInt(coords[0]);
            int y = Integer.parseInt(coords[1]);
            int z = Integer.parseInt(coords[2]);

            Location trapLoc = instance.getSpawnPoint().clone().add(x, y, z);

            switch (type.toLowerCase()) {
                case "lava":
                    createLavaTrap(trapLoc);
                    break;
                case "arrow":
                    createArrowTrap(trapLoc, damage);
                    break;
                case "poison":
                    createPoisonTrap(trapLoc, damage);
                    break;
                case "explosion":
                    createExplosionTrap(trapLoc, damage);
                    break;
                case "teleport":
                    createTeleportTrap(trapLoc);
                    break;
                default:
                    plugin.getLogger().warning("Type de piège invalide: " + type);
                    return null;
            }

            return trapLoc;
        } catch (NumberFormatException e) {
            plugin.getLogger().warning("Format de coordonnées invalide: " + location);
            return null;
        }
    }

    private void createLavaTrap(Location location) {
        Block block = location.getBlock();
        block.setType(Material.STONE_PRESSURE_PLATE);
        
        // Le bloc sous la plaque de pression
        Block below = block.getRelative(0, -1, 0);
        below.setType(Material.REDSTONE_BLOCK);
        
        // Le bloc qui contiendra la lave
        Block lavaBlock = block.getRelative(0, 2, 0);
        lavaBlock.setType(Material.LAVA);
    }

    private void createArrowTrap(Location location, double damage) {
        Block block = location.getBlock();
        block.setType(Material.STONE_PRESSURE_PLATE);
        
        // Placer les dispensers
        Block dispenser1 = block.getRelative(2, 1, 0);
        Block dispenser2 = block.getRelative(-2, 1, 0);
        Block dispenser3 = block.getRelative(0, 1, 2);
        Block dispenser4 = block.getRelative(0, 1, -2);
        
        dispenser1.setType(Material.DISPENSER);
        dispenser2.setType(Material.DISPENSER);
        dispenser3.setType(Material.DISPENSER);
        dispenser4.setType(Material.DISPENSER);
    }

    private void createPoisonTrap(Location location, double damage) {
        Block block = location.getBlock();
        block.setType(Material.STONE_PRESSURE_PLATE);
        
        // Le bloc sous la plaque de pression
        Block below = block.getRelative(0, -1, 0);
        below.setType(Material.REDSTONE_BLOCK);
    }

    private void createExplosionTrap(Location location, double damage) {
        Block block = location.getBlock();
        block.setType(Material.STONE_PRESSURE_PLATE);
        
        // Le bloc sous la plaque de pression
        Block below = block.getRelative(0, -1, 0);
        below.setType(Material.TNT);
    }

    private void createTeleportTrap(Location location) {
        Block block = location.getBlock();
        block.setType(Material.STONE_PRESSURE_PLATE);
        
        // Le bloc sous la plaque de pression
        Block below = block.getRelative(0, -1, 0);
        below.setType(Material.END_STONE);
    }

    private void generateChests(DungeonInstance instance) {
        Dungeon dungeon = instance.getDungeon();
        ConfigurationSection chestsSection = plugin.getConfigManager().getConfig()
            .getConfigurationSection("dungeons." + dungeon.getId() + ".chests");

        if (chestsSection == null) return;

        for (String key : chestsSection.getKeys(false)) {
            ConfigurationSection chestSection = chestsSection.getConfigurationSection(key);
            if (chestSection == null) continue;

            String location = chestSection.getString("location");
            boolean isTrapped = chestSection.getBoolean("trapped", false);
            ConfigurationSection itemsSection = chestSection.getConfigurationSection("items");

            createChest(instance, location, itemsSection, isTrapped);
        }
    }

    private void createChest(DungeonInstance instance, String location, ConfigurationSection itemsSection, boolean isTrapped) {
        String[] coords = location.split(",");
        if (coords.length != 3) return;

        try {
            int x = Integer.parseInt(coords[0]);
            int y = Integer.parseInt(coords[1]);
            int z = Integer.parseInt(coords[2]);

            Location chestLoc = instance.getSpawnPoint().clone().add(x, y, z);
            Block block = chestLoc.getBlock();
            block.setType(Material.CHEST);

            if (block.getState() instanceof Chest) {
                Chest chest = (Chest) block.getState();
                fillChest(chest, instance.getDungeon());

                if (isTrapped) {
                    // TODO: Ajouter un piège au coffre
                }
            }
        } catch (NumberFormatException e) {
            plugin.getLogger().warning("Coordonnées de coffre invalides: " + location);
        }
    }

    private void fillChest(Chest chest, Dungeon dungeon) {
        // TODO: Implémenter le remplissage des coffres avec des items aléatoires
    }

    private void generatePortals(DungeonInstance instance) {
        // TODO: Implémenter la génération de portails
    }

    private void startDungeonEvents(DungeonInstance instance) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (instance.getState() != DungeonInstance.DungeonState.IN_PROGRESS) {
                    this.cancel();
                    return;
                }

                triggerRandomEvent(instance);
            }
        }.runTaskTimer(plugin, 20 * 60, 20 * 60); // Toutes les minutes
    }

    private void triggerRandomEvent(DungeonInstance instance) {
        // TODO: Implémenter les événements aléatoires
    }

    public List<Location> getTrapLocations(UUID playerId) {
        return trapLocations.getOrDefault(playerId, new ArrayList<>());
    }

    public List<Location> getMobSpawnPoints(UUID playerId) {
        return mobSpawnPoints.getOrDefault(playerId, new ArrayList<>());
    }

    public DungeonInstance createDungeonInstance(Dungeon dungeon, Player player) {
        Location spawnPoint = findSuitableLocation(player.getWorld());
        if (spawnPoint == null) return null;

        DungeonInstance instance = new DungeonInstance(dungeon, player.getUniqueId(), spawnPoint, player.getLocation());
        generateDungeonContent(instance);
        return instance;
    }

    private Location findSuitableLocation(World world) {
        // TODO: Implémenter la recherche d'un emplacement approprié
        return world.getSpawnLocation();
    }

    private void generateMaze(Location startLocation) {
        // TODO: Implémenter la génération du labyrinthe
    }

    private List<Location> generateTraps(Location startLocation) {
        // TODO: Implémenter la génération des pièges
        return new ArrayList<>();
    }

    private List<Location> generateMobs(Location startLocation) {
        // TODO: Implémenter la génération des mobs
        return new ArrayList<>();
    }

    private void createSpikeTrap(Location location) {
        // TODO: Implémenter le piège à pointes
    }

    private void createArrowTrap(Location location) {
        // TODO: Implémenter le piège à flèches
    }

    public void cleanup(UUID dungeonId) {
        // TODO: Implémenter le nettoyage du donjon
    }

    private static class Room {
        private final Location location;
        private final int width;
        private final int length;

        public Room(Location location, int width, int length) {
            this.location = location;
            this.width = width;
            this.length = length;
        }

        public Location getLocation() {
            return location;
        }

        public int getWidth() {
            return width;
        }

        public int getLength() {
            return length;
        }
    }

    public enum DungeonTheme {
        MEDIEVAL,
        NETHER,
        END,
        CAVE,
        TEMPLE,
        MINE,
        FOREST,
        DESERT,
        ICE,
        OCEAN
    }
} 