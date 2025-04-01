package fr.akashisei.reveildeschasseurs.dungeon;

import fr.akashisei.reveildeschasseurs.ReveilDesChasseurs;
import fr.akashisei.reveildeschasseurs.models.Dungeon;
import fr.akashisei.reveildeschasseurs.models.DungeonTheme;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;

import java.util.*;

public class DungeonGenerator {
    private final ReveilDesChasseurs plugin;
    private final Random random;
    private final Map<UUID, List<Location>> mobSpawnPoints;
    private static final int MIN_MOBS = 5;
    private static final int MAX_MOBS = 15;

    public DungeonGenerator(ReveilDesChasseurs plugin) {
        this.plugin = plugin;
        this.random = new Random();
        this.mobSpawnPoints = new HashMap<>();
    }

    public void generateDungeonContent(DungeonInstance instance) {
        World world = instance.getWorld();
        Location spawnLoc = instance.getSpawnLocation();
        Location exitLoc = instance.getExitLocation();
        DungeonTheme theme = instance.getDungeon().getTheme();

        // Générer la structure de base avec le thème
        generateThemedStructure(world, spawnLoc, exitLoc, theme);

        // Générer les pièces spéciales
        generateSpecialRooms(world, spawnLoc, exitLoc, theme);

        // Générer les mobs selon le thème
        generateThemeMobs(world, spawnLoc, exitLoc, theme);

        // Générer les coffres avec du butin thématique
        generateThemedChests(world, spawnLoc, exitLoc, theme);
    }

    private void generateThemedStructure(World world, Location spawnLoc, Location exitLoc, DungeonTheme theme) {
        int minX = Math.min(spawnLoc.getBlockX(), exitLoc.getBlockX()) - 10;
        int maxX = Math.max(spawnLoc.getBlockX(), exitLoc.getBlockX()) + 10;
        int minZ = Math.min(spawnLoc.getBlockZ(), exitLoc.getBlockZ()) - 10;
        int maxZ = Math.max(spawnLoc.getBlockZ(), exitLoc.getBlockZ()) + 10;
        int minY = Math.min(spawnLoc.getBlockY(), exitLoc.getBlockY()) - 2;
        int maxY = Math.max(spawnLoc.getBlockY(), exitLoc.getBlockY()) + 5;

        List<Material> mainBlocks = theme.getMainBlocks();
        List<Material> decorativeBlocks = theme.getDecorativeBlocks();

        // Générer les murs et le sol avec les blocs thématiques
        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    if (x == minX || x == maxX || z == minZ || z == maxZ || y == minY || y == maxY) {
                        Material material = mainBlocks.get(random.nextInt(mainBlocks.size()));
                        world.getBlockAt(x, y, z).setType(material);
                    } else if (random.nextDouble() < 0.1) { // 10% de chance d'avoir un bloc décoratif
                        Material material = decorativeBlocks.get(random.nextInt(decorativeBlocks.size()));
                        world.getBlockAt(x, y, z).setType(material);
                    }
                }
            }
        }

        // Ajouter l'éclairage thématique
        for (int x = minX + 2; x <= maxX - 2; x += 4) {
            for (int z = minZ + 2; z <= maxZ - 2; z += 4) {
                world.getBlockAt(x, minY + 1, z).setType(theme.getLightSource());
            }
        }
    }

    private void generateSpecialRooms(World world, Location spawnLoc, Location exitLoc, DungeonTheme theme) {
        // Générer la salle du boss
        Location bossRoom = findSuitableLocation(world, spawnLoc, exitLoc);
        generateBossRoom(world, bossRoom, theme);

        // Générer la salle du trésor
        Location treasureRoom = findSuitableLocation(world, spawnLoc, exitLoc);
        generateTreasureRoom(world, treasureRoom, theme);
    }

    private void generateBossRoom(World world, Location center, DungeonTheme theme) {
        int size = 15;
        int height = 8;

        // Créer une grande salle circulaire
        for (int x = -size; x <= size; x++) {
            for (int y = 0; y <= height; y++) {
                for (int z = -size; z <= size; z++) {
                    if (x*x + z*z <= size*size) {
                        Location loc = center.clone().add(x, y, z);
                        if (y == 0 || y == height || x*x + z*z >= (size-1)*(size-1)) {
                            loc.getBlock().setType(theme.getMainBlocks().get(0));
                        } else {
                            loc.getBlock().setType(Material.AIR);
                        }
                    }
                }
            }
        }

        // Ajouter des décorations thématiques
        for (int i = 0; i < 8; i++) {
            double angle = i * Math.PI / 4;
            int x = (int) (Math.cos(angle) * (size - 2));
            int z = (int) (Math.sin(angle) * (size - 2));
            Location pillarBase = center.clone().add(x, 1, z);
            
            for (int y = 0; y < height - 1; y++) {
                pillarBase.clone().add(0, y, 0).getBlock().setType(theme.getDecorativeBlocks().get(0));
            }
        }
    }

    private void generateTreasureRoom(World world, Location center, DungeonTheme theme) {
        int size = 8;
        int height = 5;

        // Créer une salle carrée
        for (int x = -size; x <= size; x++) {
            for (int y = 0; y <= height; y++) {
                for (int z = -size; z <= size; z++) {
                    Location loc = center.clone().add(x, y, z);
                    if (y == 0 || y == height || Math.abs(x) == size || Math.abs(z) == size) {
                        loc.getBlock().setType(theme.getMainBlocks().get(0));
                    } else {
                        loc.getBlock().setType(Material.AIR);
                    }
                }
            }
        }

        // Ajouter des coffres aux coins
        for (int x = -size + 2; x <= size - 2; x += size - 3) {
            for (int z = -size + 2; z <= size - 2; z += size - 3) {
                Location chestLoc = center.clone().add(x, 1, z);
                chestLoc.getBlock().setType(Material.CHEST);
                if (chestLoc.getBlock().getState() instanceof Chest) {
                    fillChestWithThemedLoot((Chest) chestLoc.getBlock().getState(), theme);
                }
            }
        }
    }

    private void generateThemeMobs(World world, Location spawnLoc, Location exitLoc, DungeonTheme theme) {
        int mobCount = random.nextInt(MAX_MOBS - MIN_MOBS + 1) + MIN_MOBS;
        List<String> commonMobs = theme.getCommonMobs();

        for (int i = 0; i < mobCount; i++) {
            Location mobLoc = findSuitableLocation(world, spawnLoc, exitLoc);
            String mobType = commonMobs.get(random.nextInt(commonMobs.size()));
            // Utiliser MythicMobs pour spawner les mobs personnalisés
            plugin.getMobManager().spawnMob(mobType, mobLoc, 1);
            mobSpawnPoints.computeIfAbsent(UUID.randomUUID(), k -> new ArrayList<>()).add(mobLoc);
        }
    }

    private void generateThemedChests(World world, Location spawnLoc, Location exitLoc, DungeonTheme theme) {
        int chestCount = random.nextInt(5) + 3;
        for (int i = 0; i < chestCount; i++) {
            Location chestLoc = findSuitableLocation(world, spawnLoc, exitLoc);
            chestLoc.getBlock().setType(Material.CHEST);
            if (chestLoc.getBlock().getState() instanceof Chest) {
                fillChestWithThemedLoot((Chest) chestLoc.getBlock().getState(), theme);
            }
        }
    }

    private void fillChestWithThemedLoot(Chest chest, DungeonTheme theme) {
        // Obtenir l'inventaire du coffre
        org.bukkit.inventory.Inventory chestInventory = chest.getInventory();
        
        // Nombre d'objets à ajouter (entre 3 et 8)
        int itemCount = random.nextInt(6) + 3;
        
        // Liste des objets possibles selon le thème
        List<org.bukkit.inventory.ItemStack> possibleItems = new ArrayList<>();
        
        // Ajouter les objets thématiques selon le thème
        switch (theme) {
            case MINE:
                possibleItems.add(new org.bukkit.inventory.ItemStack(Material.IRON_INGOT, random.nextInt(3) + 1));
                possibleItems.add(new org.bukkit.inventory.ItemStack(Material.GOLD_INGOT, random.nextInt(2) + 1));
                possibleItems.add(new org.bukkit.inventory.ItemStack(Material.DIAMOND, random.nextInt(2) + 1));
                possibleItems.add(new org.bukkit.inventory.ItemStack(Material.EMERALD, random.nextInt(2) + 1));
                possibleItems.add(new org.bukkit.inventory.ItemStack(Material.REDSTONE, random.nextInt(4) + 1));
                possibleItems.add(new org.bukkit.inventory.ItemStack(Material.LAPIS_LAZULI, random.nextInt(3) + 1));
                break;
            case TEMPLE:
                possibleItems.add(new org.bukkit.inventory.ItemStack(Material.GOLDEN_APPLE, random.nextInt(2) + 1));
                possibleItems.add(new org.bukkit.inventory.ItemStack(Material.ENCHANTED_GOLDEN_APPLE, 1));
                possibleItems.add(new org.bukkit.inventory.ItemStack(Material.GOLDEN_CARROT, random.nextInt(4) + 1));
                possibleItems.add(new org.bukkit.inventory.ItemStack(Material.GOLD_INGOT, random.nextInt(3) + 1));
                possibleItems.add(new org.bukkit.inventory.ItemStack(Material.DIAMOND, random.nextInt(2) + 1));
                break;
            case FORET:
                possibleItems.add(new org.bukkit.inventory.ItemStack(Material.EMERALD, random.nextInt(3) + 1));
                possibleItems.add(new org.bukkit.inventory.ItemStack(Material.GOLDEN_APPLE, random.nextInt(2) + 1));
                possibleItems.add(new org.bukkit.inventory.ItemStack(Material.GOLDEN_CARROT, random.nextInt(3) + 1));
                possibleItems.add(new org.bukkit.inventory.ItemStack(Material.IRON_INGOT, random.nextInt(2) + 1));
                possibleItems.add(new org.bukkit.inventory.ItemStack(Material.DIAMOND, random.nextInt(2) + 1));
                break;
            case NETHER:
                possibleItems.add(new org.bukkit.inventory.ItemStack(Material.NETHERITE_SCRAP, random.nextInt(2) + 1));
                possibleItems.add(new org.bukkit.inventory.ItemStack(Material.GOLD_INGOT, random.nextInt(3) + 1));
                possibleItems.add(new org.bukkit.inventory.ItemStack(Material.DIAMOND, random.nextInt(2) + 1));
                possibleItems.add(new org.bukkit.inventory.ItemStack(Material.BLAZE_POWDER, random.nextInt(3) + 1));
                possibleItems.add(new org.bukkit.inventory.ItemStack(Material.GHAST_TEAR, random.nextInt(2) + 1));
                break;
            case OCEAN:
                possibleItems.add(new org.bukkit.inventory.ItemStack(Material.PRISMARINE_SHARD, random.nextInt(3) + 1));
                possibleItems.add(new org.bukkit.inventory.ItemStack(Material.PRISMARINE_CRYSTALS, random.nextInt(2) + 1));
                possibleItems.add(new org.bukkit.inventory.ItemStack(Material.NAUTILUS_SHELL, random.nextInt(2) + 1));
                possibleItems.add(new org.bukkit.inventory.ItemStack(Material.DIAMOND, random.nextInt(2) + 1));
                possibleItems.add(new org.bukkit.inventory.ItemStack(Material.EMERALD, random.nextInt(2) + 1));
                break;
        }
        
        // Ajouter des objets aléatoires dans le coffre
        for (int i = 0; i < itemCount; i++) {
            if (!possibleItems.isEmpty()) {
                int index = random.nextInt(possibleItems.size());
                org.bukkit.inventory.ItemStack item = possibleItems.get(index);
                chestInventory.setItem(random.nextInt(chestInventory.getSize()), item);
            }
        }
    }

    private Location findSuitableLocation(World world, Location start, Location end) {
        int minX = Math.min(start.getBlockX(), end.getBlockX()) + 2;
        int maxX = Math.max(start.getBlockX(), end.getBlockX()) - 2;
        int minZ = Math.min(start.getBlockZ(), end.getBlockZ()) + 2;
        int maxZ = Math.max(start.getBlockZ(), end.getBlockZ()) - 2;
        int y = Math.min(start.getBlockY(), end.getBlockY());

        int x = minX + random.nextInt(maxX - minX + 1);
        int z = minZ + random.nextInt(maxZ - minZ + 1);

        return new Location(world, x, y, z);
    }

    public List<Location> getMobSpawnPoints(UUID playerId) {
        return mobSpawnPoints.getOrDefault(playerId, new ArrayList<>());
    }

    public DungeonInstance createDungeonInstance(Dungeon dungeon, Player player) {
        Location spawnPoint = findSuitableLocation(player.getWorld(), player.getLocation(), player.getLocation());
        if (spawnPoint == null) return null;

        DungeonInstance instance = new DungeonInstance(dungeon, player.getUniqueId(), spawnPoint, player.getLocation());
        generateDungeonContent(instance);
        return instance;
    }
} 