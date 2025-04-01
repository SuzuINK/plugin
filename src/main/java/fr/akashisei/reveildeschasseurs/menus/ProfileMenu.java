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
import java.util.List;

public class ProfileMenu {
    private final ReveilDesChasseurs plugin;

    public ProfileMenu(ReveilDesChasseurs plugin) {
        this.plugin = plugin;
    }

    public void openMenu(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 54, ChatColor.DARK_PURPLE + "Profil");
        PlayerData playerData = plugin.getPlayerDataManager().getOrCreatePlayerData(player);

        // Tête du joueur
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta headMeta = (SkullMeta) head.getItemMeta();
        headMeta.setDisplayName(ChatColor.GOLD + player.getName());
        headMeta.setOwningPlayer(player);
        List<String> headLore = new ArrayList<>();
        headLore.add(ChatColor.YELLOW + "Classe: " + ChatColor.WHITE + playerData.getPlayerClass());
        headLore.add(ChatColor.YELLOW + "Rang: " + ChatColor.WHITE + playerData.getRank());
        headMeta.setLore(headLore);
        head.setItemMeta(headMeta);
        inventory.setItem(4, head);

        // Niveau et expérience
        ItemStack level = new ItemStack(Material.EXPERIENCE_BOTTLE);
        ItemMeta levelMeta = level.getItemMeta();
        levelMeta.setDisplayName(ChatColor.GOLD + "Niveau et Expérience");
        List<String> levelLore = new ArrayList<>();
        levelLore.add(ChatColor.YELLOW + "Niveau: " + ChatColor.WHITE + playerData.getLevel());
        levelLore.add(ChatColor.YELLOW + "Expérience: " + ChatColor.WHITE + playerData.getExperience());
        levelMeta.setLore(levelLore);
        level.setItemMeta(levelMeta);
        inventory.setItem(20, level);

        // Argent
        ItemStack money = new ItemStack(Material.GOLD_INGOT);
        ItemMeta moneyMeta = money.getItemMeta();
        moneyMeta.setDisplayName(ChatColor.GOLD + "Argent");
        List<String> moneyLore = new ArrayList<>();
        moneyLore.add(ChatColor.YELLOW + "Coins: " + ChatColor.WHITE + playerData.getMoney());
        moneyMeta.setLore(moneyLore);
        money.setItemMeta(moneyMeta);
        inventory.setItem(22, money);

        // Statistiques
        ItemStack stats = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta statsMeta = stats.getItemMeta();
        statsMeta.setDisplayName(ChatColor.GOLD + "Statistiques");
        List<String> statsLore = new ArrayList<>();
        statsLore.add(ChatColor.YELLOW + "Mobs tués: " + ChatColor.WHITE + playerData.getMobsKilled());
        statsLore.add(ChatColor.YELLOW + "Morts: " + ChatColor.WHITE + playerData.getDeaths());
        statsLore.add(ChatColor.YELLOW + "Donjons complétés: " + ChatColor.WHITE + playerData.getCompletedDungeons());
        statsLore.add(ChatColor.YELLOW + "Donjons échoués: " + ChatColor.WHITE + playerData.getFailedDungeons());
        statsLore.add(ChatColor.YELLOW + "Quêtes complétées: " + ChatColor.WHITE + playerData.getCompletedQuests());
        statsMeta.setLore(statsLore);
        stats.setItemMeta(statsMeta);
        inventory.setItem(24, stats);

        // Quêtes actives
        ItemStack activeQuests = new ItemStack(Material.BOOK);
        ItemMeta questsMeta = activeQuests.getItemMeta();
        questsMeta.setDisplayName(ChatColor.GOLD + "Quêtes actives");
        List<String> questsLore = new ArrayList<>();
        questsLore.add(ChatColor.YELLOW + "Nombre de quêtes actives: " + ChatColor.WHITE + playerData.getActiveQuests().size());
        questsMeta.setLore(questsLore);
        activeQuests.setItemMeta(questsMeta);
        inventory.setItem(30, activeQuests);

        // Bouton de réinitialisation des statistiques
        ItemStack resetStats = new ItemStack(Material.BARRIER);
        ItemMeta resetStatsMeta = resetStats.getItemMeta();
        resetStatsMeta.setDisplayName(ChatColor.RED + "Réinitialiser les statistiques");
        List<String> resetStatsLore = new ArrayList<>();
        resetStatsLore.add(ChatColor.GRAY + "Cliquez pour réinitialiser vos statistiques");
        resetStatsMeta.setLore(resetStatsLore);
        resetStats.setItemMeta(resetStatsMeta);
        inventory.setItem(48, resetStats);

        // Bouton de réinitialisation des quêtes
        ItemStack resetQuests = new ItemStack(Material.BARRIER);
        ItemMeta resetQuestsMeta = resetQuests.getItemMeta();
        resetQuestsMeta.setDisplayName(ChatColor.RED + "Réinitialiser les quêtes");
        List<String> resetQuestsLore = new ArrayList<>();
        resetQuestsLore.add(ChatColor.GRAY + "Cliquez pour réinitialiser vos quêtes");
        resetQuestsMeta.setLore(resetQuestsLore);
        resetQuests.setItemMeta(resetQuestsMeta);
        inventory.setItem(50, resetQuests);

        // Bouton de retour
        ItemStack back = new ItemStack(Material.ARROW);
        ItemMeta backMeta = back.getItemMeta();
        backMeta.setDisplayName(ChatColor.YELLOW + "Retour");
        back.setItemMeta(backMeta);
        inventory.setItem(49, back);

        player.openInventory(inventory);
    }

    public void handleClick(Player player, int slot, ItemStack clickedItem) {
        if (clickedItem == null || !clickedItem.hasItemMeta() || !clickedItem.getItemMeta().hasDisplayName()) {
            return;
        }

        String itemName = clickedItem.getItemMeta().getDisplayName();
        if (itemName.equals(ChatColor.YELLOW + "Retour")) {
            plugin.getMenuManager().openMainMenu(player);
        } else if (itemName.equals(ChatColor.RED + "Réinitialiser les statistiques")) {
            PlayerData playerData = plugin.getPlayerDataManager().getOrCreatePlayerData(player);
            playerData.setMobsKilled(0);
            playerData.setDeaths(0);
            playerData.setCompletedDungeons(0);
            playerData.setFailedDungeons(0);
            playerData.setCompletedQuests(0);
            plugin.getPlayerDataManager().savePlayerData(player.getUniqueId());
            player.sendMessage(ChatColor.GREEN + "Vos statistiques ont été réinitialisées!");
            openMenu(player);
        } else if (itemName.equals(ChatColor.RED + "Réinitialiser les quêtes")) {
            PlayerData playerData = plugin.getPlayerDataManager().getOrCreatePlayerData(player);
            playerData.getActiveQuests().clear();
            playerData.getQuestProgress().clear();
            playerData.getQuestCooldowns().clear();
            playerData.getQuestCompletions().clear();
            plugin.getPlayerDataManager().savePlayerData(player.getUniqueId());
            player.sendMessage(ChatColor.GREEN + "Vos quêtes ont été réinitialisées!");
            openMenu(player);
        }
    }
} 