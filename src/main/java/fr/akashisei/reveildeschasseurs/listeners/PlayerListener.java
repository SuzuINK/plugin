package fr.akashisei.reveildeschasseurs.listeners;

import fr.akashisei.reveildeschasseurs.ReveilDesChasseurs;
import fr.akashisei.reveildeschasseurs.models.PlayerData;
import org.bukkit.ChatColor;
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
        PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(player.getUniqueId());

        // Message de bienvenue personnalisé
        player.sendMessage(ChatColor.GOLD + "Bienvenue sur ReveilDesChasseurs!");
        player.sendMessage(ChatColor.WHITE + "Votre classe : " + ChatColor.GREEN + playerData.getPlayerClass());
        player.sendMessage(ChatColor.WHITE + "Votre rang : " + ChatColor.GREEN + playerData.getRank());
        player.sendMessage(ChatColor.WHITE + "Niveau : " + ChatColor.GREEN + playerData.getLevel());
        player.sendMessage(ChatColor.WHITE + "XP : " + ChatColor.GREEN + playerData.getExperience());
        player.sendMessage(ChatColor.WHITE + "Argent : " + ChatColor.GREEN + playerData.getMoney() + " coins");
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        
        // Sauvegarder les données du joueur
        plugin.getPlayerDataManager().savePlayerData(player);
        
        // Vérifier si le joueur est dans un donjon
        if (plugin.getDungeonManager().isPlayerInDungeon(player)) {
            plugin.getDungeonManager().removePlayerFromDungeon(player);
        }
    }
} 