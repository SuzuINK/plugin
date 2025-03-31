package fr.akashisei.reveildeschasseurs.managers;

import fr.akashisei.reveildeschasseurs.ReveilDesChasseurs;
import fr.akashisei.reveildeschasseurs.models.CustomItem;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.NamespacedKey;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class CustomItemManager {
    private final ReveilDesChasseurs plugin;
    private final Map<String, CustomItem> customItems;
    private final File itemsFile;
    private final FileConfiguration itemsConfig;

    public CustomItemManager(ReveilDesChasseurs plugin) {
        this.plugin = plugin;
        this.customItems = new HashMap<>();
        this.itemsFile = new File(plugin.getDataFolder(), "items.yml");
        this.itemsConfig = YamlConfiguration.loadConfiguration(itemsFile);
        loadCustomItems();
    }

    public void loadCustomItems() {
        customItems.clear();
        ConfigurationSection itemsSection = itemsConfig.getConfigurationSection("items");
        if (itemsSection == null) {
            itemsSection = itemsConfig.createSection("items");
        }

        for (String itemId : itemsSection.getKeys(false)) {
            ConfigurationSection itemSection = itemsSection.getConfigurationSection(itemId);
            if (itemSection == null) continue;

            CustomItem customItem = new CustomItem(itemId);
            customItem.setName(itemSection.getString("name", "Item"));
            customItem.setMaterial(Material.valueOf(itemSection.getString("material", "STONE")));
            customItem.setLore(itemSection.getStringList("lore"));
            customItem.setAmount(itemSection.getInt("amount", 1));
            customItem.setGlowing(itemSection.getBoolean("glowing", false));
            customItem.setDropRate(itemSection.getDouble("dropRate", 1.0));
            customItem.setMinAmount(itemSection.getInt("minAmount", 1));
            customItem.setMaxAmount(itemSection.getInt("maxAmount", 1));
            customItem.setEnchantments(itemSection.getStringList("enchantments"));
            customItem.setPotionEffects(itemSection.getStringList("potionEffects"));
            customItem.setAttributes(itemSection.getStringList("attributes"));

            customItems.put(itemId, customItem);
        }
    }

    public void saveCustomItems() {
        itemsConfig.set("items", null);
        for (Map.Entry<String, CustomItem> entry : customItems.entrySet()) {
            String itemId = entry.getKey();
            CustomItem customItem = entry.getValue();

            itemsConfig.set("items." + itemId + ".name", customItem.getName());
            itemsConfig.set("items." + itemId + ".material", customItem.getMaterial().name());
            itemsConfig.set("items." + itemId + ".lore", customItem.getLore());
            itemsConfig.set("items." + itemId + ".amount", customItem.getAmount());
            itemsConfig.set("items." + itemId + ".glowing", customItem.isGlowing());
            itemsConfig.set("items." + itemId + ".dropRate", customItem.getDropRate());
            itemsConfig.set("items." + itemId + ".minAmount", customItem.getMinAmount());
            itemsConfig.set("items." + itemId + ".maxAmount", customItem.getMaxAmount());
            itemsConfig.set("items." + itemId + ".enchantments", customItem.getEnchantments());
            itemsConfig.set("items." + itemId + ".potionEffects", customItem.getPotionEffects());
            itemsConfig.set("items." + itemId + ".attributes", customItem.getAttributes());
        }

        try {
            itemsConfig.save(itemsFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Impossible de sauvegarder les items personnalisés: " + e.getMessage());
        }
    }

    public CustomItem getCustomItem(String id) {
        return customItems.get(id);
    }

    public List<CustomItem> getAllCustomItems() {
        return new ArrayList<>(customItems.values());
    }

    public boolean createCustomItem(CustomItem customItem) {
        if (customItems.containsKey(customItem.getId())) {
            return false;
        }
        customItems.put(customItem.getId(), customItem);
        saveCustomItems();
        return true;
    }

    public boolean deleteCustomItem(String id) {
        if (!customItems.containsKey(id)) {
            return false;
        }
        customItems.remove(id);
        saveCustomItems();
        return true;
    }

    public boolean isCustomItem(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        
        PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
        return container.has(
            new NamespacedKey("reveildeschasseurs", "custom_item_id"),
            PersistentDataType.STRING
        );
    }

    public String getCustomItemId(ItemStack item) {
        if (!isCustomItem(item)) return null;
        
        PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
        return container.get(
            new NamespacedKey("reveildeschasseurs", "custom_item_id"),
            PersistentDataType.STRING
        );
    }

    public List<ItemStack> generateRandomLoot(List<String> itemIds) {
        List<ItemStack> loot = new ArrayList<>();
        for (String itemId : itemIds) {
            CustomItem customItem = getCustomItem(itemId);
            if (customItem != null && Math.random() <= customItem.getDropRate()) {
                loot.add(customItem.generateRandomAmount());
            }
        }
        return loot;
    }
} 