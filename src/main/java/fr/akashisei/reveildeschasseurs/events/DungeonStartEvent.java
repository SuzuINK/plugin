package fr.akashisei.reveildeschasseurs.events;

import fr.akashisei.reveildeschasseurs.models.Dungeon;
import fr.akashisei.reveildeschasseurs.models.DungeonInstance;
import org.bukkit.entity.Player;

import java.util.List;

public class DungeonStartEvent extends DungeonEvent {
    public DungeonStartEvent(DungeonInstance instance, Dungeon dungeon, List<Player> players) {
        super(instance, dungeon, players);
    }
} 