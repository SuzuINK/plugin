package fr.akashisei.reveildeschasseurs.listeners;

import fr.akashisei.reveildeschasseurs.ReveilDesChasseurs;
import fr.akashisei.reveildeschasseurs.menus.QuestMenu;
import fr.akashisei.reveildeschasseurs.quests.Quest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.List;

public class QuestMenuListener implements Listener {
    private final ReveilDesChasseurs plugin;
    private QuestMenu questMenu;

    public QuestMenuListener(ReveilDesChasseurs plugin) {
        this.plugin = plugin;
    }

    public void openQuestMenu(Player player) {
        List<Quest> quests = plugin.getQuestManager().getAvailableQuests(player);
        questMenu = new QuestMenu(plugin, player, quests);
        questMenu.open(player);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();
        String title = event.getView().getTitle();

        if (title.equals("§6Menu des Quêtes")) {
            event.setCancelled(true);
            handleMainMenuClick(player, event.getSlot());
        } else if (title.equals("§6Quêtes Actives")) {
            event.setCancelled(true);
            handleActiveQuestsClick(player, event.getSlot());
        } else if (title.equals("§6Quêtes Disponibles")) {
            event.setCancelled(true);
            handleAvailableQuestsClick(player, event.getSlot());
        }
    }

    private void handleMainMenuClick(Player player, int slot) {
        switch (slot) {
            case 11:
                questMenu.openActiveQuests(player);
                break;
            case 13:
                questMenu.openAvailableQuests(player);
                break;
            case 15:
                questMenu.openCompletedQuests(player);
                break;
        }
    }

    private void handleActiveQuestsClick(Player player, int slot) {
        List<String> activeQuestIds = plugin.getQuestManager().getActiveQuests(player);
        if (slot < 0 || slot >= activeQuestIds.size()) return;
        
        String questId = activeQuestIds.get(slot);
        Quest quest = plugin.getQuestManager().getQuest(questId);
        if (quest != null) {
            plugin.getQuestManager().abandonQuest(player, quest.getId());
            questMenu.openActiveQuests(player);
        }
    }

    private void handleAvailableQuestsClick(Player player, int slot) {
        List<Quest> availableQuests = plugin.getQuestManager().getAvailableQuests(player);
        if (slot < 0 || slot >= availableQuests.size()) return;
        
        Quest quest = availableQuests.get(slot);
        if (quest != null) {
            plugin.getQuestManager().acceptQuest(player, quest.getId());
            questMenu.openAvailableQuests(player);
        }
    }
} 