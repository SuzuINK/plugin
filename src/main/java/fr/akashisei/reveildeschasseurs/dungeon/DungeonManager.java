package fr.akashisei.reveildeschasseurs.dungeon;

import fr.akashisei.reveildeschasseurs.ReveilDesChasseurs;
import fr.akashisei.reveildeschasseurs.models.Dungeon;
import org.bukkit.entity.Player;

import java.util.*;

public class DungeonManager {
    private final ReveilDesChasseurs plugin;
    private final Map<String, Dungeon> dungeons;
    private final Map<UUID, DungeonInstance> activeDungeons;
    private final DungeonGenerator dungeonGenerator;

    public DungeonManager(ReveilDesChasseurs plugin) {
        this.plugin = plugin;
        this.dungeons = new HashMap<>();
        this.activeDungeons = new HashMap<>();
        this.dungeonGenerator = new DungeonGenerator(plugin);
        loadDungeons();
    }

    public void loadDungeons() {
        // TODO: Charger les donjons depuis la configuration
    }

    public void startDungeon(Player player, String dungeonName) {
        if (activeDungeons.containsKey(player.getUniqueId())) {
            player.sendMessage("§cVous êtes déjà dans un donjon !");
            return;
        }

        Dungeon dungeon = getDungeonByName(dungeonName);
        if (dungeon == null) {
            player.sendMessage("§cCe donjon n'existe pas !");
            return;
        }

        DungeonInstance instance = dungeonGenerator.createDungeonInstance(dungeon, player);
        activeDungeons.put(player.getUniqueId(), instance);
        instance.addPlayer(player);
        player.teleport(instance.getSpawnPoint());
        player.sendMessage("§aVous avez rejoint le donjon " + dungeon.getName() + " !");
    }

    public void leaveDungeon(Player player) {
        DungeonInstance instance = activeDungeons.get(player.getUniqueId());
        if (instance == null) {
            player.sendMessage("§cVous n'êtes pas dans un donjon !");
            return;
        }

        player.teleport(instance.getExitPoint());
        activeDungeons.remove(player.getUniqueId());
        player.sendMessage("§aVous avez quitté le donjon !");
    }

    public List<Dungeon> getAvailableDungeons(Player player) {
        List<Dungeon> availableDungeons = new ArrayList<>();
        int playerLevel = plugin.getPlayerDataManager().getPlayerData(player.getUniqueId()).getLevel();
        for (Dungeon dungeon : dungeons.values()) {
            if (dungeon.getMinLevel() <= playerLevel) {
                availableDungeons.add(dungeon);
            }
        }
        return availableDungeons;
    }

    public Dungeon getDungeonByName(String name) {
        for (Dungeon dungeon : dungeons.values()) {
            if (dungeon.getName().equalsIgnoreCase(name)) {
                return dungeon;
            }
        }
        return null;
    }

    public DungeonInstance getPlayerDungeon(UUID playerId) {
        return activeDungeons.get(playerId);
    }

    public boolean isInDungeon(UUID playerId) {
        return activeDungeons.containsKey(playerId);
    }

    public DungeonInstance getPlayerInstance(Player player) {
        return activeDungeons.get(player.getUniqueId());
    }

    public void removePlayerFromDungeon(Player player) {
        activeDungeons.remove(player.getUniqueId());
    }
}