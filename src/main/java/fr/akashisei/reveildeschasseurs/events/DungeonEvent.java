package fr.akashisei.reveildeschasseurs.events;

import fr.akashisei.reveildeschasseurs.models.Dungeon;
import fr.akashisei.reveildeschasseurs.models.DungeonInstance;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.List;

public class DungeonEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final DungeonInstance instance;
    private final Dungeon dungeon;
    private final List<Player> players;

    public DungeonEvent(DungeonInstance instance, Dungeon dungeon, List<Player> players) {
        this.instance = instance;
        this.dungeon = dungeon;
        this.players = players;
    }

    public DungeonInstance getInstance() {
        return instance;
    }

    public Dungeon getDungeon() {
        return dungeon;
    }

    public List<Player> getPlayers() {
        return players;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
} 