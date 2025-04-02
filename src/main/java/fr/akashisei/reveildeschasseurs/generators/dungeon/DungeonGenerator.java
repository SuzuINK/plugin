package fr.akashisei.reveildeschasseurs.generators.dungeon;

import fr.akashisei.reveildeschasseurs.ReveilDesChasseurs;
import fr.akashisei.reveildeschasseurs.models.DungeonTheme;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Rotatable;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class DungeonGenerator {
    private final ReveilDesChasseurs plugin;
    private final File schematicsFolder;
    private final Map<String, FileConfiguration> schematicConfigs;

    public DungeonGenerator(ReveilDesChasseurs plugin) {
        this.plugin = plugin;
        this.schematicsFolder = new File(plugin.getDataFolder(), "schematics");
        this.schematicConfigs = new HashMap<>();
        loadSchematicConfigs();
    }

    private void loadSchematicConfigs() {
        if (!schematicsFolder.exists()) {
            schematicsFolder.mkdirs();
        }

        File[] files = schematicsFolder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files != null) {
            for (File file : files) {
                String name = file.getName().replace(".yml", "");
                FileConfiguration config = YamlConfiguration.loadConfiguration(file);
                schematicConfigs.put(name, config);
            }
        }
    }

    public void generateDungeon(String schematicName, Location location, Player player) {
        FileConfiguration config = schematicConfigs.get(schematicName);
        if (config == null) {
            player.sendMessage("§cSchéma non trouvé : " + schematicName);
            return;
        }

        // Générer la structure
        for (String key : config.getKeys(false)) {
            String[] parts = key.split(",");
            if (parts.length != 3) continue;

            int x = Integer.parseInt(parts[0]);
            int y = Integer.parseInt(parts[1]);
            int z = Integer.parseInt(parts[2]);

            Location blockLoc = location.clone().add(x, y, z);
            String blockType = config.getString(key + ".type");
            String blockData = config.getString(key + ".data", "");
            String rotation = config.getString(key + ".rotation", "");
            String facing = config.getString(key + ".facing", "");

            // Placer le bloc
            Block block = blockLoc.getBlock();
            block.setType(Material.valueOf(blockType));

            // Appliquer les données du bloc
            if (!blockData.isEmpty()) {
                applyBlockData(block, blockData);
            }

            // Appliquer la rotation
            if (!rotation.isEmpty()) {
                applyRotation(block, rotation);
            }

            // Appliquer la direction
            if (!facing.isEmpty()) {
                applyFacing(block, facing);
            }

            // Spawner des entités
            if (config.contains(key + ".entities")) {
                spawnEntities(blockLoc, config.getStringList(key + ".entities"));
            }
        }

        player.sendMessage("§aDonjon généré avec succès !");
    }

    private void applyBlockData(Block block, String data) {
        // Implémentation de l'application des données de bloc
        // À compléter selon les besoins
    }

    private void applyRotation(Block block, String rotation) {
        if (block.getBlockData() instanceof Rotatable) {
            Rotatable rotatable = (Rotatable) block.getBlockData();
            rotatable.setRotation(BlockFace.valueOf(rotation));
        }
    }

    private void applyFacing(Block block, String facing) {
        if (block.getBlockData() instanceof Directional) {
            Directional directional = (Directional) block.getBlockData();
            directional.setFacing(BlockFace.valueOf(facing));
        }
    }

    private void spawnEntities(Location location, List<String> entities) {
        for (String entityData : entities) {
            String[] parts = entityData.split(":");
            if (parts.length == 2) {
                EntityType type = EntityType.valueOf(parts[0]);
                int amount = Integer.parseInt(parts[1]);
                for (int i = 0; i < amount; i++) {
                    Entity entity = location.getWorld().spawnEntity(location, type);
                    // Appliquer des modifications à l'entité si nécessaire
                }
            }
        }
    }

    public void saveSchematic(String name, Location pos1, Location pos2) {
        File schematicFile = new File(schematicsFolder, name + ".yml");
        FileConfiguration config = new YamlConfiguration();

        int minX = Math.min(pos1.getBlockX(), pos2.getBlockX());
        int maxX = Math.max(pos1.getBlockX(), pos2.getBlockX());
        int minY = Math.min(pos1.getBlockY(), pos2.getBlockY());
        int maxY = Math.max(pos1.getBlockY(), pos2.getBlockY());
        int minZ = Math.min(pos1.getBlockZ(), pos2.getBlockZ());
        int maxZ = Math.max(pos1.getBlockZ(), pos2.getBlockZ());

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Location blockLoc = new Location(pos1.getWorld(), x, y, z);
                    Block block = blockLoc.getBlock();
                    String key = (x - minX) + "," + (y - minY) + "," + (z - minZ);

                    config.set(key + ".type", block.getType().name());
                    if (block.getBlockData() instanceof Rotatable) {
                        config.set(key + ".rotation", ((Rotatable) block.getBlockData()).getRotation().name());
                    }
                    if (block.getBlockData() instanceof Directional) {
                        config.set(key + ".facing", ((Directional) block.getBlockData()).getFacing().name());
                    }
                }
            }
        }

        try {
            config.save(schematicFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Impossible de sauvegarder le schéma : " + e.getMessage());
        }
    }
} 