package fr.akashisei.reveildeschasseurs.menus;

import fr.akashisei.reveildeschasseurs.ReveilDesChasseurs;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class MainMenu extends Menu {
    public MainMenu(ReveilDesChasseurs plugin) {
        super(plugin, ChatColor.GOLD + "Menu Principal", 27);
    }

    @Override
    public void update(Player player) {
        clearInventory();

        // Profil
        ItemStack profile = new ItemStack(Material.PLAYER_HEAD);
        ItemMeta profileMeta = profile.getItemMeta();
        profileMeta.setDisplayName(ChatColor.YELLOW + "Profil");
        profileMeta.setLore(Arrays.asList(
            ChatColor.GRAY + "Cliquez pour voir votre profil",
            "",
            ChatColor.WHITE + "Niveau: " + plugin.getPlayerDataManager().getOrCreatePlayerData(player).getLevel(),
            ChatColor.WHITE + "XP: " + plugin.getPlayerDataManager().getOrCreatePlayerData(player).getExperience()
        ));
        profile.setItemMeta(profileMeta);
        setItem(10, profile);

        // Quêtes
        ItemStack quests = new ItemStack(Material.BOOK);
        ItemMeta questsMeta = quests.getItemMeta();
        questsMeta.setDisplayName(ChatColor.YELLOW + "Quêtes");
        questsMeta.setLore(Arrays.asList(
            ChatColor.GRAY + "Cliquez pour voir les quêtes disponibles",
            "",
            ChatColor.WHITE + "Quêtes complétées: " + plugin.getPlayerDataManager().getOrCreatePlayerData(player).getCompletedQuests()
        ));
        quests.setItemMeta(questsMeta);
        setItem(13, quests);

        // Donjons
        ItemStack dungeons = new ItemStack(Material.NETHER_STAR);
        ItemMeta dungeonsMeta = dungeons.getItemMeta();
        dungeonsMeta.setDisplayName(ChatColor.YELLOW + "Donjons");
        dungeonsMeta.setLore(Arrays.asList(
            ChatColor.GRAY + "Cliquez pour voir les donjons disponibles",
            "",
            ChatColor.WHITE + "Donjons complétés: " + plugin.getPlayerDataManager().getOrCreatePlayerData(player).getCompletedDungeons()
        ));
        dungeons.setItemMeta(dungeonsMeta);
        setItem(16, dungeons);
    }

    @Override
    public void onClick(Player player, int slot) {
        switch (slot) {
            case 10: // Profil
                plugin.getMenuManager().openProfileMenu(player);
                break;
            case 13: // Quêtes
                plugin.getMenuManager().openQuestMenu(player, 1);
                break;
            case 16: // Donjons
                plugin.getMenuManager().openDungeonMenu(player);
                break;
        }
    }
} 