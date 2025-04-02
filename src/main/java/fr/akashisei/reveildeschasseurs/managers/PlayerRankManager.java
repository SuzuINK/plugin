package fr.akashisei.reveildeschasseurs.managers;

import fr.akashisei.reveildeschasseurs.models.PlayerRank;
import org.bukkit.entity.Player;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerRankManager {
    private final Map<UUID, PlayerRank> playerRanks;

    public PlayerRankManager() {
        this.playerRanks = new HashMap<>();
    }

    public PlayerRank getPlayerRank(Player player) {
        return playerRanks.getOrDefault(player.getUniqueId(), PlayerRank.E);
    }

    public void setPlayerRank(Player player, PlayerRank rank) {
        playerRanks.put(player.getUniqueId(), rank);
        updatePlayerDisplay(player);
    }

    public boolean promotePlayer(Player player) {
        PlayerRank currentRank = getPlayerRank(player);
        if (currentRank == PlayerRank.S) {
            return false;
        }
        
        PlayerRank nextRank = PlayerRank.values()[currentRank.ordinal() + 1];
        setPlayerRank(player, nextRank);
        return true;
    }

    public boolean demotePlayer(Player player) {
        PlayerRank currentRank = getPlayerRank(player);
        if (currentRank == PlayerRank.E) {
            return false;
        }
        
        PlayerRank previousRank = PlayerRank.values()[currentRank.ordinal() - 1];
        setPlayerRank(player, previousRank);
        return true;
    }

    private void updatePlayerDisplay(Player player) {
        PlayerRank rank = getPlayerRank(player);
        String displayName = rank.getColor() + "[" + rank.name() + "] " + player.getName();
        player.setDisplayName(displayName);
        player.setPlayerListName(displayName);
    }

    public void loadPlayerData(Player player) {
        // TODO: Charger les données depuis la base de données
        setPlayerRank(player, PlayerRank.E);
    }

    public void savePlayerData(Player player) {
        // TODO: Sauvegarder les données dans la base de données
        PlayerRank rank = getPlayerRank(player);
        // Implémentation de la sauvegarde
    }

    public boolean hasRank(Player player, PlayerRank rank) {
        return getPlayerRank(player).getRequiredLevel() >= rank.getRequiredLevel();
    }

    public void addExperience(Player player, int amount) {
        PlayerRank currentRank = getPlayerRank(player);
        PlayerRank newRank = currentRank.addExperience(amount);
        if (newRank != currentRank) {
            setPlayerRank(player, newRank);
            player.sendMessage("§aFélicitations ! Vous avez atteint le rang " + newRank.getDisplayName() + " !");
        }
    }
} 