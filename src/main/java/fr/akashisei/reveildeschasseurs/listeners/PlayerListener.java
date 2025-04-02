package fr.akashisei.reveildeschasseurs.listeners;

import fr.akashisei.reveildeschasseurs.ReveilDesChasseurs;
import fr.akashisei.reveildeschasseurs.models.PlayerRank;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {
    private final ReveilDesChasseurs plugin;

    public PlayerListener(ReveilDesChasseurs plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        
        // Charger les données du joueur
        plugin.getRankManager().loadPlayerData(player);
        plugin.getEconomyManager().loadPlayerData(player);
        
        // Message de bienvenue
        PlayerRank rank = plugin.getRankManager().getPlayerRank(player);
        player.sendMessage("§6=== Bienvenue dans ReveilDesChasseurs ===\n" +
                        "§7Votre rang: " + rank.getDisplayName() + "\n" +
                        "§7Utilisez /help pour voir les commandes disponibles");
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        
        // Sauvegarder les données du joueur
        plugin.getRankManager().savePlayerData(player);
        plugin.getEconomyManager().savePlayerData(player);
    }
} 