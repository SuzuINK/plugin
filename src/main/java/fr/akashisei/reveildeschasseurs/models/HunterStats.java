package fr.akashisei.reveildeschasseurs.models;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import java.util.HashMap;
import java.util.Map;

public class HunterStats {
    private static final Map<String, HunterStats> playerStats = new HashMap<>();
    private final Player player;
    private HunterClass hunterClass;
    private int level;
    private int experience;
    private int maxExperience;
    private int strength;
    private int defense;
    private int mana;
    private int maxMana;
    private int speed;
    private int health;
    private int maxHealth;
    private double manaRegenRate;

    private HunterStats(Player player) {
        this.player = player;
        this.hunterClass = HunterClass.TANK; // Classe par défaut
        this.level = 1;
        this.experience = 0;
        this.maxExperience = 100;
        this.strength = 10;
        this.defense = 10;
        this.mana = 100;
        this.maxMana = 100;
        this.speed = 10;
        this.health = 20;
        this.maxHealth = 20;
        this.manaRegenRate = 1.0;
        updateStats();
    }

    public static HunterStats fromPlayer(Player player) {
        return playerStats.computeIfAbsent(player.getUniqueId().toString(), k -> new HunterStats(player));
    }

    public void setHunterClass(HunterClass hunterClass) {
        this.hunterClass = hunterClass;
        updateStats();
    }

    private void updateStats() {
        // Mettre à jour les multiplicateurs selon la classe
        this.strength = (int) (10 * hunterClass.getDamageMultiplier());
        this.defense = (int) (10 * hunterClass.getDefenseMultiplier());
        this.maxMana = (int) (100 * hunterClass.getManaMultiplier());
        this.speed = (int) (10 * hunterClass.getSpeedMultiplier());
        this.maxHealth = (int) (20 * hunterClass.getDefenseMultiplier());

        // Appliquer les effets passifs
        applyPassiveEffects();
    }

    private void applyPassiveEffects() {
        PotionEffectType[] effects = hunterClass.getPassiveEffects();
        int[] amplifiers = hunterClass.getEffectAmplifiers();
        int[] durations = hunterClass.getEffectDurations();

        for (int i = 0; i < effects.length; i++) {
            player.addPotionEffect(new org.bukkit.potion.PotionEffect(
                effects[i],
                durations[i] * 20, // Convertir en ticks
                amplifiers[i]
            ));
        }
    }

    public void addExperience(int amount) {
        this.experience += amount;
        while (this.experience >= this.maxExperience) {
            levelUp();
        }
    }

    private void levelUp() {
        this.level++;
        this.experience -= this.maxExperience;
        this.maxExperience = (int) (100 * Math.pow(1.5, level - 1));
        
        // Augmenter les statistiques de base
        this.strength += 2;
        this.defense += 2;
        this.maxMana += 10;
        this.speed += 1;
        this.maxHealth += 5;
        
        // Mettre à jour les stats avec les multiplicateurs de classe
        updateStats();
        
        // Notifier le joueur
        player.sendMessage("§aFélicitations ! Vous avez atteint le niveau " + level + " !");
    }

    // Getters et Setters
    public HunterClass getHunterClass() {
        return hunterClass;
    }

    public int getLevel() {
        return level;
    }

    public int getExperience() {
        return experience;
    }

    public int getMaxExperience() {
        return maxExperience;
    }

    public int getStrength() {
        return strength;
    }

    public int getDefense() {
        return defense;
    }

    public int getMana() {
        return mana;
    }

    public int getMaxMana() {
        return maxMana;
    }

    public int getSpeed() {
        return speed;
    }

    public int getHealth() {
        return health;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public double getManaRegenRate() {
        return manaRegenRate;
    }

    public void setMana(int mana) {
        this.mana = Math.min(Math.max(0, mana), maxMana);
    }

    public void setHealth(int health) {
        this.health = Math.min(Math.max(0, health), maxHealth);
    }
} 