package fr.akashisei.reveildeschasseurs.models;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.List;

public class Weapon {
    private final String name;
    private final String description;
    private final int level;
    private final int requiredLevel;
    private final double damage;
    private final int durability;
    private final long price;
    private final ItemStack itemStack;

    public Weapon(String name, String description, int level, int requiredLevel, 
                 double damage, int durability, long price, ItemStack itemStack) {
        this.name = name;
        this.description = description;
        this.level = level;
        this.requiredLevel = requiredLevel;
        this.damage = damage;
        this.durability = durability;
        this.price = price;
        this.itemStack = itemStack;
    }

    // Getters
    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getLevel() { return level; }
    public int getRequiredLevel() { return requiredLevel; }
    public double getDamage() { return damage; }
    public int getDurability() { return durability; }
    public long getPrice() { return price; }
    public ItemStack getItemStack() { return itemStack; }

    // Méthodes utilitaires
    public ItemStack createItemStack() {
        ItemStack item = itemStack.clone();
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            List<String> lore = meta.getLore();
            if (lore != null) {
                lore.add("§7Niveau requis: " + requiredLevel);
                lore.add("§cDégâts: " + damage);
                lore.add("§bDurabilité: " + durability);
                lore.add("§6Prix: " + price + " pièces");
                meta.setLore(lore);
            }
            item.setItemMeta(meta);
        }
        return item;
    }
} 