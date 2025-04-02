package fr.akashisei.reveildeschasseurs.listeners;

import fr.akashisei.reveildeschasseurs.ReveilDesChasseurs;
import fr.akashisei.reveildeschasseurs.quests.QuestType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

public class QuestListener implements Listener {
    private final ReveilDesChasseurs plugin;

    public QuestListener(ReveilDesChasseurs plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity().getKiller() == null) return;
        
        Player player = event.getEntity().getKiller();
        String entityType = event.getEntityType().name();
        
        plugin.getQuestManager().updateQuestProgress(player, QuestType.KILL, entityType, 1);
    }

    @EventHandler
    public void onItemPickup(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        
        Player player = (Player) event.getEntity();
        ItemStack item = event.getItem().getItemStack();
        String itemType = item.getType().name();
        
        plugin.getQuestManager().updateQuestProgress(player, QuestType.COLLECT, itemType, item.getAmount());
    }

    @EventHandler
    public void onItemConsume(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        String itemType = item.getType().name();
        
        plugin.getQuestManager().updateQuestProgress(player, QuestType.CONSUME, itemType, 1);
    }

    @EventHandler
    public void onPlayerFish(PlayerFishEvent event) {
        if (event.getState() != PlayerFishEvent.State.CAUGHT_FISH) return;
        if (!(event.getCaught() instanceof org.bukkit.entity.Item)) return;
        
        Player player = event.getPlayer();
        ItemStack fish = ((org.bukkit.entity.Item) event.getCaught()).getItemStack();
        String fishType = fish.getType().name();
        
        plugin.getQuestManager().updateQuestProgress(player, QuestType.FISH, fishType, 1);
    }

    @EventHandler
    public void onCraftItem(CraftItemEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        
        Player player = (Player) event.getWhoClicked();
        ItemStack craftedItem = event.getCurrentItem();
        String itemType = craftedItem.getType().name();
        
        plugin.getQuestManager().updateQuestProgress(player, QuestType.CRAFT, itemType, craftedItem.getAmount());
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        if (event.getInventory().getType() != InventoryType.MERCHANT) return;
        
        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null) return;
        
        String itemType = clickedItem.getType().name();
        plugin.getQuestManager().updateQuestProgress(player, QuestType.TRADE, itemType, 1);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        plugin.getQuestManager().savePlayerData(player);
    }
} 