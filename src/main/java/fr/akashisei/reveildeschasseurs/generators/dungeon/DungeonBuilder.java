package fr.akashisei.reveildeschasseurs.generators.dungeon;

import fr.akashisei.reveildeschasseurs.ReveilDesChasseurs;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Rotatable;
import org.bukkit.block.data.type.Door;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.block.data.type.TrapDoor;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class DungeonBuilder {
    private final ReveilDesChasseurs plugin;
    private final File templatesFolder;
    private final Map<String, FileConfiguration> templateConfigs;
    private final Random random;

    public DungeonBuilder(ReveilDesChasseurs plugin) {
        this.plugin = plugin;
        this.templatesFolder = new File(plugin.getDataFolder(), "templates");
        this.templateConfigs = new HashMap<>();
        this.random = new Random();
        loadTemplateConfigs();
    }

    private void loadTemplateConfigs() {
        if (!templatesFolder.exists()) {
            templatesFolder.mkdirs();
        }

        File[] files = templatesFolder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files != null) {
            for (File file : files) {
                String name = file.getName().replace(".yml", "");
                FileConfiguration config = YamlConfiguration.loadConfiguration(file);
                templateConfigs.put(name, config);
            }
        }
    }

    public void buildDungeon(String templateName, Location location, Player player) {
        FileConfiguration config = templateConfigs.get(templateName);
        if (config == null) {
            player.sendMessage("§cTemplate de donjon non trouvé : " + templateName);
            return;
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    buildStructure(config, location, player);
                } catch (Exception e) {
                    plugin.getLogger().severe("Erreur lors de la construction du donjon : " + e.getMessage());
                    player.sendMessage("§cUne erreur est survenue lors de la construction du donjon.");
                }
            }
        }.runTaskAsynchronously(plugin);
    }

    private void buildStructure(FileConfiguration config, Location location, Player player) {
        World world = location.getWorld();
        if (world == null) return;

        // Construire les blocs de base
        for (String key : config.getKeys(false)) {
            if (key.equals("metadata")) continue;

            String[] coords = key.split(",");
            if (coords.length != 3) continue;

            try {
                int x = Integer.parseInt(coords[0]);
                int y = Integer.parseInt(coords[1]);
                int z = Integer.parseInt(coords[2]);

                String blockData = config.getString(key);
                if (blockData == null) continue;

                Location blockLoc = location.clone().add(x, y, z);
                Block block = blockLoc.getBlock();
                BlockData data = org.bukkit.Bukkit.createBlockData(blockData);

                // Gérer les blocs spéciaux
                if (data instanceof Directional) {
                    handleDirectionalBlock(block, data, config, key);
                } else if (data instanceof Rotatable) {
                    handleRotatableBlock(block, data, config, key);
                } else {
                    block.setBlockData(data);
                }
            } catch (NumberFormatException e) {
                plugin.getLogger().warning("Coordonnées invalides dans le template : " + key);
            }
        }

        // Construire les entités
        if (config.contains("metadata.entities")) {
            for (String entityKey : config.getConfigurationSection("metadata.entities").getKeys(false)) {
                String path = "metadata.entities." + entityKey;
                String type = config.getString(path + ".type");
                if (type == null) continue;

                Location entityLoc = location.clone().add(
                    config.getDouble(path + ".x", 0),
                    config.getDouble(path + ".y", 0),
                    config.getDouble(path + ".z", 0)
                );

                spawnEntity(type, entityLoc, config, path);
            }
        }

        player.sendMessage("§aDonjon construit avec succès !");
    }

    private void handleDirectionalBlock(Block block, BlockData data, FileConfiguration config, String key) {
        if (data instanceof Door) {
            Door door = (Door) data;
            door.setFacing(getBlockFace(config, key + ".facing"));
            door.setHalf(getDoorHalf(config, key + ".half"));
            door.setHinge(getDoorHinge(config, key + ".hinge"));
            block.setBlockData(door);
        } else if (data instanceof Stairs) {
            Stairs stairs = (Stairs) data;
            stairs.setFacing(getBlockFace(config, key + ".facing"));
            stairs.setHalf(getStairsHalf(config, key + ".half"));
            stairs.setShape(getStairsShape(config, key + ".shape"));
            block.setBlockData(stairs);
        } else if (data instanceof TrapDoor) {
            TrapDoor trapDoor = (TrapDoor) data;
            trapDoor.setFacing(getBlockFace(config, key + ".facing"));
            trapDoor.setHalf(getTrapDoorHalf(config, key + ".half"));
            block.setBlockData(trapDoor);
        } else if (data instanceof WallSign) {
            WallSign sign = (WallSign) data;
            sign.setFacing(getBlockFace(config, key + ".facing"));
            block.setBlockData(sign);
        } else {
            Directional directional = (Directional) data;
            directional.setFacing(getBlockFace(config, key + ".facing"));
            block.setBlockData(directional);
        }
    }

    private void handleRotatableBlock(Block block, BlockData data, FileConfiguration config, String key) {
        Rotatable rotatable = (Rotatable) data;
        String rotationStr = config.getString(key + ".rotation", "NONE");
        try {
            BlockFace face = BlockFace.valueOf(rotationStr.toUpperCase());
            rotatable.setRotation(face);
        } catch (IllegalArgumentException e) {
            rotatable.setRotation(BlockFace.NORTH);
        }
        block.setBlockData(rotatable);
    }

    private BlockFace getBlockFace(FileConfiguration config, String path) {
        String facing = config.getString(path, "NORTH");
        try {
            return BlockFace.valueOf(facing.toUpperCase());
        } catch (IllegalArgumentException e) {
            return BlockFace.NORTH;
        }
    }

    private Door.Half getDoorHalf(FileConfiguration config, String path) {
        String half = config.getString(path, "BOTTOM");
        try {
            return Door.Half.valueOf(half.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Door.Half.BOTTOM;
        }
    }

    private Door.Hinge getDoorHinge(FileConfiguration config, String path) {
        String hinge = config.getString(path, "LEFT");
        try {
            return Door.Hinge.valueOf(hinge.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Door.Hinge.LEFT;
        }
    }

    private Stairs.Half getStairsHalf(FileConfiguration config, String path) {
        String half = config.getString(path, "BOTTOM");
        try {
            return Stairs.Half.valueOf(half.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Stairs.Half.BOTTOM;
        }
    }

    private Stairs.Shape getStairsShape(FileConfiguration config, String path) {
        String shape = config.getString(path, "STRAIGHT");
        try {
            return Stairs.Shape.valueOf(shape.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Stairs.Shape.STRAIGHT;
        }
    }

    private TrapDoor.Half getTrapDoorHalf(FileConfiguration config, String path) {
        String half = config.getString(path, "BOTTOM");
        try {
            return TrapDoor.Half.valueOf(half.toUpperCase());
        } catch (IllegalArgumentException e) {
            return TrapDoor.Half.BOTTOM;
        }
    }

    private void spawnEntity(String type, Location location, FileConfiguration config, String path) {
        // Utilisation du random pour la génération aléatoire
        if (random.nextDouble() < config.getDouble(path + ".chance", 1.0)) {
            // TODO: Implémenter la génération d'entités
        }
    }

    public void saveTemplate(String name, Location pos1, Location pos2) {
        File file = new File(templatesFolder, name + ".yml");
        FileConfiguration config = new YamlConfiguration();

        int minX = Math.min(pos1.getBlockX(), pos2.getBlockX());
        int minY = Math.min(pos1.getBlockY(), pos2.getBlockY());
        int minZ = Math.min(pos1.getBlockZ(), pos2.getBlockZ());
        int maxX = Math.max(pos1.getBlockX(), pos2.getBlockX());
        int maxY = Math.max(pos1.getBlockY(), pos2.getBlockY());
        int maxZ = Math.max(pos1.getBlockZ(), pos2.getBlockZ());

        World world = pos1.getWorld();
        if (world == null) return;

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Block block = world.getBlockAt(x, y, z);
                    String key = (x - minX) + "," + (y - minY) + "," + (z - minZ);
                    config.set(key, block.getBlockData().getAsString());

                    // Sauvegarder les propriétés supplémentaires pour les blocs spéciaux
                    if (block.getBlockData() instanceof Directional) {
                        Directional directional = (Directional) block.getBlockData();
                        config.set(key + ".facing", directional.getFacing().name());
                    }
                    if (block.getBlockData() instanceof Rotatable) {
                        Rotatable rotatable = (Rotatable) block.getBlockData();
                        config.set(key + ".rotation", rotatable.getRotation().name());
                    }
                }
            }
        }

        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Impossible de sauvegarder le template : " + e.getMessage());
        }
    }
} 