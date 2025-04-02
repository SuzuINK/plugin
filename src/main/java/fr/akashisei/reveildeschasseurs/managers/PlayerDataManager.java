package fr.akashisei.reveildeschasseurs.managers;

import fr.akashisei.reveildeschasseurs.ReveilDesChasseurs;
import fr.akashisei.reveildeschasseurs.models.PlayerData;
import org.bukkit.entity.Player;

import java.util.*;

public class PlayerDataManager {
    private final ReveilDesChasseurs plugin;
    private final Map<UUID, PlayerData> playerDataMap;

    public PlayerDataManager(ReveilDesChasseurs plugin) {
        this.plugin = plugin;
        this.playerDataMap = new HashMap<>();
        loadAllPlayerData();
    }

    public void loadAllPlayerData() {
        List<String> playerIds = plugin.getConfigManager().getPlayerIds();
        for (String playerIdStr : playerIds) {
            try {
                UUID playerId = UUID.fromString(playerIdStr);
                PlayerData data = plugin.getConfigManager().loadPlayerData(playerId);
                if (data != null) {
                    playerDataMap.put(playerId, data);
                }
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("ID de joueur invalide : " + playerIdStr);
            }
        }
    }

    public void saveAllPlayerData() {
        for (PlayerData data : playerDataMap.values()) {
            savePlayerData(data);
        }
    }

    public PlayerData getPlayerData(UUID playerId) {
        return playerDataMap.get(playerId);
    }

    public PlayerData getPlayerData(Player player) {
        return getPlayerData(player.getUniqueId());
    }

    public PlayerData getOrCreatePlayerData(Player player) {
        PlayerData data = getPlayerData(player);
        if (data == null) {
            data = new PlayerData(player.getUniqueId(), player.getName());
            playerDataMap.put(player.getUniqueId(), data);
            savePlayerData(data);
        }
        return data;
    }

    public void savePlayerData(UUID playerId) {
        PlayerData data = getPlayerData(playerId);
        if (data != null) {
            savePlayerData(data);
        }
    }

    public void savePlayerData(Player player) {
        PlayerData data = getPlayerData(player);
        if (data != null) {
            savePlayerData(data);
        }
    }

    public void savePlayerData(PlayerData data) {
        plugin.getConfigManager().savePlayerData(data);
        playerDataMap.put(data.getPlayerId(), data);
    }

    public void removePlayerData(Player player) {
        PlayerData data = getPlayerData(player);
        if (data != null) {
            playerDataMap.remove(player.getUniqueId());
            plugin.getConfigManager().deletePlayerData(player.getUniqueId());
        }
    }

    public boolean hasPlayerData(Player player) {
        return playerDataMap.containsKey(player.getUniqueId());
    }

    public void saveAll() {
        saveAllPlayerData();
    }
} 