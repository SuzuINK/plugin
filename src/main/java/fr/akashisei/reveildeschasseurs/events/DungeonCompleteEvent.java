package fr.akashisei.reveildeschasseurs.events;

import fr.akashisei.reveildeschasseurs.models.Dungeon;
import fr.akashisei.reveildeschasseurs.models.DungeonInstance;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

public class DungeonCompleteEvent extends DungeonEvent {
    private final long completionTime;
    private final Map<Player, Integer> playerKills;
    private final Map<Player, Integer> playerDeaths;
    private final boolean isSuccess;

    public DungeonCompleteEvent(DungeonInstance instance, Dungeon dungeon, List<Player> players, 
                              long completionTime, Map<Player, Integer> playerKills, 
                              Map<Player, Integer> playerDeaths, boolean isSuccess) {
        super(instance, dungeon, players);
        this.completionTime = completionTime;
        this.playerKills = playerKills;
        this.playerDeaths = playerDeaths;
        this.isSuccess = isSuccess;
    }

    public long getCompletionTime() {
        return completionTime;
    }

    public Map<Player, Integer> getPlayerKills() {
        return playerKills;
    }

    public Map<Player, Integer> getPlayerDeaths() {
        return playerDeaths;
    }

    public boolean isSuccess() {
        return isSuccess;
    }
} 