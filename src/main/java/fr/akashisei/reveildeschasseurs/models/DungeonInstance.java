package fr.akashisei.reveildeschasseurs.models;

import org.bukkit.Location;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class DungeonInstance {
    private final Dungeon dungeon;
    private final UUID ownerId;
    private final Location spawnPoint;
    private final Location exitLocation;
    private final Set<UUID> players;
    private boolean completed;

    public DungeonInstance(Dungeon dungeon, UUID ownerId, Location spawnPoint, Location exitLocation) {
        this.dungeon = dungeon;
        this.ownerId = ownerId;
        this.spawnPoint = spawnPoint;
        this.exitLocation = exitLocation;
        this.players = new HashSet<>();
        this.completed = false;
    }

    public void addPlayer(org.bukkit.entity.Player player) {
        players.add(player.getUniqueId());
    }

    public void removePlayer(org.bukkit.entity.Player player) {
        players.remove(player.getUniqueId());
    }

    public boolean hasPlayer(org.bukkit.entity.Player player) {
        return players.contains(player.getUniqueId());
    }

    public Set<UUID> getPlayers() {
        return new HashSet<>(players);
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
    }
} 