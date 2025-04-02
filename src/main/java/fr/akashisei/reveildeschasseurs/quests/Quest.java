package fr.akashisei.reveildeschasseurs.quests;

import org.bukkit.inventory.ItemStack;
import java.util.*;
import java.util.List;

public class Quest {
    private final String id;
    private final String name;
    private final String description;
    private final QuestType type;
    private final int required;
    private final int reward;
    private final List<String> requirements;
    private final List<String> rewards;
    private final int requiredLevel;
    private final Set<String> requiredQuests;
    private final long cooldown;
    private final Map<ItemStack, Integer> requiredItems;
    private final QuestReward rewardReward;
    private final boolean repeatable;
    private final int maxCompletions;
    private final Set<String> allowedClasses;
    private final Set<String> allowedRanks;
    private final Map<String, Integer> objectives;
    private final int rewardExp;
    private final int rewardMoney;

    public Quest(String id, String name, String description, String type, int required, int reward, 
                List<String> requirements, List<String> rewards) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.type = QuestType.valueOf(type.toUpperCase());
        this.required = required;
        this.reward = reward;
        this.requirements = new ArrayList<>(requirements);
        this.rewards = new ArrayList<>(rewards);
        this.requiredLevel = 1;
        this.requiredQuests = new HashSet<>();
        this.cooldown = 0;
        this.requiredItems = new HashMap<>();
        this.rewardReward = new QuestReward(reward, 0);
        this.repeatable = false;
        this.maxCompletions = 1;
        this.allowedClasses = new HashSet<>();
        this.allowedRanks = new HashSet<>();
        this.objectives = new HashMap<>();
        this.objectives.put(type.toUpperCase() + ":", required);
        this.rewardExp = 0;
        this.rewardMoney = 0;
    }

    public Quest(String id, String name, String description, QuestType type, int required, int reward,
                List<String> requirements, List<String> rewards, int requiredLevel, 
                Set<String> requiredQuests, long cooldown) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.type = type;
        this.required = required;
        this.reward = reward;
        this.requirements = new ArrayList<>(requirements);
        this.rewards = new ArrayList<>(rewards);
        this.requiredLevel = requiredLevel;
        this.requiredQuests = new HashSet<>(requiredQuests);
        this.cooldown = cooldown;
        this.requiredItems = new HashMap<>();
        this.rewardReward = new QuestReward(reward, 0);
        this.repeatable = false;
        this.maxCompletions = 1;
        this.allowedClasses = new HashSet<>();
        this.allowedRanks = new HashSet<>();
        this.objectives = new HashMap<>();
        this.objectives.put(type.name() + ":", required);
        this.rewardExp = 0;
        this.rewardMoney = 0;
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

    public QuestType getType() {
        return type;
    }

    public int getRequired() {
        return required;
    }

    public int getReward() {
        return reward;
    }

    public List<String> getRequirements() {
        return Collections.unmodifiableList(requirements);
    }

    public List<String> getRewards() {
        return Collections.unmodifiableList(rewards);
    }

    public int getRequiredLevel() {
        return requiredLevel;
    }

    public Set<String> getRequiredQuests() {
        return Collections.unmodifiableSet(requiredQuests);
    }

    public long getCooldown() {
        return cooldown;
    }

    public Map<ItemStack, Integer> getRequiredItems() {
        return new HashMap<>(requiredItems);
    }

    public QuestReward getRewardReward() {
        return rewardReward;
    }

    public int getRewardExp() {
        return rewardExp;
    }

    public int getRewardMoney() {
        return rewardMoney;
    }

    public boolean isRepeatable() {
        return repeatable;
    }

    public int getMaxCompletions() {
        return maxCompletions;
    }

    public Set<String> getAllowedClasses() {
        return new HashSet<>(allowedClasses);
    }

    public Set<String> getAllowedRanks() {
        return new HashSet<>(allowedRanks);
    }

    public Map<String, Integer> getObjectives() {
        return new HashMap<>(objectives);
    }

    public Map<String, Object> toConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("id", id);
        config.put("name", name);
        config.put("description", description);
        config.put("type", type.name());
        config.put("required", required);
        config.put("reward", reward);
        config.put("required_level", requiredLevel);
        config.put("required_items", requiredItems);
        config.put("reward_exp", rewardExp);
        config.put("reward_money", rewardMoney);
        config.put("guaranteed_rewards", rewardReward.getGuaranteedItems());
        config.put("random_rewards", rewardReward.getRandomRewards());
        config.put("conditional_rewards", rewardReward.getConditionalRewards());
        config.put("repeatable", repeatable);
        config.put("max_completions", maxCompletions);
        config.put("required_quests", new ArrayList<>(requiredQuests));
        config.put("allowed_classes", new ArrayList<>(allowedClasses));
        config.put("allowed_ranks", new ArrayList<>(allowedRanks));
        config.put("cooldown", cooldown);
        config.put("objectives", objectives);
        config.put("requirements", new ArrayList<>(requirements));
        config.put("rewards", new ArrayList<>(rewards));
        return config;
    }

    public boolean hasRequirement(String requirement) {
        return requirements.contains(requirement);
    }

    public boolean hasReward(String reward) {
        return rewards.contains(reward);
    }

    public boolean requiresQuest(String questId) {
        return requiredQuests.contains(questId);
    }

    public boolean hasCooldown() {
        return cooldown > 0;
    }

    public String getTarget() {
        return objectives.keySet().iterator().next().split(":")[1];
    }

    public int getRequiredAmount() {
        return objectives.values().iterator().next();
    }

    public String getRewardWeapon() {
        for (String reward : rewards) {
            if (reward.startsWith("WEAPON:")) {
                return reward.substring(7);
            }
        }
        return null;
    }

    public List<String> getRewardItems() {
        List<String> items = new ArrayList<>();
        for (String reward : rewards) {
            if (reward.startsWith("ITEM:")) {
                items.add(reward.substring(5));
            }
        }
        return items;
    }
} 