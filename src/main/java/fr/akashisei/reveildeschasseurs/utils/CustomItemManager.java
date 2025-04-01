package fr.akashisei.reveildeschasseurs.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.ChatColor;

import java.util.Arrays;
import java.util.List;

public class CustomItemManager {
    public static ItemStack createCustomItem(Material material, String name, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    // Objets communs
    public static ItemStack getHealingPotion() {
        return createCustomItem(
            Material.SPLASH_POTION,
            "&cPotion de Soin",
            Arrays.asList(
                "&7Restaure 50% de vos PV",
                "&7Cooldown: 30 secondes"
            )
        );
    }

    public static ItemStack getManaPotion() {
        return createCustomItem(
            Material.SPLASH_POTION,
            "&bPotion de Mana",
            Arrays.asList(
                "&7Restaure 50% de votre mana",
                "&7Cooldown: 30 secondes"
            )
        );
    }

    public static ItemStack getBasicRune() {
        return createCustomItem(
            Material.PAPER,
            "&fRune Basique",
            Arrays.asList(
                "&7Utilisée pour améliorer",
                "&7vos équipements"
            )
        );
    }

    // Objets peu communs
    public static ItemStack getMagicStone() {
        return createCustomItem(
            Material.EMERALD,
            "&aPierre Magique",
            Arrays.asList(
                "&7Utilisée pour améliorer",
                "&7vos armes et armures"
            )
        );
    }

    public static ItemStack getEnhancementStone() {
        return createCustomItem(
            Material.DIAMOND,
            "&bPierre d'Amélioration",
            Arrays.asList(
                "&7Utilisée pour améliorer",
                "&7vos équipements de rang supérieur"
            )
        );
    }

    public static ItemStack getRareRune() {
        return createCustomItem(
            Material.PAPER,
            "&eRune Rare",
            Arrays.asList(
                "&7Utilisée pour améliorer",
                "&7vos équipements rares"
            )
        );
    }

    // Objets rares de boss
    public static ItemStack getCrystalFragment() {
        return createCustomItem(
            Material.PRISMARINE_SHARD,
            "&dFragment de Cristal",
            Arrays.asList(
                "&7Fragment magique obtenu",
                "&7en tuant des boss"
            )
        );
    }

    public static ItemStack getMagicCore() {
        return createCustomItem(
            Material.NETHER_STAR,
            "&5Noyau Magique",
            Arrays.asList(
                "&7Noyau puissant obtenu",
                "&7en tuant des boss légendaires"
            )
        );
    }

    public static ItemStack getAncientRune() {
        return createCustomItem(
            Material.PAPER,
            "&6Rune Ancienne",
            Arrays.asList(
                "&7Rune légendaire utilisée",
                "&7pour améliorer les équipements mythiques"
            )
        );
    }

    public static ItemStack getRankStone() {
        return createCustomItem(
            Material.NETHERITE_INGOT,
            "&cPierre de Rang",
            Arrays.asList(
                "&7Utilisée pour améliorer",
                "&7votre rang de chasseur"
            )
        );
    }
} 