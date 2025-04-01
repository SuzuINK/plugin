package fr.akashisei.reveildeschasseurs.menu;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import fr.akashisei.reveildeschasseurs.ReveilDesChasseurs;
import fr.akashisei.reveildeschasseurs.models.Dungeon;

import java.util.List;

public class DungeonMenu {

    private ReveilDesChasseurs plugin;
    private Player player;
    private List<Dungeon> dungeons;
    private Inventory inventory;

    public DungeonMenu(ReveilDesChasseurs plugin) {
        this.plugin = plugin;
        this.inventory = Bukkit.createInventory(null, 54, "§6Donjons");
        initializeItems();
    }

    public DungeonMenu(Player player, List<Dungeon> dungeons) {
        this.player = player;
        this.dungeons = dungeons;
        this.inventory = Bukkit.createInventory(null, 54, "§6Donjons");
        initializeItems();
    }

    private void initializeItems() {
        // Implementation of initializeItems method
    }
} 