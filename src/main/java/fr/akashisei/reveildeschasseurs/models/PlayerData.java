package fr.akashisei.reveildeschasseurs.models;

import java.util.*;

public class PlayerData {
    private final UUID uuid;
    private String name;
    private String playerClass;
    private String rank;
    private int level;
    private int experience;
    private long money;
    private int mobsKilled;
    private int deaths;
    private int completedDungeons;
    private int failedDungeons;
    private int completedQuests;
    private List<String> activeQuests;
    private Map<String, Integer> questProgress;
    private Map<String, Long> questCooldowns;
    private Map<String, Integer> questCompletions;
    private boolean questTracking;
    private String trackedQuest;
    private Map<String, Map<String, Integer>> questObjectiveProgress;

    public PlayerData(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
        this.activeQuests = new ArrayList<>();
        this.questProgress = new HashMap<>();
        this.questCooldowns = new HashMap<>();
        this.questCompletions = new HashMap<>();
        this.questObjectiveProgress = new HashMap<>();
        this.questTracking = false;
        this.trackedQuest = null;
    }

    // Getters
    public UUID getUuid() { return uuid; }
    public String getName() { return name; }
    public String getPlayerClass() { return playerClass; }
    public String getRank() { return rank; }
    public int getLevel() { return level; }
    public int getExperience() { return experience; }
    public long getMoney() { return money; }
    public int getMobsKilled() { return mobsKilled; }
    public int getDeaths() { return deaths; }
    public int getCompletedDungeons() { return completedDungeons; }
    public int getFailedDungeons() { return failedDungeons; }
    public int getCompletedQuests() { return completedQuests; }
    public List<String> getActiveQuests() { return activeQuests; }
    public Map<String, Integer> getQuestProgress() { return questProgress; }
    public Map<String, Long> getQuestCooldowns() { return questCooldowns; }
    public Map<String, Integer> getQuestCompletions() { return questCompletions; }

    // Setters
    public void setName(String name) { this.name = name; }
    public void setPlayerClass(String playerClass) { this.playerClass = playerClass; }
    public void setRank(String rank) { this.rank = rank; }
    public void setLevel(int level) { this.level = level; }
    public void setExperience(int experience) { this.experience = experience; }
    public void setMoney(long money) { this.money = money; }
    public void setMobsKilled(int mobsKilled) { this.mobsKilled = mobsKilled; }
    public void setDeaths(int deaths) { this.deaths = deaths; }
    public void setCompletedDungeons(int completedDungeons) { this.completedDungeons = completedDungeons; }
    public void setFailedDungeons(int failedDungeons) { this.failedDungeons = failedDungeons; }
    public void setCompletedQuests(int completedQuests) { this.completedQuests = completedQuests; }
    public void setActiveQuests(List<String> activeQuests) { this.activeQuests = activeQuests; }
    public void setQuestProgress(Map<String, Integer> questProgress) { this.questProgress = questProgress; }
    public void setQuestCooldowns(Map<String, Long> questCooldowns) { this.questCooldowns = questCooldowns; }
    public void setQuestCompletions(Map<String, Integer> questCompletions) { this.questCompletions = questCompletions; }

    // Méthodes pour les quêtes
    public void addActiveQuest(String questId) {
        if (!activeQuests.contains(questId)) {
            activeQuests.add(questId);
        }
    }

    public void removeActiveQuest(String questId) {
        activeQuests.remove(questId);
    }

    public boolean hasActiveQuest(String questId) {
        return activeQuests.contains(questId);
    }

    public void setQuestProgress(String questId, int progress) {
        questProgress.put(questId, progress);
    }

    public int getQuestProgress(String questId) {
        return questProgress.getOrDefault(questId, 0);
    }

    public void updateQuestProgress(String questId, int progress) {
        questProgress.merge(questId, progress, Integer::sum);
    }

    public void setQuestCooldown(String questId, long cooldown) {
        questCooldowns.put(questId, cooldown);
    }

    public boolean isQuestOnCooldown(String questId) {
        Long cooldown = questCooldowns.get(questId);
        return cooldown != null && cooldown > System.currentTimeMillis();
    }

    public void incrementQuestCompletions(String questId) {
        questCompletions.merge(questId, 1, Integer::sum);
        completedQuests++;
    }

    public boolean hasCompletedQuest(String questId) {
        return questCompletions.containsKey(questId) && questCompletions.get(questId) > 0;
    }

    // Méthodes pour les donjons
    public void incrementCompletedDungeons() {
        completedDungeons++;
    }

    public void incrementFailedDungeons() {
        failedDungeons++;
    }

    // Méthodes pour l'expérience et l'argent
    public void addExperience(long amount) {
        this.experience += amount;
        // TODO: Implémenter la logique de niveau
    }

    public void addMoney(long amount) {
        this.money += amount;
    }

    public long getNextLevelExperience() {
        // TODO: Implémenter la formule de calcul d'expérience pour le prochain niveau
        return 1000L * level;
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
} 