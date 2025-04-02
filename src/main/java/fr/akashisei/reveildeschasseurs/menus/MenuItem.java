package fr.akashisei.reveildeschasseurs.menus;

import org.bukkit.inventory.ItemStack;

public class MenuItem {
    private final ItemStack item;
    private MenuAction action;

    public MenuItem(ItemStack item) {
        this.item = item;
        this.action = null;
    }

    public MenuItem(ItemStack item, MenuAction action) {
        this.item = item;
        this.action = action;
    }

    public ItemStack getItem() {
        return item;
    }

    public MenuAction getAction() {
        return action;
    }

    public void setAction(MenuAction action) {
        this.action = action;
    }
} 