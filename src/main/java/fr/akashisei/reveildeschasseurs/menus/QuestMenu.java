package fr.akashisei.reveildeschasseurs.menus;

import fr.akashisei.reveildeschasseurs.ReveilDesChasseurs;
import fr.akashisei.reveildeschasseurs.models.PlayerData;
import fr.akashisei.reveildeschasseurs.quests.Quest;
import fr.akashisei.reveildeschasseurs.quests.QuestType;
import fr.akashisei.reveildeschasseurs.quests.QuestReward;
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
import java.util.Map;

public class QuestMenu {
    private final ReveilDesChasseurs plugin;
    private final Player player;
    private final List<Quest> quests;

    public QuestMenu(ReveilDesChasseurs plugin, Player player, List<Quest> quests) {
        this.plugin = plugin;
        this.player = player;
        this.quests = quests;
    }

    public void openMainMenu(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 27, ChatColor.GOLD + "Menu des Quêtes");

        // Quêtes actives
        ItemStack activeQuests = createMenuItem(Material.BOOK, ChatColor.GOLD + "Quêtes Actives", 
            ChatColor.GRAY + "Cliquez pour voir vos quêtes en cours");
        inventory.setItem(11, activeQuests);

        // Quêtes disponibles
        ItemStack availableQuests = createMenuItem(Material.WRITABLE_BOOK, ChatColor.GOLD + "Quêtes Disponibles", 
            ChatColor.GRAY + "Cliquez pour voir les quêtes disponibles");
        inventory.setItem(13, availableQuests);

        // Quêtes complétées
        ItemStack completedQuests = createMenuItem(Material.ENCHANTED_BOOK, ChatColor.GOLD + "Quêtes Complétées", 
            ChatColor.GRAY + "Cliquez pour voir vos quêtes terminées");
        inventory.setItem(15, completedQuests);

        player.openInventory(inventory);
    }

    public void openActiveQuests(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 54, "§6Quêtes Actives");
        
        // Remplir l'inventaire avec les quêtes actives
        List<String> activeQuestIds = plugin.getQuestManager().getActiveQuests(player);
        for (String questId : activeQuestIds) {
            Quest quest = plugin.getQuestManager().getQuest(questId);
            if (quest != null) {
                PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(player);
                int progress = playerData != null ? playerData.getQuestProgress(questId) : 0;
                inventory.addItem(createQuestItem(quest, progress));
            }
        }

        // Ajouter le bouton retour
        inventory.setItem(49, createBackButton());
        
        player.openInventory(inventory);
    }

    public void openAvailableQuests(Player player) {
        List<Quest> availableQuests = plugin.getQuestManager().getAvailableQuests(player);
        Inventory inventory = Bukkit.createInventory(null, 54, ChatColor.GOLD + "Quêtes Disponibles");
        int slot = 0;

        for (Quest quest : availableQuests) {
            inventory.setItem(slot++, createQuestItem(quest, 0));
        }

        player.openInventory(inventory);
    }

    public void openCompletedQuests(Player player) {
        PlayerData playerData = plugin.getPlayerDataManager().getOrCreatePlayerData(player);
        Inventory inventory = Bukkit.createInventory(null, 54, ChatColor.GOLD + "Quêtes Complétées");
        int slot = 0;

        for (String questId : playerData.getCompletedQuests()) {
            Quest quest = plugin.getQuestManager().getQuest(questId);
            if (quest != null) {
                inventory.setItem(slot++, createQuestItem(quest, quest.getRequired()));
            }
        }

        player.openInventory(inventory);
    }

    public void open(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 54, ChatColor.GOLD + "Menu des Quêtes");
        
        // Remplir l'inventaire avec les quêtes
        for (Quest quest : quests) {
            PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(player);
            int progress = playerData != null ? playerData.getQuestProgress(quest.getId()) : 0;
            inventory.addItem(createQuestItem(quest, progress));
        }

        // Ajouter le bouton retour
        inventory.setItem(49, createBackButton());
        
        player.openInventory(inventory);
    }

    private ItemStack createQuestItem(Quest quest, int progress) {
        Material material = getQuestMaterial(quest.getType());
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.GOLD + quest.getName());
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + quest.getDescription());
            lore.add("");
            lore.add(ChatColor.YELLOW + "Type: " + ChatColor.GRAY + quest.getType().getDisplayName());
            lore.add(ChatColor.YELLOW + "Progression: " + ChatColor.GRAY + progress + "/" + quest.getRequired());
            lore.add("");
            lore.add(ChatColor.YELLOW + "Niveau requis: " + ChatColor.GRAY + quest.getRequiredLevel());
            
            QuestReward reward = quest.getRewardReward();
            lore.add(ChatColor.YELLOW + "Récompenses:");
            lore.add(ChatColor.GRAY + "- " + reward.getBaseExp() + " XP");
            lore.add(ChatColor.GRAY + "- " + reward.getBaseMoney() + " pièces");

            if (!quest.getRequiredItems().isEmpty()) {
                lore.add("");
                lore.add(ChatColor.YELLOW + "Items requis:");
                for (Map.Entry<ItemStack, Integer> entry : quest.getRequiredItems().entrySet()) {
                    ItemStack reqItem = entry.getKey();
                    int amount = entry.getValue();
                    String itemName = reqItem.getItemMeta() != null && reqItem.getItemMeta().hasDisplayName() ? 
                        reqItem.getItemMeta().getDisplayName() : 
                        reqItem.getType().name().toLowerCase().replace("_", " ");
                    lore.add(ChatColor.GRAY + "- " + itemName + " x" + amount);
                }
            }

            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack createMenuItem(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            if (lore.length > 0) {
                meta.setLore(List.of(lore));
            }
            item.setItemMeta(meta);
        }
        return item;
    }

    private Material getQuestMaterial(QuestType type) {
        return switch (type) {
            case KILL -> Material.DIAMOND_SWORD;
            case COLLECT -> Material.CHEST;
            case CONSUME -> Material.GOLDEN_APPLE;
            case CRAFT -> Material.CRAFTING_TABLE;
            case FISH -> Material.FISHING_ROD;
            case TRADE -> Material.EMERALD;
            case DUNGEON -> Material.NETHER_STAR;
            default -> Material.BOOK;
        };
    }

    private ItemStack createBackButton() {
        ItemStack backButton = createMenuItem(Material.ARROW, ChatColor.RED + "Retour");
        ItemMeta meta = backButton.getItemMeta();
        if (meta != null) {
            meta.setLore(List.of(ChatColor.GRAY + "Retournez au menu principal"));
            meta.setDisplayName(ChatColor.RED + "Retour");
            backButton.setItemMeta(meta);
        }
        return backButton;
    }
} 