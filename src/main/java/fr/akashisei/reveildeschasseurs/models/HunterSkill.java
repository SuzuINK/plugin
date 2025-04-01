package fr.akashisei.reveildeschasseurs.models;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class HunterSkill {
    private final String id;
    private final String name;
    private final String description;
    private final SkillType type;
    private final int levelRequirement;
    private final int manaCost;
    private final int cooldown;
    private final double damageMultiplier;
    private final int duration;
    private final PotionEffectType[] effects;
    private final int[] effectAmplifiers;
    private final int[] effectDurations;
    private int masteryLevel;
    private int experience;
    private int maxExperience;
    private double masteryMultiplier;

    public HunterSkill(String id, String name, String description, SkillType type, 
                      int levelRequirement, int manaCost, int cooldown, 
                      double damageMultiplier, int duration,
                      PotionEffectType[] effects, int[] effectAmplifiers, int[] effectDurations) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.type = type;
        this.levelRequirement = levelRequirement;
        this.manaCost = manaCost;
        this.cooldown = cooldown;
        this.damageMultiplier = damageMultiplier;
        this.duration = duration;
        this.effects = effects;
        this.effectAmplifiers = effectAmplifiers;
        this.effectDurations = effectDurations;
        this.masteryLevel = 1;
        this.experience = 0;
        this.maxExperience = calculateMaxExperience(1);
        this.masteryMultiplier = 1.0;
    }

    private int calculateMaxExperience(int level) {
        // Formule de progression : 100 * (level ^ 1.5)
        return (int) (100 * Math.pow(level, 1.5));
    }

    public void addExperience(int amount) {
        experience += amount;
        while (experience >= maxExperience) {
            experience -= maxExperience;
            levelUp();
        }
    }

    private void levelUp() {
        masteryLevel++;
        maxExperience = calculateMaxExperience(masteryLevel);
        masteryMultiplier = 1.0 + (masteryLevel - 1) * 0.1; // +10% par niveau
        updateSkillStats();
    }

    private void updateSkillStats() {
        // Mise à jour des statistiques selon le niveau de maîtrise
        for (int i = 0; i < effects.length; i++) {
            effectAmplifiers[i] = (int) (effectAmplifiers[i] * masteryMultiplier);
            effectDurations[i] = (int) (effectDurations[i] * masteryMultiplier);
        }
    }

    public void apply(Player player) {
        // Appliquer les effets de potion avec le multiplicateur de maîtrise
        for (int i = 0; i < effects.length; i++) {
            player.addPotionEffect(new PotionEffect(
                effects[i],
                effectDurations[i] * 20, // Convertir en ticks
                effectAmplifiers[i]
            ));
        }
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

    public SkillType getType() {
        return type;
    }

    public int getLevelRequirement() {
        return levelRequirement;
    }

    public int getManaCost() {
        return manaCost;
    }

    public int getCooldown() {
        return cooldown;
    }

    public double getDamageMultiplier() {
        return damageMultiplier;
    }

    public int getDuration() {
        return duration;
    }

    public int getMasteryLevel() {
        return masteryLevel;
    }

    public void setMasteryLevel(int masteryLevel) {
        this.masteryLevel = masteryLevel;
        this.maxExperience = calculateMaxExperience(masteryLevel);
        this.masteryMultiplier = 1.0 + (masteryLevel - 1) * 0.1;
        updateSkillStats();
    }

    public int getExperience() {
        return experience;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }

    public int getMaxExperience() {
        return maxExperience;
    }

    public double getMasteryMultiplier() {
        return masteryMultiplier;
    }

    public double getEffectiveDamageMultiplier() {
        return damageMultiplier * masteryMultiplier;
    }

    public int getEffectiveManaCost() {
        return (int) (manaCost * (1.0 - (masteryLevel - 1) * 0.05)); // -5% de mana par niveau
    }

    public int getEffectiveCooldown() {
        return (int) (cooldown * (1.0 - (masteryLevel - 1) * 0.05)); // -5% de cooldown par niveau
    }

    public enum SkillType {
        ACTIVE,     // Compétence active avec cooldown
        PASSIVE,    // Compétence passive toujours active
        ULTIMATE,   // Compétence ultime avec long cooldown
        BUFF,       // Compétence de buff
        DEBUFF,     // Compétence de debuff
        HEAL,       // Compétence de soin
        SHIELD      // Compétence de bouclier
    }
} 