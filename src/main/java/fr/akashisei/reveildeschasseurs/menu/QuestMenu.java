package fr.akashisei.reveildeschasseurs.menu;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import fr.akashisei.reveildeschasseurs.ReveilDesChasseurs;
import fr.akashisei.reveildeschasseurs.quests.Quest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class QuestMenu {

    private ReveilDesChasseurs plugin;
    private Player player;
    private List<Quest> quests;
    private Inventory inventory;

    public QuestMenu(ReveilDesChasseurs plugin) {
        this.plugin = plugin;
        this.inventory = Bukkit.createInventory(null, 54, "§6Quêtes");
        initializeItems();
    }

    public QuestMenu(ReveilDesChasseurs plugin, Player player, List<Quest> quests) {
        this.plugin = plugin;
        this.player = player;
        this.quests = quests;
        this.inventory = Bukkit.createInventory(null, 54, "§6Quêtes");
        initializeItems();
    }

    private void initializeItems() {
        // Ajout des quêtes
        if (quests != null) {
            for (int i = 0; i < quests.size() && i < 45; i++) {
                Quest quest = quests.get(i);
                ItemStack questItem = createQuestItem(quest);
                inventory.setItem(i, questItem);
            }
        }

        // Bouton de fermeture
        ItemStack closeButton = new ItemStack(Material.BARRIER);
        ItemMeta closeMeta = closeButton.getItemMeta();
        closeMeta.setDisplayName(ChatColor.RED + "Fermer");
        closeButton.setItemMeta(closeMeta);
        inventory.setItem(49, closeButton);
    }

    private ItemStack createQuestItem(Quest quest) {
        ItemStack item = new ItemStack(Material.BOOK);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + quest.getName());
        
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "Description: " + ChatColor.WHITE + quest.getDescription());
        lore.add(ChatColor.GRAY + "Niveau requis: " + ChatColor.WHITE + quest.getRequiredLevel());
        
        Map<ItemStack, Integer> requiredItems = quest.getRequiredItems();
        if (!requiredItems.isEmpty()) {
            lore.add(ChatColor.GRAY + "Objets requis:");
            for (Map.Entry<ItemStack, Integer> entry : requiredItems.entrySet()) {
                ItemStack requiredItem = entry.getKey();
                int amount = entry.getValue();
                String itemName = requiredItem.getItemMeta().hasDisplayName() ? 
                    requiredItem.getItemMeta().getDisplayName() : 
                    requiredItem.getType().name();
                lore.add(ChatColor.WHITE + "- " + itemName + " x" + amount);
            }
        }
        
        lore.add("");
        lore.add(ChatColor.YELLOW + "Récompense : " + ChatColor.GOLD + quest.getRewardMoney() + " pièces");
        lore.add(ChatColor.YELLOW + "XP : " + ChatColor.GREEN + quest.getRewardExp() + " XP");
        
        if (quest.getRewardWeapon() != null && !quest.getRewardWeapon().isEmpty()) {
            lore.add(ChatColor.YELLOW + "Arme : " + ChatColor.GOLD + quest.getRewardWeapon());
        }
        
        if (!quest.getRewards().isEmpty()) {
            lore.add(ChatColor.YELLOW + "Autres récompenses:");
            for (String reward : quest.getRewards()) {
                lore.add(ChatColor.WHITE + "- " + reward);
            }
        }
        
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    public void openMenu(Player player) {
        player.openInventory(inventory);
    }
} 