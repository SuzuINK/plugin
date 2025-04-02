package fr.akashisei.reveildeschasseurs.models;

import fr.akashisei.reveildeschasseurs.ReveilDesChasseurs;
import fr.akashisei.reveildeschasseurs.events.DungeonBossEvent;
import fr.akashisei.reveildeschasseurs.events.DungeonCompleteEvent;
import fr.akashisei.reveildeschasseurs.events.DungeonStartEvent;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class DungeonInstance {
    private final ReveilDesChasseurs plugin;
    private final Dungeon dungeon;
    private final UUID ownerId;
    private final Location spawnPoint;
    private final Location exitLocation;
    private final Set<UUID> players;
    private final Map<UUID, Integer> playerKills;
    private final Map<UUID, Integer> playerDeaths;
    private final long startTime;
    private boolean completed;
    private boolean bossSpawned;

    public DungeonInstance(ReveilDesChasseurs plugin, Dungeon dungeon, UUID ownerId, Location spawnPoint, Location exitLocation) {
        this.plugin = plugin;
        this.dungeon = dungeon;
        this.ownerId = ownerId;
        this.spawnPoint = spawnPoint;
        this.exitLocation = exitLocation;
        this.players = new HashSet<>();
        this.playerKills = new HashMap<>();
        this.playerDeaths = new HashMap<>();
        this.startTime = System.currentTimeMillis();
        this.completed = false;
        this.bossSpawned = false;
    }

    public void addPlayer(Player player) {
        players.add(player.getUniqueId());
        playerKills.put(player.getUniqueId(), 0);
        playerDeaths.put(player.getUniqueId(), 0);
        
        if (players.size() == 1) {
            // Premier joueur, déclencher l'événement de début
            List<Player> playerList = getPlayerList();
            DungeonStartEvent event = new DungeonStartEvent(this, dungeon, playerList);
            plugin.getServer().getPluginManager().callEvent(event);
        }
    }

    public void removePlayer(Player player) {
        players.remove(player.getUniqueId());
        playerKills.remove(player.getUniqueId());
        playerDeaths.remove(player.getUniqueId());
    }

    public boolean hasPlayer(Player player) {
        return players.contains(player.getUniqueId());
    }

    public Set<UUID> getPlayers() {
        return new HashSet<>(players);
    }

    public List<Player> getPlayerList() {
        return players.stream()
                .map(id -> plugin.getServer().getPlayer(id))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public Dungeon getDungeon() {
        return dungeon;
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public Location getSpawnPoint() {
        return spawnPoint;
    }

    public Location getExitLocation() {
        return exitLocation;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
        if (completed) {
            // Déclencher l'événement de fin
            List<Player> playerList = getPlayerList();
            Map<Player, Integer> kills = playerKills.entrySet().stream()
                    .collect(Collectors.toMap(
                            e -> plugin.getServer().getPlayer(e.getKey()),
                            Map.Entry::getValue
                    ));
            Map<Player, Integer> deaths = playerDeaths.entrySet().stream()
                    .collect(Collectors.toMap(
                            e -> plugin.getServer().getPlayer(e.getKey()),
                            Map.Entry::getValue
                    ));
            
            DungeonCompleteEvent event = new DungeonCompleteEvent(
                    this, dungeon, playerList,
                    System.currentTimeMillis() - startTime,
                    kills, deaths, true
            );
            plugin.getServer().getPluginManager().callEvent(event);
        }
    }

    public void incrementKills(Player player) {
        playerKills.merge(player.getUniqueId(), 1, Integer::sum);
    }

    public void incrementDeaths(Player player) {
        playerDeaths.merge(player.getUniqueId(), 1, Integer::sum);
    }

    public void spawnBoss(String bossName, int bossHealth) {
        if (!bossSpawned) {
            bossSpawned = true;
            List<Player> playerList = getPlayerList();
            DungeonBossEvent event = new DungeonBossEvent(this, dungeon, playerList, bossName, bossHealth);
            plugin.getServer().getPluginManager().callEvent(event);
        }
    }

    public int getPlayerKills(Player player) {
        return playerKills.getOrDefault(player.getUniqueId(), 0);
    }

    public int getPlayerDeaths(Player player) {
        return playerDeaths.getOrDefault(player.getUniqueId(), 0);
    }

    public long getStartTime() {
        return startTime;
    }
} 