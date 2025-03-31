package fr.akashisei.reveildeschasseurs.generators;

import fr.akashisei.reveildeschasseurs.ReveilDesChasseurs;
import fr.akashisei.reveildeschasseurs.dungeon.DungeonInstance;
import fr.akashisei.reveildeschasseurs.models.Dungeon;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.*;

public class DungeonBuilder {
    private final ReveilDesChasseurs plugin;
    private final Random random;
    private final Map<Location, String> roomLocations;
    private final Map<Location, String> trapLocations;
    private final Map<Location, String> spawnerLocations;

    public DungeonBuilder(ReveilDesChasseurs plugin) {
        this.plugin = plugin;
        this.random = new Random();
        this.roomLocations = new HashMap<>();
        this.trapLocations = new HashMap<>();
        this.spawnerLocations = new HashMap<>();
    }

    public DungeonInstance generateDungeon(String dungeonId, World world, Location center, Player player) {
        // Récupérer le donjon
        Dungeon dungeon = plugin.getDungeonManager().getDungeon(dungeonId);
        if (dungeon == null) return null;

        // Créer une nouvelle instance de donjon
        Location spawnLoc = center.clone().add(0, 2, 0);
        DungeonInstance instance = new DungeonInstance(dungeon, player, spawnLoc);

        // Générer la structure du donjon
        generateStructure(world, center);
        
        // Générer les pièges
        generateTraps(world);
        
        // Générer les spawners de mobs
        generateSpawners(world);
        
        // Générer les coffres et récompenses
        generateLoot(world);

        return instance;
    }

    private void generateStructure(World world, Location center) {
        // Générer la salle principale
        generateMainRoom(world, center);
        
        // Générer les couloirs
        generateCorridors(world, center);
        
        // Générer les salles secondaires
        generateSideRooms(world, center);
        
        // Générer la salle du boss
        generateBossRoom(world, center);
    }

    private void generateMainRoom(World world, Location center) {
        // Dimensions de la salle principale
        int width = 15;
        int length = 15;
        int height = 5;

        // Générer les murs
        for (int x = -width/2; x <= width/2; x++) {
            for (int y = 0; y < height; y++) {
                for (int z = -length/2; z <= length/2; z++) {
                    Location loc = center.clone().add(x, y, z);
                    
                    // Murs extérieurs
                    if (Math.abs(x) == width/2 || Math.abs(z) == length/2) {
                        loc.getBlock().setType(Material.STONE_BRICKS);
                    }
                    // Sol
                    else if (y == 0) {
                        loc.getBlock().setType(Material.STONE_BRICKS);
                    }
                    // Plafond
                    else if (y == height - 1) {
                        loc.getBlock().setType(Material.STONE_BRICKS);
                    }
                    // Intérieur
                    else {
                        loc.getBlock().setType(Material.AIR);
                    }
                }
            }
        }

        // Ajouter des décorations
        addDecorations(world, center, width, length, height);
    }

    private void generateCorridors(World world, Location center) {
        // Générer 4 couloirs principaux
        int corridorLength = 10;
        int corridorWidth = 3;
        int corridorHeight = 3;

        // Couloir Nord
        generateCorridor(world, center.clone().add(0, 0, -corridorLength/2), 
            corridorLength, corridorWidth, corridorHeight, BlockFace.NORTH);
        
        // Couloir Sud
        generateCorridor(world, center.clone().add(0, 0, corridorLength/2), 
            corridorLength, corridorWidth, corridorHeight, BlockFace.SOUTH);
        
        // Couloir Est
        generateCorridor(world, center.clone().add(corridorLength/2, 0, 0), 
            corridorLength, corridorWidth, corridorHeight, BlockFace.EAST);
        
        // Couloir Ouest
        generateCorridor(world, center.clone().add(-corridorLength/2, 0, 0), 
            corridorLength, corridorWidth, corridorHeight, BlockFace.WEST);
    }

    private void generateCorridor(World world, Location start, int length, int width, int height, BlockFace direction) {
        Vector dir = direction.getDirection();
        Vector right = new Vector(-dir.getZ(), 0, dir.getX());
        
        for (int i = 0; i < length; i++) {
            Location current = start.clone().add(dir.clone().multiply(i));
            
            // Générer le couloir
            for (int w = -width/2; w <= width/2; w++) {
                for (int h = 0; h < height; h++) {
                    Location loc = current.clone().add(right.clone().multiply(w)).add(0, h, 0);
                    
                    // Murs
                    if (Math.abs(w) == width/2 || h == 0 || h == height - 1) {
                        loc.getBlock().setType(Material.STONE_BRICKS);
                    }
                    // Intérieur
                    else {
                        loc.getBlock().setType(Material.AIR);
                    }
                }
            }
        }
    }

    private void generateSideRooms(World world, Location center) {
        // Générer 4 salles secondaires aux extrémités des couloirs
        int roomSize = 7;
        int roomHeight = 4;
        
        // Salle Nord
        generateRoom(world, center.clone().add(0, 0, -15), roomSize, roomHeight);
        
        // Salle Sud
        generateRoom(world, center.clone().add(0, 0, 15), roomSize, roomHeight);
        
        // Salle Est
        generateRoom(world, center.clone().add(15, 0, 0), roomSize, roomHeight);
        
        // Salle Ouest
        generateRoom(world, center.clone().add(-15, 0, 0), roomSize, roomHeight);
    }

    private void generateRoom(World world, Location center, int size, int height) {
        for (int x = -size/2; x <= size/2; x++) {
            for (int y = 0; y < height; y++) {
                for (int z = -size/2; z <= size/2; z++) {
                    Location loc = center.clone().add(x, y, z);
                    
                    // Murs
                    if (Math.abs(x) == size/2 || Math.abs(z) == size/2 || y == 0 || y == height - 1) {
                        loc.getBlock().setType(Material.STONE_BRICKS);
                    }
                    // Intérieur
                    else {
                        loc.getBlock().setType(Material.AIR);
                    }
                }
            }
        }
    }

    private void generateBossRoom(World world, Location center) {
        // Générer la salle du boss à l'extrémité d'un couloir
        int bossRoomSize = 12;
        int bossRoomHeight = 6;
        
        // Choisir une direction aléatoire pour la salle du boss
        BlockFace[] directions = {BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST};
        BlockFace bossDirection = directions[random.nextInt(directions.length)];
        Location bossRoomCenter = center.clone().add(
            bossDirection.getModX() * 25,
            0,
            bossDirection.getModZ() * 25
        );
        
        // Générer la salle
        generateRoom(world, bossRoomCenter, bossRoomSize, bossRoomHeight);
        
        // Ajouter un trône ou un autel pour le boss
        Location throneLoc = bossRoomCenter.clone().add(0, 1, 0);
        throneLoc.getBlock().setType(Material.NETHER_BRICKS);
    }

    private void generateTraps(World world) {
        // Générer des pièges dans les salles
        for (Map.Entry<Location, String> entry : roomLocations.entrySet()) {
            Location roomLoc = entry.getKey();
            
            // Piège de lave (20% de chance)
            if (random.nextDouble() < 0.2) {
                Location trapLoc = roomLoc.clone().add(random.nextInt(3) - 1, 0, random.nextInt(3) - 1);
                trapLocations.put(trapLoc, "LAVA");
            }
            
            // Piège à flèches (15% de chance)
            if (random.nextDouble() < 0.15) {
                Location trapLoc = roomLoc.clone().add(random.nextInt(3) - 1, 2, random.nextInt(3) - 1);
                trapLocations.put(trapLoc, "ARROW");
            }
            
            // Piège de poison (10% de chance)
            if (random.nextDouble() < 0.1) {
                Location trapLoc = roomLoc.clone().add(random.nextInt(3) - 1, 0, random.nextInt(3) - 1);
                trapLocations.put(trapLoc, "POISON");
            }
            
            // Piège explosif (5% de chance)
            if (random.nextDouble() < 0.05) {
                Location trapLoc = roomLoc.clone().add(random.nextInt(3) - 1, 0, random.nextInt(3) - 1);
                trapLocations.put(trapLoc, "TNT");
            }
        }

        // Placer les pièges
        generateLavaTraps(world);
        generateArrowTraps(world);
        generatePoisonTraps(world);
        generateExplosionTraps(world);
    }

    private void generateLavaTraps(World world) {
        for (Map.Entry<Location, String> entry : trapLocations.entrySet()) {
            if (entry.getValue().equals("LAVA")) {
                Location loc = entry.getKey();
                loc.getBlock().setType(Material.LAVA);
            }
        }
    }

    private void generateArrowTraps(World world) {
        for (Map.Entry<Location, String> entry : trapLocations.entrySet()) {
            if (entry.getValue().equals("ARROW")) {
                Location loc = entry.getKey();
                loc.getBlock().setType(Material.DISPENSER);
                // TODO: Configurer le dispenser avec des flèches
            }
        }
    }

    private void generatePoisonTraps(World world) {
        for (Map.Entry<Location, String> entry : trapLocations.entrySet()) {
            if (entry.getValue().equals("POISON")) {
                Location loc = entry.getKey();
                loc.getBlock().setType(Material.SOUL_SAND);
                // TODO: Ajouter un effet de poison
            }
        }
    }

    private void generateExplosionTraps(World world) {
        for (Map.Entry<Location, String> entry : trapLocations.entrySet()) {
            if (entry.getValue().equals("TNT")) {
                Location loc = entry.getKey();
                loc.getBlock().setType(Material.TNT);
            }
        }
    }

    private void generateSpawners(World world) {
        // Générer des spawners dans les salles
        for (Map.Entry<Location, String> entry : roomLocations.entrySet()) {
            Location roomLoc = entry.getKey();
            
            // 30% de chance d'avoir un spawner dans une salle
            if (random.nextDouble() < 0.3) {
                Location spawnerLoc = roomLoc.clone().add(random.nextInt(3) - 1, 1, random.nextInt(3) - 1);
                String mobType = getRandomMobType();
                spawnerLocations.put(spawnerLoc, mobType);
                
                // Placer le spawner
                spawnerLoc.getBlock().setType(Material.SPAWNER);
                // TODO: Configurer le spawner avec le type de mob
            }
        }
    }

    private String getRandomMobType() {
        String[] mobTypes = {
            "ZOMBIE", "SKELETON", "SPIDER", "CAVE_SPIDER", "CREEPER",
            "WITCH", "VINDICATOR", "PILLAGER", "BLAZE", "WITHER_SKELETON"
        };
        return mobTypes[random.nextInt(mobTypes.length)];
    }

    private void generateLoot(World world) {
        // Générer des coffres dans les salles
        for (Map.Entry<Location, String> entry : roomLocations.entrySet()) {
            Location roomLoc = entry.getKey();
            
            // 40% de chance d'avoir un coffre dans une salle
            if (random.nextDouble() < 0.4) {
                Location chestLoc = roomLoc.clone().add(random.nextInt(3) - 1, 1, random.nextInt(3) - 1);
                chestLoc.getBlock().setType(Material.CHEST);
                // TODO: Remplir le coffre avec du butin
            }
        }
    }

    private void addDecorations(World world, Location center, int width, int length, int height) {
        // Ajouter des torches
        for (int x = -width/2; x <= width/2; x += 3) {
            for (int z = -length/2; z <= length/2; z += 3) {
                if (Math.abs(x) == width/2 || Math.abs(z) == length/2) {
                    Location torchLoc = center.clone().add(x, height - 1, z);
                    torchLoc.getBlock().setType(Material.WALL_TORCH);
                }
            }
        }

        // Ajouter des colonnes
        for (int x = -width/3; x <= width/3; x += width/3) {
            for (int z = -length/3; z <= length/3; z += length/3) {
                if (x != 0 || z != 0) {
                    Location pillarLoc = center.clone().add(x, 0, z);
                    for (int y = 0; y < height; y++) {
                        pillarLoc.clone().add(0, y, 0).getBlock().setType(Material.STONE_BRICKS);
                    }
                }
            }
        }
    }

    public Map<Location, String> getRoomLocations() {
        return roomLocations;
    }

    public Map<Location, String> getTrapLocations() {
        return trapLocations;
    }

    public Map<Location, String> getSpawnerLocations() {
        return spawnerLocations;
    }
} 