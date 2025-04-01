package fr.akashisei.reveildeschasseurs.menus;

import org.bukkit.entity.Player;

@FunctionalInterface
public interface MenuAction {
    void execute(Player player);
} 