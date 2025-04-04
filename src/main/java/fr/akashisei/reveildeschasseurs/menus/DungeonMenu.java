package fr.akashisei.reveildeschasseurs.menus;

import fr.akashisei.reveildeschasseurs.ReveilDesChasseurs;
import fr.akashisei.reveildeschasseurs.models.Dungeon;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class DungeonMenu extends Menu {
    private static final int MENU_SIZE = 54; // 6 rangées
    private static final String MENU_TITLE = ChatColor.DARK_PURPLE + "Donjons disponibles";
    private static final int ITEMS_PER_PAGE = 45; // 5 rangées pour les donjons

    private final List<Dungeon> availableDungeons;
    private int currentPage = 0;

    public DungeonMenu(ReveilDesChasseurs plugin, Player player, List<Dungeon> dungeons) {
        super(plugin, MENU_TITLE, MENU_SIZE);
        this.availableDungeons = dungeons;
        update(player);
    }

    @Override
    public void update(Player player) {
        clearInventory();
        setupMenu();
    }

    private void setupMenu() {
        // Calculer les indices de début et de fin pour la page actuelle
        int startIndex = currentPage * ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, availableDungeons.size());

        // Ajouter les donjons pour la page actuelle
        for (int i = startIndex; i < endIndex; i++) {
            Dungeon dungeon = availableDungeons.get(i);
            setItem(i - startIndex, createDungeonItem(dungeon));
        }

        // Ajouter les boutons de navigation si nécessaire
        if (currentPage > 0) {
            setItem(45, createNavigationItem(Material.ARROW, "Page précédente"));
        }
        if (endIndex < availableDungeons.size()) {
            setItem(53, createNavigationItem(Material.ARROW, "Page suivante"));
        }

        // Ajouter le bouton de fermeture
        setItem(49, createNavigationItem(Material.BARRIER, "Fermer"));
    }

    private ItemStack createDungeonItem(Dungeon dungeon) {
        ItemStack item = new ItemStack(Material.END_PORTAL_FRAME);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.GOLD + dungeon.getName());
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + dungeon.getDescription());
            lore.add("");
            lore.add(ChatColor.YELLOW + "Niveau requis : " + ChatColor.WHITE + dungeon.getMinLevel());
            lore.add(ChatColor.YELLOW + "Temps limite : " + ChatColor.WHITE + dungeon.getTimeLimit() + " minutes");
            lore.add(ChatColor.YELLOW + "Récompense : " + ChatColor.GOLD + dungeon.getBaseRewardMoney() + " pièces");
            lore.add("");
            lore.add(ChatColor.GREEN + "Cliquez pour rejoindre !");
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

    private void nextPage() {
        if ((currentPage + 1) * ITEMS_PER_PAGE < availableDungeons.size()) {
            currentPage++;
            update(null);
        }
    }

    private void previousPage() {
        if (currentPage > 0) {
            currentPage--;
            update(null);
        }
    }

    private Dungeon getDungeonAt(int slot) {
        int index = currentPage * ITEMS_PER_PAGE + slot;
        if (index >= 0 && index < availableDungeons.size()) {
            return availableDungeons.get(index);
        }
        return null;
    }

    @Override
    public void onClick(Player player, int slot) {
        ItemStack clickedItem = getItem(slot);
        if (clickedItem == null || !clickedItem.hasItemMeta() || !clickedItem.getItemMeta().hasDisplayName()) {
            return;
        }

        String itemName = clickedItem.getItemMeta().getDisplayName();

        if (itemName.equals(ChatColor.YELLOW + "Fermer")) {
            plugin.getMenuManager().openMainMenu(player);
        } else if (itemName.equals(ChatColor.YELLOW + "Page précédente")) {
            previousPage();
        } else if (itemName.equals(ChatColor.YELLOW + "Page suivante")) {
            nextPage();
        } else {
            Dungeon dungeon = getDungeonAt(slot);
            if (dungeon != null) {
                player.closeInventory();
                plugin.getDungeonManager().startDungeon(player, dungeon);
            }
        }
    }
} 