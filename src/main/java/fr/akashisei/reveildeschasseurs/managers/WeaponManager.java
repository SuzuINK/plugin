package fr.akashisei.reveildeschasseurs.managers;

import fr.akashisei.reveildeschasseurs.ReveilDesChasseurs;
import fr.akashisei.reveildeschasseurs.models.Weapon;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class WeaponManager {
    private final ReveilDesChasseurs plugin;
    private final Map<String, Weapon> weapons;
    private final File weaponsFile;
    private final FileConfiguration weaponsConfig;

    public WeaponManager(ReveilDesChasseurs plugin) {
        this.plugin = plugin;
        this.weapons = new HashMap<>();
        this.weaponsFile = new File(plugin.getDataFolder(), "weapons.yml");
        this.weaponsConfig = YamlConfiguration.loadConfiguration(weaponsFile);
        loadWeapons();
    }

    public void loadWeapons() {
        if (!weaponsFile.exists()) {
            createDefaultWeapons();
            return;
        }

        for (String weaponId : weaponsConfig.getKeys(false)) {
            String name = weaponsConfig.getString(weaponId + ".name", "Arme inconnue");
            String description = weaponsConfig.getString(weaponId + ".description", "");
            int level = weaponsConfig.getInt(weaponId + ".level", 1);
            int requiredLevel = weaponsConfig.getInt(weaponId + ".required_level", 1);
            double damage = weaponsConfig.getDouble(weaponId + ".damage", 1.0);
            int durability = weaponsConfig.getInt(weaponId + ".durability", 100);
            long price = weaponsConfig.getLong(weaponId + ".price", 0);
            String material = weaponsConfig.getString(weaponId + ".material", "DIAMOND_SWORD");

            ItemStack itemStack = new ItemStack(Material.valueOf(material));
            Weapon weapon = new Weapon(name, description, level, requiredLevel, damage, durability, price, itemStack);
            weapons.put(weaponId, weapon);
        }
    }

    private void createDefaultWeapons() {
        // Épée de base
        ItemStack basicSword = new ItemStack(Material.IRON_SWORD);
        Weapon basicWeapon = new Weapon(
            "Épée de base",
            "Une épée simple pour débuter",
            1,
            1,
            5.0,
            100,
            100,
            basicSword
        );
        weapons.put("basic_sword", basicWeapon);

        // Épée avancée
        ItemStack advancedSword = new ItemStack(Material.DIAMOND_SWORD);
        Weapon advancedWeapon = new Weapon(
            "Épée avancée",
            "Une épée plus puissante",
            2,
            5,
            10.0,
            200,
            500,
            advancedSword
        );
        weapons.put("advanced_sword", advancedWeapon);

        saveWeapons();
    }

    public void saveWeapons() {
        for (Map.Entry<String, Weapon> entry : weapons.entrySet()) {
            String weaponId = entry.getKey();
            Weapon weapon = entry.getValue();

            weaponsConfig.set(weaponId + ".name", weapon.getName());
            weaponsConfig.set(weaponId + ".description", weapon.getDescription());
            weaponsConfig.set(weaponId + ".level", weapon.getLevel());
            weaponsConfig.set(weaponId + ".required_level", weapon.getRequiredLevel());
            weaponsConfig.set(weaponId + ".damage", weapon.getDamage());
            weaponsConfig.set(weaponId + ".durability", weapon.getDurability());
            weaponsConfig.set(weaponId + ".price", weapon.getPrice());
            weaponsConfig.set(weaponId + ".material", weapon.getItemStack().getType().name());
        }

        try {
            weaponsConfig.save(weaponsFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Impossible de sauvegarder les armes : " + e.getMessage());
        }
    }

    public Weapon getWeapon(String name) {
        return weapons.get(name);
    }

    public Collection<Weapon> getAllWeapons() {
        return weapons.values();
    }

    public boolean giveWeapon(Player player, Weapon weapon) {
        if (player.getLevel() < weapon.getRequiredLevel()) {
            return false;
        }

        player.getInventory().addItem(weapon.createItemStack());
        return true;
    }

    public boolean upgradeWeapon(Player player, Weapon weapon) {
        // TODO: Implémenter la logique d'amélioration des armes
        return false;
    }
} 