package fr.akashisei.reveildeschasseurs.models;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.ChatColor;
import java.util.ArrayList;
import java.util.List;

public class CustomWeapon {
    private String id;
    private String name;
    private String description;
    private double damage;
    private double attackSpeed;
    private List<String> abilities;
    private ItemStack itemStack;

    public CustomWeapon(String id, String name, String description, double damage, double attackSpeed) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.damage = damage;
        this.attackSpeed = attackSpeed;
        this.abilities = new ArrayList<>();
        this.itemStack = createItemStack();
    }

    private ItemStack createItemStack() {
        ItemStack item = new ItemStack(org.bukkit.Material.DIAMOND_SWORD);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + name);
        
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + description);
        lore.add("");
        lore.add(ChatColor.RED + "Dégâts: " + damage);
        lore.add(ChatColor.BLUE + "Vitesse d'attaque: " + attackSpeed);
        
        if (!abilities.isEmpty()) {
            lore.add("");
            lore.add(ChatColor.YELLOW + "Capacités:");
            for (String ability : abilities) {
                lore.add(ChatColor.GRAY + "- " + ability);
            }
        }
        
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    public void addAbility(String ability) {
        abilities.add(ability);
        itemStack = createItemStack();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public double getDamage() {
        return damage;
    }

    public double getAttackSpeed() {
        return attackSpeed;
    }

    public List<String> getAbilities() {
        return new ArrayList<>(abilities);
    }

    public ItemStack getItemStack() {
        return itemStack.clone();
    }
} 