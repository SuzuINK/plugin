package fr.akashisei.reveildeschasseurs.managers;

import fr.akashisei.reveildeschasseurs.ReveilDesChasseurs;
import fr.akashisei.reveildeschasseurs.menus.*;
import fr.akashisei.reveildeschasseurs.models.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public class MenuManager {
    private final ReveilDesChasseurs plugin;

    public MenuManager(ReveilDesChasseurs plugin) {
        this.plugin = plugin;
    }

    public void openMainMenu(Player player) {
        MainMenu menu = new MainMenu(plugin);
        menu.open(player);
    }

    public void openDungeonsMenu(Player player) {
        PlayerData playerData = plugin.getPlayerDataManager().getOrCreatePlayerData(player);
        DungeonMenu menu = new DungeonMenu(plugin, player, 
            plugin.getDungeonManager().getAvailableDungeons(player));
        menu.open(player);
    }

    public void openQuestsMenu(Player player) {
        PlayerData playerData = plugin.getPlayerDataManager().getOrCreatePlayerData(player);
        QuestMenu menu = new QuestMenu(plugin, player, 
            plugin.getQuestManager().getAvailableQuests(player));
        menu.open(player);
    }

    public void openProfileMenu(Player player) {
        ProfileMenu menu = new ProfileMenu(plugin);
        menu.open(player);
    }

    private ItemStack createMenuItem(Material material, String name, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }
} 