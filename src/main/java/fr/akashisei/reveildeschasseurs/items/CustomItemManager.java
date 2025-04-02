package fr.akashisei.reveildeschasseurs.items;

import fr.akashisei.reveildeschasseurs.ReveilDesChasseurs;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.Random;

public class CustomItemManager {
    private final ReveilDesChasseurs plugin;
    private final File itemsFile;
    private final FileConfiguration itemsConfig;
    private final Map<String, ItemStack> customItems;

    public CustomItemManager(ReveilDesChasseurs plugin) {
        this.plugin = plugin;
        this.itemsFile = new File(plugin.getDataFolder(), "items.yml");
        this.itemsConfig = YamlConfiguration.loadConfiguration(itemsFile);
        this.customItems = new HashMap<>();
        loadCustomItems();
    }

    public void loadCustomItems() {
        customItems.clear();
        if (!itemsFile.exists()) {
            plugin.saveResource("items.yml", false);
        }

        for (String id : itemsConfig.getKeys(false)) {
            String name = itemsConfig.getString(id + ".name", "Item inconnu");
            String description = itemsConfig.getString(id + ".description", "Description manquante");
            String material = itemsConfig.getString(id + ".material", "STONE");
            int amount = itemsConfig.getInt(id + ".amount", 1);
            boolean glowing = itemsConfig.getBoolean(id + ".glowing", false);
            List<String> lore = itemsConfig.getStringList(id + ".lore");
            List<String> enchantments = itemsConfig.getStringList(id + ".enchantments");
            List<String> flags = itemsConfig.getStringList(id + ".flags");

            ItemStack item = createCustomItem(id, name, description, material, amount, glowing, lore, enchantments, flags);
            customItems.put(id, item);
        }
    }

    public void saveCustomItems() {
        for (Map.Entry<String, ItemStack> entry : customItems.entrySet()) {
            String id = entry.getKey();
            ItemStack item = entry.getValue();
            ItemMeta meta = item.getItemMeta();
            if (meta == null) continue;

            itemsConfig.set(id + ".name", meta.getDisplayName());
            itemsConfig.set(id + ".description", meta.getLore() != null && !meta.getLore().isEmpty() ? meta.getLore().get(0) : "");
            itemsConfig.set(id + ".material", item.getType().name());
            itemsConfig.set(id + ".amount", item.getAmount());
            itemsConfig.set(id + ".glowing", meta.hasEnchants());
            itemsConfig.set(id + ".lore", meta.getLore());
            itemsConfig.set(id + ".enchantments", getEnchantmentStrings(item));
            itemsConfig.set(id + ".flags", getFlagStrings(meta));
        }

        try {
            itemsConfig.save(itemsFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Impossible de sauvegarder les items personnalisés : " + e.getMessage());
        }
    }

    private ItemStack createCustomItem(String id, String name, String description, String material, int amount, boolean glowing, List<String> lore, List<String> enchantments, List<String> flags) {
        Material mat;
        try {
            mat = Material.valueOf(material.toUpperCase());
        } catch (IllegalArgumentException e) {
            mat = Material.STONE;
        }

        ItemStack item = new ItemStack(mat, amount);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;

        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        
        List<String> finalLore = new ArrayList<>();
        if (description != null && !description.isEmpty()) {
            finalLore.add(ChatColor.GRAY + description);
        }
        for (String line : lore) {
            finalLore.add(ChatColor.translateAlternateColorCodes('&', line));
        }
        meta.setLore(finalLore);

        // Appliquer les enchantements
        for (String enchant : enchantments) {
            String[] parts = enchant.split(":");
            if (parts.length == 2) {
                try {
                    org.bukkit.enchantments.Enchantment ench = org.bukkit.enchantments.Enchantment.getByName(parts[0].toUpperCase());
                    int level = Integer.parseInt(parts[1]);
                    if (ench != null) {
                        meta.addEnchant(ench, level, true);
                    }
                } catch (NumberFormatException e) {
                    plugin.getLogger().warning("Niveau d'enchantement invalide pour " + enchant);
                }
            }
        }

        // Appliquer les flags
        for (String flag : flags) {
            try {
                meta.addItemFlags(ItemFlag.valueOf(flag.toUpperCase()));
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Flag d'item invalide : " + flag);
            }
        }

        item.setItemMeta(meta);
        return item;
    }

    private List<String> getEnchantmentStrings(ItemStack item) {
        List<String> enchantments = new ArrayList<>();
        for (org.bukkit.enchantments.Enchantment ench : item.getEnchantments().keySet()) {
            enchantments.add(ench.getName() + ":" + item.getEnchantmentLevel(ench));
        }
        return enchantments;
    }

    private List<String> getFlagStrings(ItemMeta meta) {
        List<String> flags = new ArrayList<>();
        for (ItemFlag flag : meta.getItemFlags()) {
            flags.add(flag.name());
        }
        return flags;
    }

    public ItemStack getCustomItem(String id) {
        ItemStack item = customItems.get(id);
        return item != null ? item.clone() : null;
    }

    public boolean isCustomItem(ItemStack item) {
        if (item == null || item.getType().isAir()) {
            return false;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return false;

        String name = meta.getDisplayName();
        for (ItemStack customItem : customItems.values()) {
            if (customItem.getItemMeta() != null && customItem.getItemMeta().getDisplayName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public String getCustomItemId(ItemStack item) {
        if (item == null || item.getType().isAir()) {
            return null;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return null;

        String name = meta.getDisplayName();
        for (Map.Entry<String, ItemStack> entry : customItems.entrySet()) {
            if (entry.getValue().getItemMeta() != null && entry.getValue().getItemMeta().getDisplayName().equals(name)) {
                return entry.getKey();
            }
        }
        return null;
    }

    public void addCustomItem(String id, ItemStack item) {
        customItems.put(id, item.clone());
        saveCustomItems();
    }

    public void removeCustomItem(String id) {
        customItems.remove(id);
        saveCustomItems();
    }

    public Set<String> getCustomItemIds() {
        return new HashSet<>(customItems.keySet());
    }

    public List<ItemStack> generateRandomLoot(List<String> possibleItems) {
        List<ItemStack> loot = new ArrayList<>();
        Random random = new Random();
        
        for (String itemId : possibleItems) {
            if (random.nextDouble() < 0.3) { // 30% de chance d'obtenir chaque item
                ItemStack item = getCustomItem(itemId);
                if (item != null) {
                    ItemStack clone = item.clone();
                    // Quantité aléatoire entre 1 et 3
                    clone.setAmount(random.nextInt(3) + 1);
                    loot.add(clone);
                }
            }
        }
        
        return loot;
    }
} 