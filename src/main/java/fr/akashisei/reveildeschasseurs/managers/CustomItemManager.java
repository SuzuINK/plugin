package fr.akashisei.reveildeschasseurs.managers;

import fr.akashisei.reveildeschasseurs.ReveilDesChasseurs;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
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
    private final Map<String, ItemStack> customItems;
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
            Map<String, Integer> enchantments = new HashMap<>();
            List<String> flags = itemsConfig.getStringList(id + ".flags");

            // Charger les enchantements
            for (String enchantment : itemsConfig.getStringList(id + ".enchantments")) {
                String[] parts = enchantment.split(":");
                if (parts.length == 2) {
                    enchantments.put(parts[0], Integer.parseInt(parts[1]));
                }
            }

            ItemStack item = createCustomItem(name, description, material, amount, glowing, lore, enchantments, flags);
            customItems.put(id, item);
        }
    }

    public void saveCustomItems() {
        for (Map.Entry<String, ItemStack> entry : customItems.entrySet()) {
            String id = entry.getKey();
            ItemStack item = entry.getValue();
            ItemMeta meta = item.getItemMeta();

            itemsConfig.set(id + ".name", meta.getDisplayName());
            itemsConfig.set(id + ".description", meta.getLore().get(0));
            itemsConfig.set(id + ".material", item.getType().name());
            itemsConfig.set(id + ".amount", item.getAmount());
            itemsConfig.set(id + ".glowing", meta.hasEnchants());
            itemsConfig.set(id + ".lore", meta.getLore());

            List<String> enchantments = new ArrayList<>();
            for (Map.Entry<Enchantment, Integer> enchantment : meta.getEnchants().entrySet()) {
                enchantments.add(enchantment.getKey().getKey().getKey() + ":" + enchantment.getValue());
            }
            itemsConfig.set(id + ".enchantments", enchantments);

            List<String> flags = new ArrayList<>();
            for (ItemFlag flag : meta.getItemFlags()) {
                flags.add(flag.name());
            }
            itemsConfig.set(id + ".flags", flags);
        }

        try {
            itemsConfig.save(itemsFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Impossible de sauvegarder les items : " + e.getMessage());
        }
    }

    private ItemStack createCustomItem(String name, String description, String material, int amount, boolean glowing, List<String> lore, Map<String, Integer> enchantments, List<String> flags) {
        Material mat = Material.valueOf(material);
        ItemStack item = new ItemStack(mat, amount);
        ItemMeta meta = item.getItemMeta();

        // Nom
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));

        // Description et lore
        List<String> itemLore = new ArrayList<>();
        itemLore.add(ChatColor.translateAlternateColorCodes('&', description));
        for (String line : lore) {
            itemLore.add(ChatColor.translateAlternateColorCodes('&', line));
        }
        meta.setLore(itemLore);

        // Enchantements
        for (Map.Entry<String, Integer> enchantment : enchantments.entrySet()) {
            Enchantment ench = Enchantment.getByKey(org.bukkit.NamespacedKey.minecraft(enchantment.getKey()));
            if (ench != null) {
                meta.addEnchant(ench, enchantment.getValue(), true);
            }
        }

        // Flags
        for (String flag : flags) {
            try {
                meta.addItemFlags(ItemFlag.valueOf(flag));
            } catch (IllegalArgumentException ignored) {
            }
        }

        item.setItemMeta(meta);
        return item;
    }

    public ItemStack getCustomItem(String id) {
        return customItems.get(id);
    }

    public boolean isCustomItem(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return false;
        }
        ItemMeta meta = item.getItemMeta();
        return meta.hasDisplayName() && meta.hasLore();
    }

    public String getCustomItemId(ItemStack item) {
        if (!isCustomItem(item)) {
            return null;
        }
        for (Map.Entry<String, ItemStack> entry : customItems.entrySet()) {
            if (entry.getValue().isSimilar(item)) {
                return entry.getKey();
            }
        }
        return null;
    }

    public void addCustomItem(String id, ItemStack item) {
        customItems.put(id, item);
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