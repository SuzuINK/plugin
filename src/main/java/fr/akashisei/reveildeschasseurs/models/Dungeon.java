package fr.akashisei.reveildeschasseurs.models;

import fr.akashisei.reveildeschasseurs.ReveilDesChasseurs;
import fr.akashisei.reveildeschasseurs.models.DungeonTheme;
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
    private PlayerRank minimumRank;
    private List<String> requiredItems;
    private List<String> forbiddenItems;
    private List<String> allowedClasses;
    private List<String> allowedRanks;
    private List<String> rewardItems;
    private Location entranceLocation;
    private World world;
    private Location spawnLocation;
    private Location exitLocation;
    private List<String> tags;
    private List<String> requiredQuests;
    private final int minRooms;
    private final int maxRooms;
    private int size;
    private double mobDensity;
    private double lootDensity;
    private double trapDensity;
    private String bossMobId;
    private Map<String, Integer> mobSpawns;
    private boolean isGateOpen;
    private boolean isGateCleared;
    private int requiredPlayerCount;
    private int currentPlayerCount;
    private long gateOpenTime;
    private long gateCloseTime;
    private Map<PlayerRank, Double> rankRewardMultipliers;
    private List<String> gateRequirements;
    private boolean isInstance;
    private String parentDungeonId;
    private String rank;  // Rang du donjon (E, D, C, B, A, S)
    private boolean isActive;

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
        this.theme = DungeonTheme.MINE;
        this.minRooms = 5;
        this.maxRooms = 10;
        this.size = 3;
        this.mobDensity = 0.5;
        this.lootDensity = 0.4;
        this.trapDensity = 0.3;
        this.mobSpawns = new HashMap<>();
        this.minimumRank = PlayerRank.E;
        this.isGateOpen = false;
        this.isGateCleared = false;
        this.requiredPlayerCount = 1;
        this.currentPlayerCount = 0;
        this.gateOpenTime = 0;
        this.gateCloseTime = 0;
        this.rankRewardMultipliers = new HashMap<>();
        this.gateRequirements = new ArrayList<>();
        this.isInstance = false;
        this.parentDungeonId = null;
        this.isActive = true;
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

    public double getLootDensity() {
        return lootDensity;
    }

    public double getTrapDensity() {
        return trapDensity;
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

    public void setLootDensity(double lootDensity) {
        this.lootDensity = lootDensity;
    }

    public void setTrapDensity(double trapDensity) {
        this.trapDensity = trapDensity;
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

    public boolean canPlayerEnter(Player player) {
        if (!canPlayerEnter(player)) {
            return false;
        }

        if (!isGateOpen) {
            return false;
        }

        if (currentPlayerCount >= maxPlayers) {
            return false;
        }

        if (!isAccessibleToRank(ReveilDesChasseurs.getInstance().getRankManager().getPlayerRank(player))) {
            return false;
        }

        for (String questId : requiredQuests) {
            if (!ReveilDesChasseurs.getInstance().getQuestManager().hasCompletedQuest(player.getUniqueId(), questId)) {
                return false;
            }
        }

        return true;
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

    public PlayerRank getMinimumRank() {
        return minimumRank;
    }

    public void setMinimumRank(PlayerRank minimumRank) {
        this.minimumRank = minimumRank;
    }

    public String getBossMobId() {
        return bossMobId;
    }

    public void setBossMobId(String bossMobId) {
        this.bossMobId = bossMobId;
    }

    public Map<String, Integer> getMobSpawns() {
        return mobSpawns;
    }

    public void setMobSpawns(Map<String, Integer> mobSpawns) {
        this.mobSpawns = mobSpawns;
    }

    public void addMobSpawn(String mobId, int count) {
        mobSpawns.put(mobId, count);
    }

    public boolean isAccessibleToRank(PlayerRank rank) {
        return rank.ordinal() >= minimumRank.ordinal();
    }

    public double calculateRewardMultiplier(PlayerRank playerRank) {
        return difficultyMultiplier * playerRank.getRewardMultiplier();
    }

    public boolean isGateOpen() {
        return isGateOpen;
    }

    public void setGateOpen(boolean gateOpen) {
        isGateOpen = gateOpen;
    }

    public boolean isGateCleared() {
        return isGateCleared;
    }

    public void setGateCleared(boolean gateCleared) {
        isGateCleared = gateCleared;
    }

    public int getRequiredPlayerCount() {
        return requiredPlayerCount;
    }

    public void setRequiredPlayerCount(int requiredPlayerCount) {
        this.requiredPlayerCount = requiredPlayerCount;
    }

    public int getCurrentPlayerCount() {
        return currentPlayerCount;
    }

    public void setCurrentPlayerCount(int currentPlayerCount) {
        this.currentPlayerCount = currentPlayerCount;
    }

    public long getGateOpenTime() {
        return gateOpenTime;
    }

    public void setGateOpenTime(long gateOpenTime) {
        this.gateOpenTime = gateOpenTime;
    }

    public long getGateCloseTime() {
        return gateCloseTime;
    }

    public void setGateCloseTime(long gateCloseTime) {
        this.gateCloseTime = gateCloseTime;
    }

    public Map<PlayerRank, Double> getRankRewardMultipliers() {
        return rankRewardMultipliers;
    }

    public void setRankRewardMultiplier(PlayerRank rank, double multiplier) {
        rankRewardMultipliers.put(rank, multiplier);
    }

    public double getRankRewardMultiplier(PlayerRank rank) {
        return rankRewardMultipliers.getOrDefault(rank, 1.0);
    }

    public List<String> getGateRequirements() {
        return gateRequirements;
    }

    public void addGateRequirement(String requirement) {
        gateRequirements.add(requirement);
    }

    public boolean isInstance() {
        return isInstance;
    }

    public void setInstance(boolean instance) {
        isInstance = instance;
    }

    public String getParentDungeonId() {
        return parentDungeonId;
    }

    public void setParentDungeonId(String parentDungeonId) {
        this.parentDungeonId = parentDungeonId;
    }

    public void openGate() {
        isGateOpen = true;
        gateOpenTime = System.currentTimeMillis();
        gateCloseTime = gateOpenTime + timeLimit;
    }

    public void closeGate() {
        isGateOpen = false;
        isGateCleared = false;
        currentPlayerCount = 0;
    }

    public boolean isGateExpired() {
        return System.currentTimeMillis() > gateCloseTime;
    }

    public long getRemainingTime() {
        return Math.max(0, gateCloseTime - System.currentTimeMillis());
    }

    public void clearGate() {
        isGateCleared = true;
        distributeRewards();
    }

    private void distributeRewards() {
        // TODO: Implémenter la distribution des récompenses
    }

    public String getRank() {
        return rank;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
} 