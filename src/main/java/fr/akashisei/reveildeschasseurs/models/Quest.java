package fr.akashisei.reveildeschasseurs.models;

import java.util.ArrayList;
import java.util.List;

public class Quest {
    private final String id;
    private final String name;
    private final String description;
    private final int minLevel;
    private long rewardMoney;
    private long rewardExp;
    private final List<String> objectives;
    private final List<String> requiredQuests;
    private final List<String> requiredItems;
    private final List<String> allowedClasses;
    private final List<String> allowedRanks;
    private boolean repeatable;
    private long cooldown;
    private int maxCompletions;

    public Quest(String id, String name, String description, int minLevel) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.minLevel = minLevel;
        this.rewardMoney = 0;
        this.rewardExp = 0;
        this.objectives = new ArrayList<>();
        this.requiredQuests = new ArrayList<>();
        this.requiredItems = new ArrayList<>();
        this.allowedClasses = new ArrayList<>();
        this.allowedRanks = new ArrayList<>();
        this.repeatable = false;
        this.cooldown = 0;
        this.maxCompletions = 0;
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

    public int getRequiredLevel() {
        return minLevel;
    }

    public long getRewardMoney() {
        return rewardMoney;
    }

    public void setRewardMoney(long rewardMoney) {
        this.rewardMoney = rewardMoney;
    }

    public long getRewardExp() {
        return rewardExp;
    }

    public void setRewardExp(long rewardExp) {
        this.rewardExp = rewardExp;
    }

    public int getReward() {
        return (int) rewardMoney;
    }

    public int getExperience() {
        return (int) rewardExp;
    }

    public List<String> getObjectives() {
        return new ArrayList<>(objectives);
    }

    public void setObjectives(List<String> objectives) {
        this.objectives.clear();
        this.objectives.addAll(objectives);
    }

    public void addObjective(String objective) {
        objectives.add(objective);
    }

    public List<String> getRequiredQuests() {
        return new ArrayList<>(requiredQuests);
    }

    public void setRequiredQuests(List<String> quests) {
        this.requiredQuests.clear();
        this.requiredQuests.addAll(quests);
    }

    public void addRequiredQuest(String questId) {
        requiredQuests.add(questId);
    }

    public List<String> getRequiredItems() {
        return new ArrayList<>(requiredItems);
    }

    public void setRequiredItems(List<String> items) {
        this.requiredItems.clear();
        this.requiredItems.addAll(items);
    }

    public void addRequiredItem(String itemId) {
        requiredItems.add(itemId);
    }

    public List<String> getAllowedClasses() {
        return new ArrayList<>(allowedClasses);
    }

    public void setAllowedClasses(List<String> classes) {
        this.allowedClasses.clear();
        this.allowedClasses.addAll(classes);
    }

    public void addAllowedClass(String className) {
        allowedClasses.add(className);
    }

    public List<String> getAllowedRanks() {
        return new ArrayList<>(allowedRanks);
    }

    public void setAllowedRanks(List<String> ranks) {
        this.allowedRanks.clear();
        this.allowedRanks.addAll(ranks);
    }

    public void addAllowedRank(String rank) {
        allowedRanks.add(rank);
    }

    public boolean isRepeatable() {
        return repeatable;
    }

    public void setRepeatable(boolean repeatable) {
        this.repeatable = repeatable;
    }

    public long getCooldown() {
        return cooldown;
    }

    public void setCooldown(long cooldown) {
        this.cooldown = cooldown;
    }

    public int getMaxCompletions() {
        return maxCompletions;
    }

    public void setMaxCompletions(int maxCompletions) {
        this.maxCompletions = maxCompletions;
    }
} 