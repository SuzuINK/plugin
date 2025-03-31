package fr.akashisei.reveildeschasseurs.menu;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import fr.akashisei.reveildeschasseurs.ReveilDesChasseurs;
import fr.akashisei.reveildeschasseurs.models.Quest;

import java.util.List;

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
        // Implementation of initializeItems method
    }
} 