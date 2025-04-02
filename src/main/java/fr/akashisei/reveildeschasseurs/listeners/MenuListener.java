package fr.akashisei.reveildeschasseurs.listeners;

import fr.akashisei.reveildeschasseurs.ReveilDesChasseurs;
import fr.akashisei.reveildeschasseurs.menus.Menu;
import fr.akashisei.reveildeschasseurs.models.Dungeon;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MenuListener implements Listener {
    private final ReveilDesChasseurs plugin;
    private final Map<UUID, Menu> openMenus;

    public MenuListener(ReveilDesChasseurs plugin) {
        this.plugin = plugin;
        this.openMenus = new HashMap<>();
    }

    public void openMenu(Player player, Menu menu) {
        menu.open(player);
        openMenus.put(player.getUniqueId(), menu);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();
        String title = event.getView().getTitle();

        // Annuler l'événement pour empêcher de prendre les items
        event.setCancelled(true);

        // Menu Principal
        if (title.equals(ChatColor.translateAlternateColorCodes('&', "&6Menu Principal"))) {
            handleMainMenuClick(event, player);
        }
        // Menu des Donjons
        else if (title.equals(ChatColor.translateAlternateColorCodes('&', "&cDonjons Disponibles"))) {
            handleDungeonsMenuClick(event, player);
        }
        // Menu du Profil
        else if (title.startsWith(ChatColor.translateAlternateColorCodes('&', "&aProfil de"))) {
            handleProfileMenuClick(event, player);
        }
        // Menu des Quêtes
        else if (title.equals(ChatColor.translateAlternateColorCodes('&', "&eQuêtes"))) {
            handleQuestsMenuClick(event, player);
        }
    }

    private void handleMainMenuClick(InventoryClickEvent event, Player player) {
        if (event.getCurrentItem() == null) return;
        
        switch (event.getSlot()) {
            case 11: // Donjons
                plugin.getMenuManager().openDungeonsMenu(player);
                break;
            case 13: // Profil
                plugin.getMenuManager().openProfileMenu(player);
                break;
            case 15: // Quêtes
                plugin.getMenuManager().openQuestsMenu(player);
                break;
        }
    }

    private void handleDungeonsMenuClick(InventoryClickEvent event, Player player) {
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null) return;

        if (event.getSlot() == 49) { // Bouton retour
            plugin.getMenuManager().openMainMenu(player);
            return;
        }

        // Vérifier si c'est un donjon
        if (clickedItem.getType().name().contains("NETHER_BRICK")) {
            String dungeonName = ChatColor.stripColor(clickedItem.getItemMeta().getDisplayName());
            Dungeon dungeon = plugin.getDungeonManager().getDungeonByName(dungeonName);
            if (dungeon != null) {
                plugin.getDungeonManager().startDungeon(player, dungeon);
            }
        }
    }

    private void handleProfileMenuClick(InventoryClickEvent event, Player player) {
        if (event.getCurrentItem() == null) return;

        if (event.getSlot() == 26) { // Bouton retour
            plugin.getMenuManager().openMainMenu(player);
        }
    }

    private void handleQuestsMenuClick(InventoryClickEvent event, Player player) {
        if (event.getCurrentItem() == null) return;

        if (event.getSlot() == 49) { // Bouton retour
            plugin.getMenuManager().openMainMenu(player);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getPlayer();
        Menu menu = openMenus.get(player.getUniqueId());

        if (menu != null && event.getInventory().equals(menu.getInventory())) {
            openMenus.remove(player.getUniqueId());
            menu.handleClose(event);
        }
    }

    public Menu getOpenMenu(Player player) {
        return openMenus.get(player.getUniqueId());
    }

    public void closeMenu(Player player) {
        Menu menu = openMenus.remove(player.getUniqueId());
        if (menu != null) {
            player.closeInventory();
        }
    }
} 