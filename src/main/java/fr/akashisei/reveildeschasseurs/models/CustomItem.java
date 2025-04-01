package fr.akashisei.reveildeschasseurs.models;

import fr.akashisei.reveildeschasseurs.ReveilDesChasseurs;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.NamespacedKey;

import java.util.ArrayList;
import java.util.List;

public class CustomItem {
    private final String id;
    private final ReveilDesChasseurs plugin;
    private String name;
    private Material material;
    private List<String> lore;
    private int amount;
    private boolean glowing;
    private double dropRate;
    private int minAmount;
    private int maxAmount;
    private List<String> enchantments;
    private List<String> potionEffects;
    private List<String> attributes;

    public CustomItem(ReveilDesChasseurs plugin, String id) {
        this.plugin = plugin;
        this.id = id;
        this.lore = new ArrayList<>();
        this.amount = 1;
        this.minAmount = 1;
        this.maxAmount = 1;
        this.enchantments = new ArrayList<>();
        this.potionEffects = new ArrayList<>();
        this.attributes = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public List<String> getLore() {
        return lore;
    }

    public void setLore(List<String> lore) {
        this.lore = lore;
    }

    public void addLore(String line) {
        this.lore.add(line);
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public boolean isGlowing() {
        return glowing;
    }

    public void setGlowing(boolean glowing) {
        this.glowing = glowing;
    }

    public double getDropRate() {
        return dropRate;
    }

    public void setDropRate(double dropRate) {
        this.dropRate = dropRate;
    }

    public int getMinAmount() {
        return minAmount;
    }

    public void setMinAmount(int minAmount) {
        this.minAmount = minAmount;
    }

    public int getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(int maxAmount) {
        this.maxAmount = maxAmount;
    }

    public List<String> getEnchantments() {
        return enchantments;
    }

    public void setEnchantments(List<String> enchantments) {
        this.enchantments = enchantments != null ? enchantments : new ArrayList<>();
    }

    public void addEnchantment(String enchantment) {
        this.enchantments.add(enchantment);
    }

    public List<String> getPotionEffects() {
        return potionEffects;
    }

    public void setPotionEffects(List<String> potionEffects) {
        this.potionEffects = potionEffects != null ? potionEffects : new ArrayList<>();
    }

    public void addPotionEffect(String effect) {
        this.potionEffects.add(effect);
    }

    public List<String> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<String> attributes) {
        this.attributes = attributes != null ? attributes : new ArrayList<>();
    }

    public void addAttribute(String attribute) {
        this.attributes.add(attribute);
    }

    public ItemStack createItemStack() {
        ItemStack item = new ItemStack(material, amount);
        ItemMeta meta = item.getItemMeta();
        
        // Nom
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        
        // Lore
        List<String> coloredLore = new ArrayList<>();
        for (String line : lore) {
            coloredLore.add(ChatColor.translateAlternateColorCodes('&', line));
        }
        meta.setLore(coloredLore);
        
        // Enchantements
        for (String enchantment : enchantments) {
            String[] parts = enchantment.split(":");
            if (parts.length == 2) {
                try {
                    org.bukkit.enchantments.Enchantment ench = org.bukkit.enchantments.Enchantment.getByKey(
                        org.bukkit.NamespacedKey.minecraft(parts[0].toLowerCase())
                    );
                    if (ench != null) {
                        meta.addEnchant(ench, Integer.parseInt(parts[1]), true);
                    }
                } catch (Exception e) {
                    // Ignorer les enchantements invalides
                }
            }
        }
        
        // Effets de potion
        if (!potionEffects.isEmpty()) {
            // TODO: Implémenter les effets de potion
        }
        
        // Attributs
        if (!attributes.isEmpty()) {
            // TODO: Implémenter les attributs
        }
        
        // Données persistantes
        PersistentDataContainer container = meta.getPersistentDataContainer();
        container.set(
            new NamespacedKey(plugin, "custom_item_id"),
            PersistentDataType.STRING,
            id
        );
        
        item.setItemMeta(meta);
        return item;
    }

    public ItemStack generateRandomAmount() {
        int randomAmount = (int) (Math.random() * (maxAmount - minAmount + 1)) + minAmount;
        ItemStack item = createItemStack();
        item.setAmount(randomAmount);
        return item;
    }
} 