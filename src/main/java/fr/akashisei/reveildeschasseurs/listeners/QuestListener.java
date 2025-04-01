package fr.akashisei.reveildeschasseurs.listeners;

import fr.akashisei.reveildeschasseurs.ReveilDesChasseurs;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerQuitEvent;
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
        
        // Mettre à jour la progression des quêtes liées aux mobs tués
        for (String questId : plugin.getPlayerDataManager().getOrCreatePlayerData(player).getActiveQuests()) {
            plugin.getQuestManager().updateQuestProgress(player, questId, "KILL_" + entityType, 1);
        }
    }

    @EventHandler
    public void onEntityPickupItem(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        
        Player player = (Player) event.getEntity();
        ItemStack item = event.getItem().getItemStack();
        
        // Mettre à jour la progression des quêtes liées aux items ramassés
        for (String questId : plugin.getPlayerDataManager().getOrCreatePlayerData(player).getActiveQuests()) {
            plugin.getQuestManager().updateQuestProgress(player, questId, "COLLECT_" + item.getType().name(), item.getAmount());
        }
    }

    @EventHandler
    public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        
        // Mettre à jour la progression des quêtes liées aux items consommés
        for (String questId : plugin.getPlayerDataManager().getOrCreatePlayerData(player).getActiveQuests()) {
            plugin.getQuestManager().updateQuestProgress(player, questId, "CONSUME_" + item.getType().name(), 1);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        plugin.getPlayerDataManager().savePlayerData(player.getUniqueId());
    }
} 