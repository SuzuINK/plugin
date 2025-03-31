package fr.akashisei.reveildeschasseurs.menus;

import fr.akashisei.reveildeschasseurs.ReveilDesChasseurs;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public abstract class Menu {
    protected final ReveilDesChasseurs plugin;
    protected final Inventory inventory;
    protected final String title;
    protected final int size;

    public Menu(ReveilDesChasseurs plugin, String title, int size) {
        this.plugin = plugin;
        this.title = title;
        this.size = size;
        this.inventory = Bukkit.createInventory(null, size, title);
    }

    public void open(Player player) {
        player.openInventory(inventory);
        update(player);
    }

    public abstract void update(Player player);

    public void handleClick(InventoryClickEvent event) {
        event.setCancelled(true);
        if (event.getWhoClicked() instanceof Player) {
            onClick((Player) event.getWhoClicked(), event.getRawSlot());
        }
    }

    public abstract void onClick(Player player, int slot);

    public void handleClose(InventoryCloseEvent event) {
        // Par défaut ne fait rien, peut être surchargé par les sous-classes
    }

    public Inventory getInventory() {
        return inventory;
    }

    protected void setItem(int slot, ItemStack item) {
        inventory.setItem(slot, item);
    }

    protected ItemStack getItem(int slot) {
        return inventory.getItem(slot);
    }

    protected void clearInventory() {
        inventory.clear();
    }
} 