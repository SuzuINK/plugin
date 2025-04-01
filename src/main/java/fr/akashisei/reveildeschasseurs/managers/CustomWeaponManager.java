package fr.akashisei.reveildeschasseurs.managers;

import fr.akashisei.reveildeschasseurs.ReveilDesChasseurs;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.io.File;
import java.util.*;

public class CustomWeaponManager {
    private final ReveilDesChasseurs plugin;
    private final Map<String, ItemStack> weapons;
    private final Map<String, List<String>> tierWeapons;
    private final NamespacedKey weaponIdKey;

    public CustomWeaponManager(ReveilDesChasseurs plugin) {
        this.plugin = plugin;
        this.weapons = new HashMap<>();
        this.tierWeapons = new HashMap<>();
        this.weaponIdKey = new NamespacedKey(plugin, "weapon_id");
        loadWeapons();
    }

    private void loadWeapons() {
        File weaponFile = new File(plugin.getDataFolder(), "weapons/weapons.yml");
        if (!weaponFile.exists()) {
            plugin.saveResource("weapons/weapons.yml", false);
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(weaponFile);
        ConfigurationSection weaponsSection = config.getConfigurationSection("weapons");

        if (weaponsSection != null) {
            for (String weaponId : weaponsSection.getKeys(false)) {
                ConfigurationSection weaponSection = weaponsSection.getConfigurationSection(weaponId);
                if (weaponSection != null) {
                    ItemStack weapon = createWeapon(weaponId, weaponSection);
                    weapons.put(weaponId, weapon);

                    // Organiser les armes par tier
                    String tier = weaponSection.getString("tier");
                    tierWeapons.computeIfAbsent(tier, k -> new ArrayList<>()).add(weaponId);
                }
            }
        }
    }

    private ItemStack createWeapon(String weaponId, ConfigurationSection config) {
        Material type = Material.valueOf(config.getString("type", "DIAMOND_SWORD"));
        ItemStack item = new ItemStack(type);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            // Nom de l'arme
            meta.setDisplayName(config.getString("name", "§fArme inconnue"));

            // Lore
            meta.setLore(config.getStringList("lore"));

            // Custom Model Data pour le skin custom
            meta.setCustomModelData(config.getInt("custom_model_data", 0));

            // Attributs
            ConfigurationSection attributes = config.getConfigurationSection("attributes");
            if (attributes != null) {
                // Ajouter les attributs ici si nécessaire
            }

            // Stockage de l'ID de l'arme
            meta.getPersistentDataContainer().set(weaponIdKey, PersistentDataType.STRING, weaponId);

            // Flags pour cacher certaines informations
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
            meta.setUnbreakable(true);

            // Effet brillant pour les armes légendaires
            if (config.getString("tier", "").equals("S")) {
                meta.addEnchant(Enchantment.DURABILITY, 1, true);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }

            item.setItemMeta(meta);
        }

        return item;
    }

    public ItemStack getWeapon(String weaponId) {
        return weapons.get(weaponId) != null ? weapons.get(weaponId).clone() : null;
    }

    public List<ItemStack> getRandomWeaponsForTier(String tier, int count) {
        List<ItemStack> result = new ArrayList<>();
        List<String> availableWeapons = tierWeapons.get(tier);

        if (availableWeapons != null && !availableWeapons.isEmpty()) {
            Random random = new Random();
            for (int i = 0; i < count; i++) {
                String weaponId = availableWeapons.get(random.nextInt(availableWeapons.size()));
                ItemStack weapon = getWeapon(weaponId);
                if (weapon != null) {
                    result.add(weapon);
                }
            }
        }

        return result;
    }

    public String getWeaponTier(ItemStack item) {
        if (item != null && item.hasItemMeta()) {
            String weaponId = item.getItemMeta().getPersistentDataContainer()
                    .get(weaponIdKey, PersistentDataType.STRING);
            if (weaponId != null && weapons.containsKey(weaponId)) {
                return weapons.get(weaponId).getItemMeta().getPersistentDataContainer()
                        .get(new NamespacedKey(plugin, "tier"), PersistentDataType.STRING);
            }
        }
        return null;
    }

    public void addWeaponToChest(ItemStack chest, String dungeonTier) {
        // Logique pour ajouter une arme dans un coffre de donjon basée sur le tier
        Random random = new Random();
        double chance = random.nextDouble();
        String weaponTier;

        // Déterminer le tier de l'arme basé sur le tier du donjon
        switch (dungeonTier) {
            case "S":
                if (chance < 0.1) weaponTier = "S";
                else if (chance < 0.3) weaponTier = "A";
                else weaponTier = "B";
                break;
            case "A":
                if (chance < 0.05) weaponTier = "S";
                else if (chance < 0.2) weaponTier = "A";
                else weaponTier = "B";
                break;
            case "B":
                if (chance < 0.1) weaponTier = "A";
                else if (chance < 0.3) weaponTier = "B";
                else weaponTier = "C";
                break;
            default:
                weaponTier = "C";
        }

        List<ItemStack> possibleWeapons = getRandomWeaponsForTier(weaponTier, 1);
        if (!possibleWeapons.isEmpty()) {
            // Ajouter l'arme au coffre
            // TODO: Implémenter la logique d'ajout au coffre
        }
    }
} 