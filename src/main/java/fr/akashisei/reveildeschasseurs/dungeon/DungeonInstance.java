package fr.akashisei.reveildeschasseurs.dungeon;

import fr.akashisei.reveildeschasseurs.models.Dungeon;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class DungeonInstance {
    private final UUID playerId;
    private final Dungeon dungeon;
    private final Set<Player> players;
    private final Location spawnLocation;
    private final Location exitLocation;
    private final World world;
    private DungeonState state;
    private final long startTime;
    private boolean completed;

    public DungeonInstance(Dungeon dungeon, UUID playerId, Location spawnLocation, Location exitLocation) {
        this.dungeon = dungeon;
        this.playerId = playerId;
        this.players = new HashSet<>();
        this.spawnLocation = spawnLocation;
        this.exitLocation = exitLocation;
        this.world = spawnLocation.getWorld();
        this.state = DungeonState.WAITING;
        this.startTime = System.currentTimeMillis();
        this.completed = false;
    }

    // Nouveau constructeur pour DungeonBuilder
    public DungeonInstance(Dungeon dungeon, Player player, Location spawnLocation) {
        this(dungeon, player.getUniqueId(), spawnLocation, null);
        this.players.add(player);
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public Dungeon getDungeon() {
        return dungeon;
    }

    public Location getSpawnLocation() {
        return spawnLocation.clone();
    }

    public Location getExitLocation() {
        return exitLocation.clone();
    }

    public World getWorld() {
        return world;
    }

    public DungeonState getState() {
        return state;
    }

    public void setState(DungeonState state) {
        this.state = state;
    }

    public Set<Player> getPlayers() {
        return new HashSet<>(players);
    }

    public void addPlayer(Player player) {
        players.add(player);
    }

    public void removePlayer(Player player) {
        players.remove(player);
    }

    public boolean hasPlayer(Player player) {
        return players.contains(player);
    }

    public long getStartTime() {
        return startTime;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public boolean isTimeExpired() {
        long currentTime = System.currentTimeMillis();
        long timeLimit = dungeon.getTimeLimit() * 60 * 1000; // Convertir minutes en millisecondes
        return (currentTime - startTime) > timeLimit;
    }

    public long getRemainingTime() {
        long currentTime = System.currentTimeMillis();
        long timeLimit = dungeon.getTimeLimit() * 60 * 1000;
        long remainingTime = timeLimit - (currentTime - startTime);
        return Math.max(0, remainingTime);
    }

    public Player getPlayer() {
        return players.isEmpty() ? null : players.iterator().next();
    }

    public Location getSpawnPoint() {
        return getSpawnLocation();
    }

    public Location getExitPoint() {
        return getExitLocation();
    }

    public enum DungeonState {
        WAITING,
        IN_PROGRESS,
        COMPLETED,
        FAILED
    }
}