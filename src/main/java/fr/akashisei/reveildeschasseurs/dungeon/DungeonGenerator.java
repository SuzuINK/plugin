package fr.akashisei.reveildeschasseurs.dungeon;

import fr.akashisei.reveildeschasseurs.ReveilDesChasseurs;
import fr.akashisei.reveildeschasseurs.models.Dungeon;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
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
            
            entity.setMaxHealth(health);
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
                    createLavaTrap(trapLoc, damage);
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
                    createTeleportTrap(trapLoc, damage);
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

    private void createLavaTrap(Location location, double damage) {
        // TODO: Implémenter la création de pièges de lave
    }

    private void createArrowTrap(Location location, double damage) {
        // TODO: Implémenter la création de pièges à flèches
    }

    private void createPoisonTrap(Location location, double damage) {
        // TODO: Implémenter la création de pièges empoisonnés
    }

    private void createExplosionTrap(Location location, double damage) {
        // TODO: Implémenter la création de pièges explosifs
    }

    private void createTeleportTrap(Location location, double damage) {
        // TODO: Implémenter la création de pièges de téléportation
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
            ConfigurationSection itemsSection = chestSection.getConfigurationSection("items");
            boolean isTrapped = chestSection.getBoolean("trapped", false);

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
            block.setType(isTrapped ? Material.TRAPPED_CHEST : Material.CHEST);

            if (block.getState() instanceof Chest) {
                Chest chest = (Chest) block.getState();
                fillChest(chest, itemsSection);
            }
        } catch (NumberFormatException e) {
            plugin.getLogger().warning("Format de coordonnées invalide: " + location);
        }
    }

    private void fillChest(Chest chest, ConfigurationSection itemsSection) {
        if (itemsSection == null) return;

        for (String key : itemsSection.getKeys(false)) {
            ConfigurationSection itemSection = itemsSection.getConfigurationSection(key);
            if (itemSection == null) continue;

            String material = itemSection.getString("material");
            int amount = itemSection.getInt("amount", 1);
            String name = itemSection.getString("name");
            List<String> lore = itemSection.getStringList("lore");

            try {
                Material mat = Material.valueOf(material);
                ItemStack item = new ItemStack(mat, amount);
                ItemMeta meta = item.getItemMeta();

                if (meta != null) {
                    if (name != null) {
                        meta.setDisplayName(name);
                    }
                    if (!lore.isEmpty()) {
                        meta.setLore(lore);
                    }
                    item.setItemMeta(meta);
                }

                chest.getInventory().addItem(item);
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Type d'item invalide: " + material);
            }
        }
    }

    private void generatePortals(DungeonInstance instance) {
        // TODO: Implémenter la génération de portails
    }

    private void startDungeonEvents(DungeonInstance instance) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (instance.getState() == DungeonInstance.DungeonState.IN_PROGRESS) {
                    if (random.nextDouble() < 0.1) { // 10% de chance par tick
                        triggerRandomEvent(instance);
                    }
                } else {
                    this.cancel();
                }
            }
        }.runTaskTimer(plugin, 20L, 20L); // Vérifier toutes les secondes
    }

    private void triggerRandomEvent(DungeonInstance instance) {
        // TODO: Implémenter des événements aléatoires
    }

    public List<Location> getTrapLocations(UUID playerId) {
        return trapLocations.getOrDefault(playerId, new ArrayList<>());
    }

    public List<Location> getMobSpawnPoints(UUID playerId) {
        return mobSpawnPoints.getOrDefault(playerId, new ArrayList<>());
    }

    public DungeonInstance createDungeonInstance(Dungeon dungeon, Player player) {
        World world = player.getWorld();
        Location startLocation = findSuitableLocation(world);
        Location spawnPoint = startLocation.clone().add(0, 1, 0);
        Location exitPoint = startLocation.clone().add(0, 1, 0);

        // Générer la structure de base
        generateBaseStructure(world);

        // Générer le labyrinthe
        generateMaze(startLocation);

        // Générer les pièges
        List<Location> traps = generateTraps(startLocation);
        trapLocations.put(player.getUniqueId(), traps);

        // Générer les points d'apparition des mobs
        List<Location> mobs = generateMobs(startLocation);
        mobSpawnPoints.put(player.getUniqueId(), mobs);

        return new DungeonInstance(dungeon, player.getUniqueId(), spawnPoint, exitPoint);
    }

    private Location findSuitableLocation(World world) {
        // TODO: Implémenter la recherche d'un emplacement approprié
        return world.getSpawnLocation();
    }

    private void generateMaze(Location startLocation) {
        // TODO: Implémenter la génération du labyrinthe
    }

    private List<Location> generateTraps(Location startLocation) {
        List<Location> traps = new ArrayList<>();
        int numTraps = random.nextInt(MAX_TRAPS - MIN_TRAPS + 1) + MIN_TRAPS;

        for (int i = 0; i < numTraps; i++) {
            // TODO: Implémenter la génération des pièges
            Location trapLocation = startLocation.clone();
            traps.add(trapLocation);
        }

        return traps;
    }

    private List<Location> generateMobs(Location startLocation) {
        List<Location> mobs = new ArrayList<>();
        int numMobs = random.nextInt(MAX_MOBS - MIN_MOBS + 1) + MIN_MOBS;

        for (int i = 0; i < numMobs; i++) {
            // TODO: Implémenter la génération des points d'apparition des mobs
            Location mobLocation = startLocation.clone();
            mobs.add(mobLocation);
        }

        return mobs;
    }

    private void createSpikeTrap(Location location) {
        // TODO: Implémenter le piège à pointes
    }

    private void createLavaTrap(Location location) {
        // TODO: Implémenter le piège de lave
    }

    private void createArrowTrap(Location location) {
        // TODO: Implémenter le piège de flèches
    }

    public void cleanup(UUID dungeonId) {
        mobSpawnPoints.remove(dungeonId);
        trapLocations.remove(dungeonId);
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