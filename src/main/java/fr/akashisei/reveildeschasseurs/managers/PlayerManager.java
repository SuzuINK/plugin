package fr.akashisei.reveildeschasseurs.managers;

import fr.akashisei.reveildeschasseurs.ReveilDesChasseurs;
import fr.akashisei.reveildeschasseurs.models.PlayerData;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class PlayerManager {
    private final ReveilDesChasseurs plugin;
    private final Map<UUID, PlayerData> playerDataMap;
    private final File playerDataFolder;

    public PlayerManager(ReveilDesChasseurs plugin) {
        this.plugin = plugin;
        this.playerDataMap = new HashMap<>();
        this.playerDataFolder = new File(plugin.getDataFolder(), "players");
        if (!playerDataFolder.exists()) {
            playerDataFolder.mkdirs();
        }
    }

    public PlayerData getPlayerData(Player player) {
        return playerDataMap.computeIfAbsent(player.getUniqueId(), uuid -> {
            File playerFile = new File(playerDataFolder, uuid.toString() + ".yml");
            if (!playerFile.exists()) {
                return createNewPlayerData(player);
            }
            return loadPlayerData(playerFile);
        });
    }

    private PlayerData createNewPlayerData(Player player) {
        PlayerData data = new PlayerData(player.getUniqueId(), player.getName());
        data.setRank("E");
        data.setLevel(1);
        data.setExperience(0);
        data.setCompletedDungeons(0);
        data.setTotalCompletedQuests(0);
        savePlayerData(data);
        return data;
    }

    private PlayerData loadPlayerData(File file) {
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        PlayerData data = new PlayerData(
            UUID.fromString(file.getName().replace(".yml", "")),
            config.getString("name", "Unknown")
        );
        data.setRank(config.getString("rank", "E"));
        data.setLevel(config.getInt("level", 1));
        data.setExperience(config.getInt("experience", 0));
        data.setCompletedDungeons(config.getInt("completed_dungeons", 0));
        data.setTotalCompletedQuests(config.getInt("completed_quests", 0));
        return data;
    }

    public void savePlayerData(PlayerData data) {
        File playerFile = new File(playerDataFolder, data.getPlayerId().toString() + ".yml");
        FileConfiguration config = new YamlConfiguration();
        
        config.set("name", data.getPlayerName());
        config.set("rank", data.getRank());
        config.set("level", data.getLevel());
        config.set("experience", data.getExperience());
        config.set("completed_dungeons", data.getCompletedDungeons());
        config.set("completed_quests", data.getTotalCompletedQuests());

        try {
            config.save(playerFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Impossible de sauvegarder les données du joueur " + data.getPlayerName());
            e.printStackTrace();
        }
    }

    public void saveAll() {
        playerDataMap.values().forEach(this::savePlayerData);
    }

    public void removePlayerData(UUID uuid) {
        playerDataMap.remove(uuid);
    }

    public void addExperience(Player player, long amount) {
        PlayerData data = getPlayerData(player);
        data.addExperience((int) amount);
        checkRankUp(data);
        savePlayerData(data);
    }

    private void checkRankUp(PlayerData data) {
        String currentRank = data.getRank();
        String newRank = calculateRank(data.getExperience());
        
        if (!currentRank.equals(newRank)) {
            data.setRank(newRank);
            // Notifier le joueur du changement de rang
            plugin.getServer().getPlayer(data.getPlayerId()).sendMessage(
                plugin.getConfigManager().getConfig().getString("messages.rank_up")
                    .replace("%rank%", newRank)
            );
        }
    }

    private String calculateRank(long experience) {
        FileConfiguration config = plugin.getConfigManager().getConfig();
        if (experience >= config.getLong("ranks.S")) return "S";
        if (experience >= config.getLong("ranks.A")) return "A";
        if (experience >= config.getLong("ranks.B")) return "B";
        if (experience >= config.getLong("ranks.C")) return "C";
        if (experience >= config.getLong("ranks.D")) return "D";
        return "E";
    }
} 