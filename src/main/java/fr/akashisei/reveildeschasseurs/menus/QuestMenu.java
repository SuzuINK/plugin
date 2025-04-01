package fr.akashisei.reveildeschasseurs.menus;

import fr.akashisei.reveildeschasseurs.ReveilDesChasseurs;
import fr.akashisei.reveildeschasseurs.quests.Quest;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class QuestMenu {
    private final ReveilDesChasseurs plugin;
    private final Player player;
    private static final int MENU_SIZE = 54; // 6 rangées
    private static final String MENU_TITLE = ChatColor.DARK_PURPLE + "Quêtes disponibles";
    private static final int ITEMS_PER_PAGE = 45; // 5 rangées pour les quêtes
    private final Map<UUID, Integer> playerPages;

    private final Inventory inventory;
    private final List<Quest> availableQuests;
    private int currentPage = 0;

    public QuestMenu(ReveilDesChasseurs plugin) {
        this.plugin = plugin;
        this.player = null;
        this.playerPages = new HashMap<>();
        this.inventory = Bukkit.createInventory(null, MENU_SIZE, MENU_TITLE);
        this.availableQuests = new ArrayList<>();
        setupMenu();
    }

    public QuestMenu(ReveilDesChasseurs plugin, Player player, List<Quest> quests) {
        this.plugin = plugin;
        this.player = player;
        this.playerPages = new HashMap<>();
        this.inventory = Bukkit.createInventory(null, MENU_SIZE, MENU_TITLE);
        this.availableQuests = quests;
        setupMenu();
    }

    private void setupMenu() {
        // Calculer les indices de début et de fin pour la page actuelle
        int startIndex = currentPage * ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, availableQuests.size());

        // Ajouter les quêtes pour la page actuelle
        for (int i = startIndex; i < endIndex; i++) {
            Quest quest = availableQuests.get(i);
            inventory.setItem(i - startIndex, createQuestItem(quest));
        }

        // Ajouter les boutons de navigation si nécessaire
        if (currentPage > 0) {
            inventory.setItem(45, createNavigationItem(Material.ARROW, "Page précédente"));
        }
        if (endIndex < availableQuests.size()) {
            inventory.setItem(53, createNavigationItem(Material.ARROW, "Page suivante"));
        }

        // Ajouter le bouton de fermeture
        inventory.setItem(49, createNavigationItem(Material.BARRIER, "Fermer"));
    }

    private ItemStack createQuestItem(Quest quest) {
        ItemStack item = new ItemStack(Material.BOOK);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.GOLD + quest.getName());
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + quest.getDescription());
            lore.add("");
            lore.add(ChatColor.YELLOW + "Niveau requis : " + ChatColor.WHITE + quest.getRequiredLevel());
            lore.add(ChatColor.YELLOW + "Récompense : " + ChatColor.GOLD + quest.getRewardMoney() + " pièces");
            lore.add(ChatColor.YELLOW + "XP : " + ChatColor.GREEN + quest.getRewardExp() + " XP");
            lore.add("");
            lore.add(ChatColor.GREEN + "Cliquez pour accepter la quête !");
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack createNavigationItem(Material material, String name) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.YELLOW + name);
            item.setItemMeta(meta);
        }
        return item;
    }

    public void nextPage() {
        if ((currentPage + 1) * ITEMS_PER_PAGE < availableQuests.size()) {
            currentPage++;
            setupMenu();
        }
    }

    public void previousPage() {
        if (currentPage > 0) {
            currentPage--;
            setupMenu();
        }
    }

    public Inventory getInventory() {
        return inventory;
    }

    public Quest getQuestAt(int slot) {
        int index = currentPage * ITEMS_PER_PAGE + slot;
        if (index >= 0 && index < availableQuests.size()) {
            return availableQuests.get(index);
        }
        return null;
    }

    public void handleClick(Player player, int slot, ItemStack clickedItem) {
        if (clickedItem == null || !clickedItem.hasItemMeta() || !clickedItem.getItemMeta().hasDisplayName()) {
            return;
        }

        String itemName = clickedItem.getItemMeta().getDisplayName();
        int currentPage = playerPages.getOrDefault(player.getUniqueId(), 1);

        if (itemName.equals(ChatColor.YELLOW + "Fermer")) {
            plugin.getMenuManager().openMainMenu(player);
            playerPages.remove(player.getUniqueId());
        } else if (itemName.equals(ChatColor.YELLOW + "Page précédente")) {
            if (currentPage > 1) {
                openMenu(player, currentPage - 1);
            }
        } else if (itemName.equals(ChatColor.YELLOW + "Page suivante")) {
            List<Quest> availableQuests = plugin.getQuestManager().getAvailableQuests(player);
            int totalPages = (int) Math.ceil(availableQuests.size() / (double) ITEMS_PER_PAGE);
            if (currentPage < totalPages) {
                openMenu(player, currentPage + 1);
            }
        } else if (itemName.startsWith(ChatColor.GOLD.toString())) {
            String questName = ChatColor.stripColor(itemName);
            Quest quest = plugin.getQuestManager().getQuest(questName);
            if (quest != null) {
                plugin.getQuestManager().acceptQuest(player, quest.getId());
                player.closeInventory();
            }
        }
    }

    public void openMenu(Player player, int page) {
        List<Quest> availableQuests = plugin.getQuestManager().getAvailableQuests(player);
        int totalPages = (int) Math.ceil(availableQuests.size() / (double) ITEMS_PER_PAGE);
        page = Math.max(1, Math.min(page, totalPages));
        playerPages.put(player.getUniqueId(), page);

        Inventory inventory = Bukkit.createInventory(null, MENU_SIZE, MENU_TITLE + " - Page " + page + "/" + totalPages);

        // Ajouter les quêtes disponibles
        int startIndex = (page - 1) * ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, availableQuests.size());
        for (int i = startIndex; i < endIndex; i++) {
            Quest quest = availableQuests.get(i);
            inventory.setItem(i - startIndex, createQuestItem(quest));
        }

        // Ajouter les boutons de navigation
        if (page > 1) {
            inventory.setItem(45, createNavigationItem(Material.ARROW, "Page précédente"));
        }
        if (page < totalPages) {
            inventory.setItem(53, createNavigationItem(Material.ARROW, "Page suivante"));
        }

        // Ajouter le bouton de fermeture
        inventory.setItem(49, createNavigationItem(Material.BARRIER, "Fermer"));

        player.openInventory(inventory);
    }
} 