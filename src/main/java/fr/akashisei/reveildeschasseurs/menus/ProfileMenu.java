package fr.akashisei.reveildeschasseurs.menus;

import fr.akashisei.reveildeschasseurs.ReveilDesChasseurs;
import fr.akashisei.reveildeschasseurs.models.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProfileMenu extends Menu {
    public ProfileMenu(ReveilDesChasseurs plugin) {
        super(plugin, ChatColor.translateAlternateColorCodes('&', "&aProfil"), 27);
    }

    @Override
    public void update(Player player) {
        PlayerData playerData = plugin.getPlayerDataManager().getOrCreatePlayerData(player);

        // Tête du joueur
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta headMeta = (SkullMeta) head.getItemMeta();
        headMeta.setDisplayName(ChatColor.GREEN + player.getName());
        headMeta.setLore(Arrays.asList(
            ChatColor.GRAY + "Niveau: " + ChatColor.YELLOW + playerData.getLevel(),
            ChatColor.GRAY + "XP: " + ChatColor.YELLOW + playerData.getExperience(),
            ChatColor.GRAY + "Rang: " + plugin.getRankManager().getPlayerRank(player).getColor() + 
                plugin.getRankManager().getPlayerRank(player).getDisplayName()
        ));
        head.setItemMeta(headMeta);
        setItem(13, head);

        // Statistiques
        setItem(11, createMenuItem(
            Material.DIAMOND_SWORD,
            ChatColor.RED + "Statistiques de combat",
            Arrays.asList(
                ChatColor.GRAY + "Force: " + ChatColor.WHITE + playerData.getStrength(),
                ChatColor.GRAY + "Défense: " + ChatColor.WHITE + playerData.getDefense(),
                ChatColor.GRAY + "Vitesse: " + ChatColor.WHITE + playerData.getSpeed()
            )
        ));

        setItem(15, createMenuItem(
            Material.CHEST,
            ChatColor.GOLD + "Progression",
            Arrays.asList(
                ChatColor.GRAY + "Donjons complétés: " + ChatColor.WHITE + playerData.getCompletedDungeons(),
                ChatColor.GRAY + "Quêtes complétées: " + ChatColor.WHITE + playerData.getCompletedQuests(),
                ChatColor.GRAY + "Pièces: " + ChatColor.YELLOW + plugin.getEconomyManager().getBalance(player)
            )
        ));

        // Bouton retour
        setItem(26, createMenuItem(
            Material.BARRIER,
            ChatColor.RED + "Retour",
            Arrays.asList(ChatColor.GRAY + "Retourner au menu principal")
        ));
    }

    @Override
    public void onClick(Player player, int slot) {
        if (slot == 26) { // Bouton retour
            plugin.getMenuManager().openMainMenu(player);
        }
    }

    private ItemStack createMenuItem(Material material, String name, java.util.List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }
} 