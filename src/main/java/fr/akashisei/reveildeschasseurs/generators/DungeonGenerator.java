package fr.akashisei.reveildeschasseurs.generators;

import fr.akashisei.reveildeschasseurs.ReveilDesChasseurs;
import fr.akashisei.reveildeschasseurs.dungeon.DungeonInstance;
import fr.akashisei.reveildeschasseurs.models.Dungeon;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.*;

public class DungeonGenerator {
    private final ReveilDesChasseurs plugin;
    private final Random random;

    public DungeonGenerator(ReveilDesChasseurs plugin) {
        this.plugin = plugin;
        this.random = new Random();
    }

    public void generateDungeonContent(DungeonInstance instance) {
        World world = instance.getWorld();
        Location spawnLoc = instance.getSpawnLocation();
        Location exitLoc = instance.getExitLocation();

        // Générer les murs et le sol
        generateWallsAndFloor(world, spawnLoc, exitLoc);

        // Générer les pièges
        generateTraps(world, spawnLoc, exitLoc);

        // Générer les coffres
        generateChests(world, spawnLoc, exitLoc);

        // Générer les mobs
        generateMobs(world, spawnLoc, exitLoc);

        // Générer les portails
        generatePortals(world, spawnLoc, exitLoc);
    }

    private void generateWallsAndFloor(World world, Location spawnLoc, Location exitLoc) {
        int minX = Math.min(spawnLoc.getBlockX(), exitLoc.getBlockX()) - 10;
        int maxX = Math.max(spawnLoc.getBlockX(), exitLoc.getBlockX()) + 10;
        int minZ = Math.min(spawnLoc.getBlockZ(), exitLoc.getBlockZ()) - 10;
        int maxZ = Math.max(spawnLoc.getBlockZ(), exitLoc.getBlockZ()) + 10;
        int minY = Math.min(spawnLoc.getBlockY(), exitLoc.getBlockY()) - 2;
        int maxY = Math.max(spawnLoc.getBlockY(), exitLoc.getBlockY()) + 5;

        // Générer le sol
        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                world.getBlockAt(x, minY, z).setType(Material.STONE_BRICKS);
            }
        }

        // Générer les murs
        for (int y = minY + 1; y <= maxY; y++) {
            for (int x = minX; x <= maxX; x++) {
                for (int z = minZ; z <= maxZ; z++) {
                    if (x == minX || x == maxX || z == minZ || z == maxZ) {
                        world.getBlockAt(x, y, z).setType(Material.STONE_BRICKS);
                    }
                }
            }
        }
    }

    private void generateTraps(World world, Location spawnLoc, Location exitLoc) {
        int trapCount = random.nextInt(5) + 3; // 3-7 pièges

        for (int i = 0; i < trapCount; i++) {
            int x = random.nextInt(Math.abs(exitLoc.getBlockX() - spawnLoc.getBlockX())) + Math.min(spawnLoc.getBlockX(), exitLoc.getBlockX());
            int z = random.nextInt(Math.abs(exitLoc.getBlockZ() - spawnLoc.getBlockZ())) + Math.min(spawnLoc.getBlockZ(), exitLoc.getBlockZ());
            int y = Math.min(spawnLoc.getBlockY(), exitLoc.getBlockY());

            Location trapLoc = new Location(world, x, y, z);
            Block block = world.getBlockAt(trapLoc);

            // Choisir un type de piège aléatoire
            switch (random.nextInt(5)) {
                case 0: // Lave
                    block.setType(Material.LAVA);
                    break;
                case 1: // TNT
                    block.setType(Material.TNT);
                    break;
                case 2: // Dispenser avec flèches
                    block.setType(Material.DISPENSER);
                    break;
                case 3: // Piston rétractable
                    block.setType(Material.PISTON);
                    break;
                case 4: // Portail de téléportation aléatoire
                    block.setType(Material.END_PORTAL_FRAME);
                    break;
            }
        }
    }

    private void generateChests(World world, Location spawnLoc, Location exitLoc) {
        int chestCount = random.nextInt(3) + 2; // 2-4 coffres

        for (int i = 0; i < chestCount; i++) {
            int x = random.nextInt(Math.abs(exitLoc.getBlockX() - spawnLoc.getBlockX())) + Math.min(spawnLoc.getBlockX(), exitLoc.getBlockX());
            int z = random.nextInt(Math.abs(exitLoc.getBlockZ() - spawnLoc.getBlockZ())) + Math.min(spawnLoc.getBlockZ(), exitLoc.getBlockZ());
            int y = Math.min(spawnLoc.getBlockY(), exitLoc.getBlockY());

            Location chestLoc = new Location(world, x, y, z);
            Block block = world.getBlockAt(chestLoc);
            block.setType(Material.CHEST);

            // Ajouter des items aléatoires dans le coffre
            if (block.getState() instanceof Chest) {
                Chest chest = (Chest) block.getState();
                int itemCount = random.nextInt(5) + 3; // 3-7 items

                for (int j = 0; j < itemCount; j++) {
                    ItemStack item = generateRandomLoot();
                    chest.getInventory().setItem(random.nextInt(27), item);
                }
            }
        }
    }

    private void generateMobs(World world, Location spawnLoc, Location exitLoc) {
        int mobCount = random.nextInt(5) + 5; // 5-10 mobs

        for (int i = 0; i < mobCount; i++) {
            int x = random.nextInt(Math.abs(exitLoc.getBlockX() - spawnLoc.getBlockX())) + Math.min(spawnLoc.getBlockX(), exitLoc.getBlockX());
            int z = random.nextInt(Math.abs(exitLoc.getBlockZ() - spawnLoc.getBlockZ())) + Math.min(spawnLoc.getBlockZ(), exitLoc.getBlockZ());
            int y = Math.min(spawnLoc.getBlockY(), exitLoc.getBlockY());

            Location mobLoc = new Location(world, x, y, z);
            EntityType[] mobTypes = {
                EntityType.ZOMBIE,
                EntityType.SKELETON,
                EntityType.SPIDER,
                EntityType.CREEPER,
                EntityType.ENDERMAN
            };

            world.spawnEntity(mobLoc, mobTypes[random.nextInt(mobTypes.length)]);
        }
    }

    private void generatePortals(World world, Location spawnLoc, Location exitLoc) {
        // Générer un portail d'entrée
        Location portalLoc = spawnLoc.clone().add(0, 1, 0);
        generatePortal(portalLoc);

        // Générer un portail de sortie
        portalLoc = exitLoc.clone().add(0, 1, 0);
        generatePortal(portalLoc);
    }

    private void generatePortal(Location location) {
        // Créer un cadre de portail 3x3
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                if (x == 0 && z == 0) continue;
                location.clone().add(x, 0, z).getBlock().setType(Material.OBSIDIAN);
            }
        }

        // Activer le portail
        location.getBlock().setType(Material.END_PORTAL);
    }

    private ItemStack generateRandomLoot() {
        Material[] possibleItems = {
            Material.DIAMOND,
            Material.IRON_INGOT,
            Material.GOLD_INGOT,
            Material.EMERALD,
            Material.ENCHANTED_GOLDEN_APPLE,
            Material.NETHERITE_INGOT,
            Material.DRAGON_EGG,
            Material.TOTEM_OF_UNDYING
        };

        Material itemType = possibleItems[random.nextInt(possibleItems.length)];
        ItemStack item = new ItemStack(itemType);
        item.setAmount(random.nextInt(3) + 1); // 1-3 items

        return item;
    }
} 