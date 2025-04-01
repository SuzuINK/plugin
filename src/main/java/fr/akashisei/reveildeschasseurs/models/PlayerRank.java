package fr.akashisei.reveildeschasseurs.models;

import org.bukkit.ChatColor;
import java.util.Arrays;
import java.util.List;

public enum PlayerRank {
    E("§7", "Rang E", 0, 0, 1.0),
    D("§f", "Rang D", 1000, 1, 1.2),
    C("§a", "Rang C", 5000, 2, 1.5),
    B("§b", "Rang B", 10000, 3, 1.8),
    A("§6", "Rang A", 25000, 4, 2.0),
    S("§c", "Rang S", 50000, 5, 2.5);

    private final String color;
    private final String displayName;
    private final int requiredPoints;
    private final int requiredLevel;
    private final double rewardMultiplier;
    private int experience;

    PlayerRank(String color, String displayName, int requiredPoints, int requiredLevel, double rewardMultiplier) {
        this.color = color;
        this.displayName = displayName;
        this.requiredPoints = requiredPoints;
        this.requiredLevel = requiredLevel;
        this.rewardMultiplier = rewardMultiplier;
        this.experience = 0;
    }

    public String getColor() {
        return color;
    }

    public String getDisplayName() {
        return color + displayName;
    }

    public int getRequiredPoints() {
        return requiredPoints;
    }

    public int getRequiredLevel() {
        return requiredLevel;
    }

    public double getRewardMultiplier() {
        return rewardMultiplier;
    }

    public PlayerRank addExperience(int amount) {
        this.experience += amount;
        
        // Check if player can rank up
        PlayerRank[] ranks = PlayerRank.values();
        for (int i = ranks.length - 1; i >= 0; i--) {
            PlayerRank rank = ranks[i];
            if (this.experience >= rank.getRequiredPoints() && rank.ordinal() > this.ordinal()) {
                return rank;
            }
        }
        return this;
    }

    public int getExperience() {
        return experience;
    }

    public static PlayerRank fromPoints(int points) {
        PlayerRank highestPossible = E;
        for (PlayerRank rank : values()) {
            if (points >= rank.getRequiredPoints()) {
                highestPossible = rank;
            }
        }
        return highestPossible;
    }
} 