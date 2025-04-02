package fr.akashisei.reveildeschasseurs.quests;

import org.bukkit.inventory.ItemStack;
import java.util.*;

public class QuestReward {
    private final int baseExp;
    private final int baseMoney;
    private final List<ItemStack> guaranteedItems;
    private final List<RandomReward> randomRewards;
    private final Map<String, ItemStack> conditionalRewards;

    public QuestReward(int baseExp, int baseMoney) {
        this.baseExp = baseExp;
        this.baseMoney = baseMoney;
        this.guaranteedItems = new ArrayList<>();
        this.randomRewards = new ArrayList<>();
        this.conditionalRewards = new HashMap<>();
    }

    public void addGuaranteedItem(ItemStack item) {
        guaranteedItems.add(item);
    }

    public void addRandomReward(ItemStack item, double chance) {
        randomRewards.add(new RandomReward(item, chance));
    }

    public void addConditionalReward(String condition, ItemStack item) {
        conditionalRewards.put(condition, item);
    }

    public int getBaseExp() {
        return baseExp;
    }

    public int getBaseMoney() {
        return baseMoney;
    }

    public List<ItemStack> getGuaranteedItems() {
        return new ArrayList<>(guaranteedItems);
    }

    public List<RandomReward> getRandomRewards() {
        return new ArrayList<>(randomRewards);
    }

    public Map<String, ItemStack> getConditionalRewards() {
        return new HashMap<>(conditionalRewards);
    }

    public static class RandomReward {
        private final ItemStack item;
        private final double chance;

        public RandomReward(ItemStack item, double chance) {
            this.item = item;
            this.chance = chance;
        }

        public ItemStack getItem() {
            return item;
        }

        public double getChance() {
            return chance;
        }
    }
} 