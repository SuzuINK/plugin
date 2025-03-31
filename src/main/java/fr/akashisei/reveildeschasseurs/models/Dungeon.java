package fr.akashisei.reveildeschasseurs.models;

import fr.akashisei.reveildeschasseurs.dungeon.DungeonGenerator.DungeonTheme;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.*;

public class Dungeon {
    private final String id;
    private String name;
    private String description;
    private int minLevel;
    private int maxPlayers;
    private long timeLimit;
    private double difficultyMultiplier;
    private long baseRewardExp;
    private long baseRewardMoney;
    private final DungeonTheme theme;
    private List<String> requiredItems;
    private final Location entranceLocation;
    private World world;
    private Location spawnLocation;
    private Location exitLocation;
    private List<String> tags;
    private List<String> requiredQuests;
    private List<String> forbiddenItems;
    private List<String> allowedClasses;
    private List<String> allowedRanks;
    private final List<String> rewardItems;
    private final int minRooms;
    private final int maxRooms;

    public Dungeon(String id, String name, String description, int minLevel, DungeonTheme theme, int minRooms, int maxRooms) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.minLevel = minLevel;
        this.theme = theme;
        this.maxPlayers = 4;
        this.timeLimit = 3600;
        this.difficultyMultiplier = 1.0;
        this.baseRewardExp = minLevel * 100;
        this.baseRewardMoney = minLevel * 50;
        this.tags = new ArrayList<>();
        this.requiredQuests = new ArrayList<>();
        this.requiredItems = new ArrayList<>();
        this.forbiddenItems = new ArrayList<>();
        this.allowedClasses = new ArrayList<>();
        this.allowedRanks = new ArrayList<>();
        this.rewardItems = new ArrayList<>();
        this.entranceLocation = null;
        this.minRooms = minRooms;
        this.maxRooms = maxRooms;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getMinLevel() {
        return minLevel;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public long getTimeLimit() {
        return timeLimit;
    }

    public double getDifficultyMultiplier() {
        return difficultyMultiplier;
    }

    public long getBaseRewardExp() {
        return baseRewardExp;
    }

    public long getBaseRewardMoney() {
        return baseRewardMoney;
    }

    public DungeonTheme getTheme() {
        return theme;
    }

    public List<String> getRequiredItems() {
        return requiredItems;
    }

    public Location getEntranceLocation() {
        return entranceLocation;
    }

    public World getWorld() {
        return world;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    public Location getSpawnLocation() {
        return spawnLocation;
    }

    public void setSpawnLocation(Location spawnLocation) {
        this.spawnLocation = spawnLocation;
    }

    public Location getExitLocation() {
        return exitLocation;
    }

    public void setExitLocation(Location exitLocation) {
        this.exitLocation = exitLocation;
    }

    public void setTimeLimit(long timeLimit) {
        this.timeLimit = timeLimit;
    }

    public void setDifficultyMultiplier(double difficultyMultiplier) {
        this.difficultyMultiplier = difficultyMultiplier;
    }

    public void setBaseRewardExp(long baseRewardExp) {
        this.baseRewardExp = baseRewardExp;
    }

    public void setBaseRewardMoney(long baseRewardMoney) {
        this.baseRewardMoney = baseRewardMoney;
    }

    public List<String> getTags() {
        return tags;
    }

    public void addTag(String tag) {
        if (!tags.contains(tag)) {
            tags.add(tag);
        }
    }

    public void removeTag(String tag) {
        tags.remove(tag);
    }

    public boolean hasTag(String tag) {
        return tags.contains(tag);
    }

    public List<String> getRequiredQuests() {
        return requiredQuests;
    }

    public void addRequiredQuest(String questId) {
        if (!requiredQuests.contains(questId)) {
            requiredQuests.add(questId);
        }
    }

    public void removeRequiredQuest(String questId) {
        requiredQuests.remove(questId);
    }

    public boolean hasRequiredQuest(String questId) {
        return requiredQuests.contains(questId);
    }

    public List<String> getForbiddenItems() {
        return forbiddenItems;
    }

    public void addForbiddenItem(String itemId) {
        if (!forbiddenItems.contains(itemId)) {
            forbiddenItems.add(itemId);
        }
    }

    public void removeForbiddenItem(String itemId) {
        forbiddenItems.remove(itemId);
    }

    public boolean hasForbiddenItem(String itemId) {
        return forbiddenItems.contains(itemId);
    }

    public List<String> getAllowedClasses() {
        return allowedClasses;
    }

    public void addAllowedClass(String className) {
        if (!allowedClasses.contains(className)) {
            allowedClasses.add(className);
        }
    }

    public void removeAllowedClass(String className) {
        allowedClasses.remove(className);
    }

    public boolean hasAllowedClass(String className) {
        return allowedClasses.contains(className);
    }

    public List<String> getAllowedRanks() {
        return allowedRanks;
    }

    public void addAllowedRank(String rank) {
        if (!allowedRanks.contains(rank)) {
            allowedRanks.add(rank);
        }
    }

    public void removeAllowedRank(String rank) {
        allowedRanks.remove(rank);
    }

    public boolean hasAllowedRank(String rank) {
        return allowedRanks.contains(rank);
    }

    public long calculateRewardExp() {
        return (long) (baseRewardExp * difficultyMultiplier);
    }

    public long calculateRewardMoney() {
        return (long) (baseRewardMoney * difficultyMultiplier);
    }

    public List<String> getRewardItems() {
        return rewardItems;
    }

    public void addRewardItem(String itemId) {
        if (!rewardItems.contains(itemId)) {
            rewardItems.add(itemId);
        }
    }

    public void removeRewardItem(String itemId) {
        rewardItems.remove(itemId);
    }

    public boolean hasRewardItem(String itemId) {
        return rewardItems.contains(itemId);
    }

    public void clearRewardItems() {
        rewardItems.clear();
    }

    public boolean canPlayerEnter(PlayerData playerData) {
        return playerData.getLevel() >= minLevel;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setMinLevel(int minLevel) {
        this.minLevel = minLevel;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public void setTags(List<String> tags) {
        this.tags = tags != null ? tags : new ArrayList<>();
    }

    public void setRequiredQuests(List<String> requiredQuests) {
        this.requiredQuests = requiredQuests != null ? requiredQuests : new ArrayList<>();
    }

    public void setRequiredItems(List<String> requiredItems) {
        this.requiredItems = requiredItems != null ? requiredItems : new ArrayList<>();
    }

    public void setForbiddenItems(List<String> forbiddenItems) {
        this.forbiddenItems = forbiddenItems != null ? forbiddenItems : new ArrayList<>();
    }

    public void setAllowedClasses(List<String> allowedClasses) {
        this.allowedClasses = allowedClasses != null ? allowedClasses : new ArrayList<>();
    }

    public void setAllowedRanks(List<String> allowedRanks) {
        this.allowedRanks = allowedRanks != null ? allowedRanks : new ArrayList<>();
    }

    public int getMinRooms() {
        return minRooms;
    }

    public int getMaxRooms() {
        return maxRooms;
    }
} 