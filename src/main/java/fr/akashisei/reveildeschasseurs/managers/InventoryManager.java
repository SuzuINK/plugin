package fr.akashisei.reveildeschasseurs.managers;

import fr.akashisei.reveildeschasseurs.ReveilDesChasseurs;
import fr.akashisei.reveildeschasseurs.models.InventoryData;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class InventoryManager {
    private final ReveilDesChasseurs plugin;
    private final Map<UUID, InventoryData> inventoryDataMap;
    private final File inventoriesFile;
    private final FileConfiguration inventoriesConfig;

    public InventoryManager(ReveilDesChasseurs plugin) {
        this.plugin = plugin;
        this.inventoryDataMap = new HashMap<>();
        this.inventoriesFile = new File(plugin.getDataFolder(), "inventories.yml");
        this.inventoriesConfig = YamlConfiguration.loadConfiguration(inventoriesFile);
        loadInventories();
    }

    public void loadInventories() {
        inventoryDataMap.clear();
        if (!inventoriesFile.exists()) {
            plugin.saveResource("inventories.yml", false);
        }

        for (String uuidStr : inventoriesConfig.getKeys(false)) {
            try {
                UUID playerId = UUID.fromString(uuidStr);
                InventoryData data = (InventoryData) inventoriesConfig.get(uuidStr);
                if (data != null) {
                    inventoryDataMap.put(playerId, data);
                }
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("UUID invalide dans les inventaires : " + uuidStr);
            }
        }
    }

    public void saveInventories() {
        for (Map.Entry<UUID, InventoryData> entry : inventoryDataMap.entrySet()) {
            inventoriesConfig.set(entry.getKey().toString(), entry.getValue());
        }
        
        try {
            inventoriesConfig.save(inventoriesFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Impossible de sauvegarder les inventaires : " + e.getMessage());
        }
    }

    public void savePlayerInventory(Player player) {
        InventoryData data = getOrCreateInventoryData(player);
        data.saveInventory(player.getInventory());
        inventoryDataMap.put(player.getUniqueId(), data);
        saveInventories();
    }

    public void loadPlayerInventory(Player player) {
        InventoryData data = getInventoryData(player);
        if (data != null) {
            data.loadInventory(player.getInventory());
        }
    }

    public InventoryData getInventoryData(Player player) {
        return inventoryDataMap.get(player.getUniqueId());
    }

    public InventoryData getOrCreateInventoryData(Player player) {
        InventoryData data = getInventoryData(player);
        if (data == null) {
            data = new InventoryData(player.getUniqueId());
            inventoryDataMap.put(player.getUniqueId(), data);
        }
        return data;
    }

    public void clearPlayerInventory(Player player) {
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.getInventory().setItemInOffHand(null);
        savePlayerInventory(player);
    }

    public void backupPlayerInventory(Player player) {
        savePlayerInventory(player);
    }

    public void restorePlayerInventory(Player player) {
        loadPlayerInventory(player);
    }
} 