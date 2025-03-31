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

    public PlayerData(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
        this.playerClass = "guerrier";
        this.rank = "novice";
        this.level = 1;
        this.experience = 0;
        this.money = 0;
        this.mobsKilled = 0;
        this.deaths = 0;
        this.completedDungeons = 0;
        this.failedDungeons = 0;
        this.completedQuests = 0;
        this.activeQuests = new ArrayList<>();
        this.questProgress = new HashMap<>();
        this.questCooldowns = new HashMap<>();
        this.questCompletions = new HashMap<>();
    }

    public UUID getUuid() {
        return uuid;
    }

    public UUID getPlayerId() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public String getPlayerName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlayerClass() {
        return playerClass;
    }

    public String getClassName() {
        return playerClass;
    }

    public void setPlayerClass(String playerClass) {
        this.playerClass = playerClass;
    }

    public String getRank() {
        return rank;
    }

    public String getRankName() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getExperience() {
        return experience;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }

    public long getMoney() {
        return money;
    }

    public void setMoney(long money) {
        this.money = money;
    }

    public int getMobsKilled() {
        return mobsKilled;
    }

    public void incrementMobsKilled() {
        this.mobsKilled++;
    }

    public int getDeaths() {
        return deaths;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    public void incrementDeaths() {
        this.deaths++;
    }

    public int getCompletedDungeons() {
        return completedDungeons;
    }

    public void incrementCompletedDungeons() {
        this.completedDungeons++;
    }

    public int getFailedDungeons() {
        return failedDungeons;
    }

    public void incrementFailedDungeons() {
        this.failedDungeons++;
    }

    public int getCompletedQuests() {
        return completedQuests;
    }

    public void incrementCompletedQuests() {
        this.completedQuests++;
    }

    public List<String> getActiveQuests() {
        return new ArrayList<>(activeQuests);
    }

    public void setActiveQuests(List<String> quests) {
        this.activeQuests = new ArrayList<>(quests);
    }

    public boolean hasActiveQuest(String questId) {
        return activeQuests.contains(questId);
    }

    public void addQuest(String questId) {
        if (!activeQuests.contains(questId)) {
            activeQuests.add(questId);
            questProgress.put(questId, 0);
        }
    }

    public void removeActiveQuest(String questId) {
        activeQuests.remove(questId);
        questProgress.remove(questId);
    }

    public Map<String, Integer> getQuestProgress() {
        return new HashMap<>(questProgress);
    }

    public void setQuestProgress(Map<String, Integer> progress) {
        this.questProgress = new HashMap<>(progress);
    }

    public void setQuestProgress(String questId, int progress) {
        questProgress.put(questId, progress);
    }

    public int getQuestProgress(String questId) {
        return questProgress.getOrDefault(questId, 0);
    }

    public Map<String, Long> getQuestCooldowns() {
        return new HashMap<>(questCooldowns);
    }

    public void setQuestCooldown(String questId, long cooldown) {
        questCooldowns.put(questId, cooldown);
    }

    public boolean isQuestOnCooldown(String questId) {
        Long cooldownEnd = questCooldowns.get(questId);
        return cooldownEnd != null && cooldownEnd > System.currentTimeMillis();
    }

    public Map<String, Integer> getQuestCompletions() {
        return new HashMap<>(questCompletions);
    }

    public void incrementQuestCompletion(String questId) {
        questCompletions.merge(questId, 1, Integer::sum);
    }

    public int getQuestCompletions(String questId) {
        return questCompletions.getOrDefault(questId, 0);
    }

    public boolean hasCompletedQuest(String questId) {
        return questCompletions.containsKey(questId);
    }

    public void addExperience(long amount) {
        this.experience += (int) amount;
        checkLevelUp();
    }

    private void checkLevelUp() {
        while (experience >= getExpForNextLevel()) {
            level++;
            experience -= getExpForNextLevel();
        }
    }

    private int getExpForNextLevel() {
        return level * 1000;
    }

    public void addMoney(long amount) {
        this.money += amount;
    }

    public boolean removeMoney(long amount) {
        if (money >= amount) {
            money -= amount;
            return true;
        }
        return false;
    }

    public void resetStats() {
        this.level = 1;
        this.experience = 0;
        this.money = 0;
        this.mobsKilled = 0;
        this.deaths = 0;
        this.completedDungeons = 0;
        this.failedDungeons = 0;
        this.completedQuests = 0;
        this.activeQuests.clear();
        this.questProgress.clear();
        this.questCooldowns.clear();
        this.questCompletions.clear();
    }

    public long getNextLevelExperience() {
        return getExpForNextLevel();
    }
} 