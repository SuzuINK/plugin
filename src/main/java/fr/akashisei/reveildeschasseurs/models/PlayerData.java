package fr.akashisei.reveildeschasseurs.models;

import java.util.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PlayerData {
    private final UUID playerId;
    private String playerName;
    private String playerClass;
    private String rank;
    private int level;
    private int experience;
    private long money;
    private int mobsKilled;
    private int deaths;
    private int completedDungeons;
    private int failedDungeons;
    private int totalCompletedQuests;
    private Set<String> activeQuests;
    private Set<String> completedQuests;
    private Map<String, Integer> questProgress;
    private Map<String, Long> questCooldowns;
    private Map<String, Integer> questCompletions;
    private boolean questTracking;
    private String trackedQuest;
    private Map<String, Map<String, Integer>> questObjectiveProgress;
    private int strength;
    private int defense;
    private int speed;
    private List<ItemStack> inventory;
    private Map<String, Object> customData;
    private Map<String, Integer> statistics;

    public PlayerData(UUID playerId, String playerName) {
        this.playerId = playerId;
        this.playerName = playerName;
        this.playerClass = "NONE";
        this.rank = "NONE";
        this.level = 1;
        this.experience = 0;
        this.money = 0;
        this.mobsKilled = 0;
        this.deaths = 0;
        this.completedDungeons = 0;
        this.failedDungeons = 0;
        this.totalCompletedQuests = 0;
        this.activeQuests = new HashSet<>();
        this.completedQuests = new HashSet<>();
        this.questProgress = new HashMap<>();
        this.questCooldowns = new HashMap<>();
        this.questCompletions = new HashMap<>();
        this.questObjectiveProgress = new HashMap<>();
        this.questTracking = false;
        this.trackedQuest = null;
        this.strength = 1;
        this.defense = 1;
        this.speed = 1;
        this.inventory = new ArrayList<>();
        this.customData = new HashMap<>();
        this.statistics = new HashMap<>();
    }

    // Getters
    public UUID getPlayerId() { return playerId; }
    public String getPlayerName() { return playerName; }
    public String getPlayerClass() { return playerClass; }
    public String getRank() { return rank; }
    public int getLevel() { return level; }
    public int getExperience() { return experience; }
    public long getMoney() { return money; }
    public int getMobsKilled() { return mobsKilled; }
    public int getDeaths() { return deaths; }
    public int getCompletedDungeons() { return completedDungeons; }
    public int getFailedDungeons() { return failedDungeons; }
    public int getTotalCompletedQuests() { return totalCompletedQuests; }
    public Set<String> getActiveQuests() { return Collections.unmodifiableSet(activeQuests); }
    public Set<String> getCompletedQuests() { return Collections.unmodifiableSet(completedQuests); }
    public Map<String, Integer> getQuestProgress() { return questProgress; }
    public Map<String, Long> getQuestCooldowns() { return questCooldowns; }
    public Map<String, Integer> getQuestCompletions() { return questCompletions; }
    public int getStrength() { return strength; }
    public int getDefense() { return defense; }
    public int getSpeed() { return speed; }
    public List<ItemStack> getInventory() { return inventory; }
    public Map<String, Object> getCustomData() { return customData; }
    public Map<String, Integer> getStatistics() { return statistics; }

    // Setters
    public void setPlayerName(String playerName) { this.playerName = playerName; }
    public void setPlayerClass(String playerClass) { this.playerClass = playerClass; }
    public void setRank(String rank) { this.rank = rank; }
    public void setLevel(int level) { this.level = level; }
    public void setExperience(int experience) { this.experience = experience; }
    public void setMoney(long money) { this.money = money; }
    public void setMobsKilled(int mobsKilled) { this.mobsKilled = mobsKilled; }
    public void setDeaths(int deaths) { this.deaths = deaths; }
    public void setCompletedDungeons(int completedDungeons) { this.completedDungeons = completedDungeons; }
    public void setFailedDungeons(int failedDungeons) { this.failedDungeons = failedDungeons; }
    public void setTotalCompletedQuests(int totalCompletedQuests) { this.totalCompletedQuests = totalCompletedQuests; }
    public void setActiveQuests(Set<String> activeQuests) { this.activeQuests = new HashSet<>(activeQuests); }
    public void setCompletedQuests(Set<String> completedQuests) { this.completedQuests = new HashSet<>(completedQuests); }
    public void setQuestProgress(Map<String, Integer> questProgress) { this.questProgress = questProgress; }
    public void setQuestCooldowns(Map<String, Long> questCooldowns) { this.questCooldowns = questCooldowns; }
    public void setQuestCompletions(Map<String, Integer> questCompletions) { this.questCompletions = questCompletions; }
    public void setStrength(int strength) { this.strength = strength; }
    public void setDefense(int defense) { this.defense = defense; }
    public void setSpeed(int speed) { this.speed = speed; }
    public void setInventory(List<ItemStack> inventory) { this.inventory = inventory; }
    public void setCustomData(Map<String, Object> customData) { this.customData = customData; }
    public void setStatistics(Map<String, Integer> statistics) { this.statistics = statistics; }

    // Méthodes pour les quêtes
    public boolean hasActiveQuest(String questId) {
        return activeQuests.contains(questId);
    }

    public void addActiveQuest(String questId) {
        activeQuests.add(questId);
        questProgress.put(questId, 0);
    }

    public void removeActiveQuest(String questId) {
        activeQuests.remove(questId);
        questProgress.remove(questId);
    }

    public boolean hasCompletedQuest(String questId) {
        return completedQuests.contains(questId);
    }

    public void addCompletedQuest(String questId) {
        completedQuests.add(questId);
        totalCompletedQuests++;
    }

    public int getQuestProgress(String questId) {
        return questProgress.getOrDefault(questId, 0);
    }

    public void setQuestProgress(String questId, int progress) {
        questProgress.put(questId, progress);
    }

    public void incrementQuestProgress(String questId) {
        questProgress.put(questId, getQuestProgress(questId) + 1);
    }

    public boolean isQuestOnCooldown(String questId) {
        if (!questCooldowns.containsKey(questId)) {
            return false;
        }
        return System.currentTimeMillis() < questCooldowns.get(questId);
    }

    public void setQuestCooldown(String questId, long endTime) {
        questCooldowns.put(questId, endTime);
    }

    public void clearQuestCooldown(String questId) {
        questCooldowns.remove(questId);
    }

    // Méthodes pour les donjons
    public void incrementCompletedDungeons() {
        completedDungeons++;
    }

    public void incrementFailedDungeons() {
        failedDungeons++;
    }

    // Méthodes pour l'expérience de chasseur
    public void addExperience(int amount) {
        this.experience += amount;
        checkLevelUp();
    }

    public void addMoney(long amount) {
        this.money += amount;
    }

    public boolean removeMoney(long amount) {
        if (this.money >= amount) {
            this.money -= amount;
            return true;
        }
        return false;
    }

    private void checkLevelUp() {
        int experienceNeeded = calculateExperienceNeeded(this.level);
        while (this.experience >= experienceNeeded) {
            this.experience -= experienceNeeded;
            this.level++;
            experienceNeeded = calculateExperienceNeeded(this.level);
        }
    }

    private int calculateExperienceNeeded(int level) {
        return level * 1000; // Formule simple pour l'expérience nécessaire
    }

    public String getClassName() {
        return playerClass != null ? playerClass : "Non assigné";
    }

    public String getRankName() {
        return rank != null ? rank : "Non assigné";
    }

    // Méthodes pour le suivi des quêtes
    public boolean isQuestTracking() {
        return questTracking;
    }

    public void setQuestTracking(boolean questTracking) {
        this.questTracking = questTracking;
    }

    public String getTrackedQuest() {
        return trackedQuest;
    }

    public void setTrackedQuest(String questId) {
        this.trackedQuest = questId;
    }

    public void updateQuestObjectiveProgress(String questId, String objective, int progress) {
        questObjectiveProgress.computeIfAbsent(questId, k -> new HashMap<>())
            .merge(objective, progress, Integer::sum);
    }

    public int getQuestProgress(String questId, String objective) {
        return questObjectiveProgress.getOrDefault(questId, Collections.emptyMap())
            .getOrDefault(objective, 0);
    }

    public Map<String, Integer> getQuestObjectiveProgress(String questId) {
        return questObjectiveProgress.getOrDefault(questId, Collections.emptyMap());
    }

    public void incrementQuestCompletions(String questId) {
        questCompletions.merge(questId, 1, Integer::sum);
        totalCompletedQuests++;
    }

    public void updateQuestProgress(String questId, int progress) {
        questProgress.put(questId, progress);
    }

    public int getQuestCompletions(String questId) {
        return questCompletions.getOrDefault(questId, 0);
    }

    // Méthodes utilitaires
    public void incrementStatistic(String stat, int amount) {
        this.statistics.merge(stat, amount, Integer::sum);
    }

    public int getStatistic(String stat) {
        return this.statistics.getOrDefault(stat, 0);
    }

    // Méthodes pour l'expérience de chasseur
    public int getHunterLevel() {
        return getLevel();
    }

    public int getHunterExp() {
        return getExperience();
    }

    public void setHunterExp(int exp) {
        setExperience(exp);
        checkLevelUp();
    }

    public void addHunterExp(int amount) {
        addExperience(amount);
    }

    public int getNextHunterLevelExp() {
        return calculateExperienceNeeded(getLevel());
    }

    public int getNextLevelExperience() {
        return calculateExperienceNeeded(level);
    }
} 