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
    private DungeonTheme theme;
    private List<String> requiredItems;
    private Location entranceLocation;
    private World world;
    private Location spawnLocation;
    private Location exitLocation;
    private List<String> tags;
    private List<String> requiredQuests;
    private List<String> forbiddenItems;
    private List<String> allowedClasses;
    private List<String> allowedRanks;
    private List<String> rewardItems;
    private final int minRooms;
    private final int maxRooms;
    private int size;
    private double mobDensity;
    private double trapDensity;
    private double lootDensity;

    public Dungeon(String id) {
        this.id = id;
        this.name = "Nouveau donjon";
        this.description = "Description par défaut";
        this.minLevel = 1;
        this.maxPlayers = 4;
        this.timeLimit = 3600;
        this.difficultyMultiplier = 1.0;
        this.baseRewardExp = 100;
        this.baseRewardMoney = 50;
        this.rewardItems = new ArrayList<>();
        this.requiredQuests = new ArrayList<>();
        this.requiredItems = new ArrayList<>();
        this.forbiddenItems = new ArrayList<>();
        this.allowedClasses = new ArrayList<>();
        this.allowedRanks = new ArrayList<>();
        this.theme = DungeonTheme.MEDIEVAL;
        this.minRooms = 3;
        this.maxRooms = 5;
        this.size = 3;
        this.mobDensity = 0.5;
        this.trapDensity = 0.3;
        this.lootDensity = 0.4;
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

    public List<String> getRewardItems() {
        return rewardItems;
    }

    public List<String> getRequiredQuests() {
        return requiredQuests;
    }

    public List<String> getRequiredItems() {
        return requiredItems;
    }

    public List<String> getForbiddenItems() {
        return forbiddenItems;
    }

    public List<String> getAllowedClasses() {
        return allowedClasses;
    }

    public List<String> getAllowedRanks() {
        return allowedRanks;
    }

    public DungeonTheme getTheme() {
        return theme;
    }

    public void setTheme(DungeonTheme theme) {
        this.theme = theme;
    }

    public int getSize() {
        return size;
    }

    public double getMobDensity() {
        return mobDensity;
    }

    public double getTrapDensity() {
        return trapDensity;
    }

    public double getLootDensity() {
        return lootDensity;
    }

    public Location getSpawnLocation() {
        return spawnLocation;
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

    public void setRewardItems(List<String> rewardItems) {
        this.rewardItems = rewardItems;
    }

    public void setRequiredQuests(List<String> requiredQuests) {
        this.requiredQuests = requiredQuests;
    }

    public void setRequiredItems(List<String> requiredItems) {
        this.requiredItems = requiredItems;
    }

    public void setForbiddenItems(List<String> forbiddenItems) {
        this.forbiddenItems = forbiddenItems;
    }

    public void setAllowedClasses(List<String> allowedClasses) {
        this.allowedClasses = allowedClasses;
    }

    public void setAllowedRanks(List<String> allowedRanks) {
        this.allowedRanks = allowedRanks;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setMobDensity(double mobDensity) {
        this.mobDensity = mobDensity;
    }

    public void setTrapDensity(double trapDensity) {
        this.trapDensity = trapDensity;
    }

    public void setLootDensity(double lootDensity) {
        this.lootDensity = lootDensity;
    }

    public void setSpawnLocation(Location spawnLocation) {
        this.spawnLocation = spawnLocation;
    }

    public World getWorld() {
        return world;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    public Location getEntranceLocation() {
        return entranceLocation;
    }

    public void setEntranceLocation(Location entranceLocation) {
        this.entranceLocation = entranceLocation;
    }

    public Location getExitLocation() {
        return exitLocation;
    }

    public void setExitLocation(Location exitLocation) {
        this.exitLocation = exitLocation;
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

    public void setTags(List<String> tags) {
        this.tags = tags != null ? tags : new ArrayList<>();
    }

    public List<String> getTags() {
        return tags;
    }

    public int getMinRooms() {
        return minRooms;
    }

    public int getMaxRooms() {
        return maxRooms;
    }
} 