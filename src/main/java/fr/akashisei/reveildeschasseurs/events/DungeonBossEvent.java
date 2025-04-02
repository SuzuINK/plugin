package fr.akashisei.reveildeschasseurs.events;

import fr.akashisei.reveildeschasseurs.models.Dungeon;
import fr.akashisei.reveildeschasseurs.models.DungeonInstance;
import org.bukkit.entity.Player;

import java.util.List;

public class DungeonBossEvent extends DungeonEvent {
    private final String bossName;
    private final int bossHealth;

    public DungeonBossEvent(DungeonInstance instance, Dungeon dungeon, List<Player> players, String bossName, int bossHealth) {
        super(instance, dungeon, players);
        this.bossName = bossName;
        this.bossHealth = bossHealth;
    }

    public String getBossName() {
        return bossName;
    }

    public int getBossHealth() {
        return bossHealth;
    }
} 