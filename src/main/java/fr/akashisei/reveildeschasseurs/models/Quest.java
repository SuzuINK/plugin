package fr.akashisei.reveildeschasseurs.models;

import org.bukkit.inventory.ItemStack;
import java.util.*;

public class Quest {
    private final String id;
    private final String name;
    private final String description;
    private final String type;
    private final int required;
    private final int reward;
    private final List<String> requirements;
    private final List<String> rewards;
    private final int requiredLevel;
    private final String category;
    private final String difficulty;
    private final HashMap<ItemStack, Integer> requiredItems;
    private final List<String> objectives;
    private final boolean repeatable;
    private final int cooldown;
    private final int minLevel;
    private final int maxLevel;
    private final int experienceReward;
    private final HashSet<String> requiredQuests;
    private final HashSet<String> allowedClasses;
    private final HashSet<String> allowedRanks;
    private final long questCooldown;

    public Quest(String id, String name, String description, String type, int required, int reward, 
                List<String> requirements, List<String> rewards, int requiredLevel, String category, 
                String difficulty, HashMap<ItemStack, Integer> requiredItems, List<String> objectives, 
                boolean repeatable, int cooldown, int minLevel, int maxLevel, int experienceReward,
                HashSet<String> requiredQuests, HashSet<String> allowedClasses, HashSet<String> allowedRanks,
                long questCooldown) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.type = type;
        this.required = required;
        this.reward = reward;
        this.requirements = requirements;
        this.rewards = rewards;
        this.requiredLevel = requiredLevel;
        this.category = category;
        this.difficulty = difficulty;
        this.requiredItems = requiredItems;
        this.objectives = objectives;
        this.repeatable = repeatable;
        this.cooldown = cooldown;
        this.minLevel = minLevel;
        this.maxLevel = maxLevel;
        this.experienceReward = experienceReward;
        this.requiredQuests = requiredQuests;
        this.allowedClasses = allowedClasses;
        this.allowedRanks = allowedRanks;
        this.questCooldown = questCooldown;
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

    public String getType() {
        return type;
    }

    public int getRequired() {
        return required;
    }

    public int getReward() {
        return reward;
    }

    public List<String> getRequirements() {
        return requirements;
    }

    public List<String> getRewards() {
        return rewards;
    }

    public int getRequiredLevel() {
        return requiredLevel;
    }

    public String getCategory() {
        return category;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public HashMap<ItemStack, Integer> getRequiredItems() {
        return requiredItems;
    }

    public List<String> getObjectives() {
        return objectives;
    }

    public boolean isRepeatable() {
        return repeatable;
    }

    public int getCooldown() {
        return cooldown;
    }

    public int getMinLevel() {
        return minLevel;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public int getExperienceReward() {
        return experienceReward;
    }

    public HashSet<String> getRequiredQuests() {
        return requiredQuests;
    }

    public HashSet<String> getAllowedClasses() {
        return allowedClasses;
    }

    public HashSet<String> getAllowedRanks() {
        return allowedRanks;
    }

    public long getQuestCooldown() {
        return questCooldown;
    }
} 