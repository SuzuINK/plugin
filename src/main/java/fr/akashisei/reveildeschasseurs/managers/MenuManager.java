package fr.akashisei.reveildeschasseurs.managers;

import fr.akashisei.reveildeschasseurs.ReveilDesChasseurs;
import fr.akashisei.reveildeschasseurs.menus.DungeonMenu;
import fr.akashisei.reveildeschasseurs.menus.QuestMenu;
import fr.akashisei.reveildeschasseurs.menus.ProfileMenu;
import fr.akashisei.reveildeschasseurs.models.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public class MenuManager implements Listener {
    private final ReveilDesChasseurs plugin;
    private final DungeonMenu dungeonMenu;
    private final QuestMenu questMenu;
    private final ProfileMenu profileMenu;

    public MenuManager(ReveilDesChasseurs plugin) {
        this.plugin = plugin;
        this.dungeonMenu = new DungeonMenu(plugin);
        this.questMenu = new QuestMenu(plugin);
        this.profileMenu = new ProfileMenu(plugin);
    }

    public void openMainMenu(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 27, ChatColor.DARK_PURPLE + "Menu Principal");

        // Bouton Donjons
        inventory.setItem(11, createMenuItem(
            Material.END_PORTAL,
            ChatColor.GOLD + "Donjons",
            Arrays.asList(
                ChatColor.GRAY + "Explorez des donjons",
                ChatColor.GRAY + "et récupérez des récompenses!"
            )
        ));

        // Bouton Quêtes
        inventory.setItem(13, createMenuItem(
            Material.BOOK,
            ChatColor.GOLD + "Quêtes",
            Arrays.asList(
                ChatColor.GRAY + "Acceptez et complétez",
                ChatColor.GRAY + "des quêtes pour progresser!"
            )
        ));

        // Bouton Profil
        inventory.setItem(15, createMenuItem(
            Material.PLAYER_HEAD,
            ChatColor.GOLD + "Profil",
            Arrays.asList(
                ChatColor.GRAY + "Consultez votre profil",
                ChatColor.GRAY + "et vos statistiques!"
            )
        ));

        player.openInventory(inventory);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        if (event.getCurrentItem() == null) return;

        Player player = (Player) event.getWhoClicked();
        String title = event.getView().getTitle();
        ItemStack clickedItem = event.getCurrentItem();

        event.setCancelled(true);

        if (title.equals(ChatColor.DARK_PURPLE + "Menu Principal")) {
            handleMainMenuClick(player, clickedItem);
        }
        else if (title.equals(ChatColor.DARK_PURPLE + "Donjons disponibles")) {
            handleDungeonMenuClick(player, event.getSlot(), clickedItem);
        }
        else if (title.equals(ChatColor.DARK_PURPLE + "Quêtes")) {
            handleQuestMenuClick(player, event.getSlot(), clickedItem);
        }
        else if (title.equals(ChatColor.DARK_PURPLE + "Profil")) {
            handleProfileMenuClick(player, event.getSlot(), clickedItem);
        }
    }

    private void handleMainMenuClick(Player player, ItemStack clickedItem) {
        String itemName = clickedItem.getItemMeta().getDisplayName();

        if (itemName.equals(ChatColor.GOLD + "Donjons")) {
            openDungeonMenu(player);
        }
        else if (itemName.equals(ChatColor.GOLD + "Quêtes")) {
            openQuestMenu(player, 1);
        }
        else if (itemName.equals(ChatColor.GOLD + "Profil")) {
            openProfileMenu(player);
        }
    }

    public void openDungeonMenu(Player player) {
        dungeonMenu.openMenu(player);
    }

    public void openQuestMenu(Player player, int page) {
        questMenu.openMenu(player, page);
    }

    public void openProfileMenu(Player player) {
        profileMenu.openMenu(player);
    }

    public void handleDungeonMenuClick(Player player, int slot, ItemStack clickedItem) {
        dungeonMenu.handleClick(player, slot, clickedItem);
    }

    public void handleQuestMenuClick(Player player, int slot, ItemStack clickedItem) {
        questMenu.handleClick(player, slot, clickedItem);
    }

    public void handleProfileMenuClick(Player player, int slot, ItemStack clickedItem) {
        profileMenu.handleClick(player, slot, clickedItem);
    }

    private ItemStack createMenuItem(Material material, String name, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    public void openDungeonsMenu(Player player) {
        PlayerData playerData = plugin.getPlayerDataManager().getOrCreatePlayerData(player);
        DungeonMenu menu = new DungeonMenu(plugin, player, 
            plugin.getDungeonManager().getAvailableDungeons(player));
        player.openInventory(menu.getInventory());
    }

    public void openQuestsMenu(Player player) {
        PlayerData playerData = plugin.getPlayerDataManager().getOrCreatePlayerData(player);
        QuestMenu menu = new QuestMenu(plugin, player, plugin.getQuestManager().getAvailableQuests(player));
        player.openInventory(menu.getInventory());
    }
} 