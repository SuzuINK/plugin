package fr.akashisei.reveildeschasseurs.quests;

import org.bukkit.inventory.ItemStack;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;

public class Quest {
    private final String id;
    private final String name;
    private final String description;
    private final String type;
    private final int requiredAmount;
    private final String target;
    private final String rewardWeapon;
    private final String tier;
    private final int requiredLevel;
    private final Map<ItemStack, Integer> requiredItems;
    private final List<String> rewards;
    private final boolean repeatable;
    private final int maxCompletions;
    private final int rewardExp;
    private final int rewardMoney;
    private final Set<String> requiredQuests;
    private final Set<String> allowedClasses;
    private final Set<String> allowedRanks;
    private final long cooldown;
    private final Map<String, Integer> objectives;

    public Quest(String id, String name, String description, String type, int requiredAmount, 
                String target, String rewardWeapon, String tier, int requiredLevel,
                Map<ItemStack, Integer> requiredItems, List<String> rewards, 
                boolean repeatable, int maxCompletions, int rewardExp, int rewardMoney,
                Set<String> requiredQuests, Set<String> allowedClasses, 
                Set<String> allowedRanks, long cooldown) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.type = type;
        this.requiredAmount = requiredAmount;
        this.target = target;
        this.rewardWeapon = rewardWeapon;
        this.tier = tier;
        this.requiredLevel = requiredLevel;
        this.requiredItems = requiredItems;
        this.rewards = rewards;
        this.repeatable = repeatable;
        this.maxCompletions = maxCompletions;
        this.rewardExp = rewardExp;
        this.rewardMoney = rewardMoney;
        this.requiredQuests = requiredQuests;
        this.allowedClasses = allowedClasses;
        this.allowedRanks = allowedRanks;
        this.cooldown = cooldown;
        this.objectives = new HashMap<>();
        this.objectives.put(type + ":" + target, requiredAmount);
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

    public int getRequiredAmount() {
        return requiredAmount;
    }

    public String getTarget() {
        return target;
    }

    public String getRewardWeapon() {
        return rewardWeapon;
    }

    public String getTier() {
        return tier;
    }

    public int getRequiredLevel() {
        return requiredLevel;
    }

    public Map<ItemStack, Integer> getRequiredItems() {
        return requiredItems;
    }

    public List<String> getRewards() {
        return rewards;
    }

    public boolean isRepeatable() {
        return repeatable;
    }

    public int getMaxCompletions() {
        return maxCompletions;
    }

    public int getRewardExp() {
        return rewardExp;
    }

    public int getRewardMoney() {
        return rewardMoney;
    }

    public Set<String> getRequiredQuests() {
        return requiredQuests;
    }

    public Set<String> getAllowedClasses() {
        return allowedClasses;
    }

    public Set<String> getAllowedRanks() {
        return allowedRanks;
    }

    public long getCooldown() {
        return cooldown;
    }

    public Map<String, Integer> getObjectives() {
        return objectives;
    }

    public Map<String, Object> toConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("id", id);
        config.put("name", name);
        config.put("description", description);
        config.put("type", type);
        config.put("required_amount", requiredAmount);
        config.put("target", target);
        config.put("reward_weapon", rewardWeapon);
        config.put("tier", tier);
        config.put("required_level", requiredLevel);
        config.put("required_items", requiredItems);
        config.put("rewards", rewards);
        config.put("repeatable", repeatable);
        config.put("max_completions", maxCompletions);
        config.put("reward_exp", rewardExp);
        config.put("reward_money", rewardMoney);
        config.put("required_quests", requiredQuests);
        config.put("allowed_classes", allowedClasses);
        config.put("allowed_ranks", allowedRanks);
        config.put("cooldown", cooldown);
        config.put("objectives", objectives);
        return config;
    }
} 