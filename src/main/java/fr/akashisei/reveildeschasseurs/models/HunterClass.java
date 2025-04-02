package fr.akashisei.reveildeschasseurs.models;

import org.bukkit.Material;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.entity.Player;

public enum HunterClass {
    WARRIOR("Guerrier", "§c", 100, 20),
    ARCHER("Archer", "§a", 80, 15),
    MAGE("Mage", "§b", 60, 10),
    ASSASSIN("Assassin", "§7", 70, 12),
    TANK("Tank", "Spécialisé dans la défense et la résistance", 
         new PotionEffectType[]{PotionEffectType.DAMAGE_RESISTANCE, PotionEffectType.REGENERATION},
         new int[]{1, 0},
         new int[]{600, 200},
         Material.DIAMOND_CHESTPLATE,
         PlayerRank.E,
         1000),

    HEALER("Soigneur", "Spécialisé dans le soin et le support",
           new PotionEffectType[]{PotionEffectType.REGENERATION, PotionEffectType.HEAL},
           new int[]{0, 1},
           new int[]{200, 300},
           Material.IRON_CHESTPLATE,
           PlayerRank.D,
           2500),

    BERSERKER("Berserker", "Spécialisé dans les dégâts et la rage",
              new PotionEffectType[]{PotionEffectType.INCREASE_DAMAGE, PotionEffectType.SPEED},
              new int[]{2, 0},
              new int[]{400, 200},
              Material.NETHERITE_CHESTPLATE,
              PlayerRank.C,
              5000),

    SHADOW_MONARCH("Monarque des Ombres", "Classe légendaire de contrôle des ombres",
                  new PotionEffectType[]{PotionEffectType.DARKNESS, PotionEffectType.INCREASE_DAMAGE},
                  new int[]{3, 2},
                  new int[]{600, 400},
                  Material.NETHERITE_CHESTPLATE,
                  PlayerRank.S,
                  10000),

    SUMMONER("Invocateur", "Spécialisé dans l'invocation de créatures",
             new PotionEffectType[]{PotionEffectType.NIGHT_VISION, PotionEffectType.REGENERATION},
             new int[]{1, 2},
             new int[]{300, 400},
             Material.GOLDEN_CHESTPLATE,
             PlayerRank.B,
             8000);

    private final String displayName;
    private final String color;
    private final double baseHealth;
    private final double baseDamage;
    private final String name;
    private final String description;
    private final PotionEffectType[] passiveEffects;
    private final int[] effectAmplifiers;
    private final int[] effectDurations;
    private final Material armorType;
    private final PlayerRank requiredRank;
    private final int cost;
    private final double damageMultiplier;
    private final double defenseMultiplier;
    private final double manaMultiplier;
    private final double speedMultiplier;

    HunterClass(String displayName, String color, double baseHealth, double baseDamage) {
        this.displayName = displayName;
        this.color = color;
        this.baseHealth = baseHealth;
        this.baseDamage = baseDamage;
        this.name = displayName;
        this.description = "";
        this.passiveEffects = new PotionEffectType[]{};
        this.effectAmplifiers = new int[]{};
        this.effectDurations = new int[]{};
        this.armorType = Material.LEATHER_CHESTPLATE;
        this.requiredRank = PlayerRank.E;
        this.cost = 0;
        
        // Multiplicateurs de base selon la classe
        double[] multipliers = getMultipliersForClass(name);
        this.damageMultiplier = multipliers[0];
        this.defenseMultiplier = multipliers[1];
        this.manaMultiplier = multipliers[2];
        this.speedMultiplier = multipliers[3];
    }

    HunterClass(String name, String description, 
                PotionEffectType[] passiveEffects, int[] effectAmplifiers, 
                int[] effectDurations, Material armorType,
                PlayerRank requiredRank, int cost) {
        this.name = name;
        this.displayName = name;
        this.color = "§e"; // Couleur par défaut pour les classes avancées
        this.description = description;
        this.passiveEffects = passiveEffects;
        this.effectAmplifiers = effectAmplifiers;
        this.effectDurations = effectDurations;
        this.armorType = armorType;
        this.requiredRank = requiredRank;
        this.cost = cost;
        this.baseHealth = 100.0;
        this.baseDamage = 20.0;
        
        // Multiplicateurs de base selon la classe
        double[] multipliers = getMultipliersForClass(name);
        this.damageMultiplier = multipliers[0];
        this.defenseMultiplier = multipliers[1];
        this.manaMultiplier = multipliers[2];
        this.speedMultiplier = multipliers[3];
    }

    private double[] getMultipliersForClass(String className) {
        return switch (className) {
            case "Tank" -> new double[]{0.8, 2.0, 1.0, 0.8};
            case "Assassin" -> new double[]{2.0, 0.6, 1.2, 1.5};
            case "Mage" -> new double[]{1.5, 0.7, 2.0, 1.0};
            case "Soigneur" -> new double[]{0.7, 1.2, 1.8, 1.1};
            case "Berserker" -> new double[]{2.5, 0.5, 0.8, 1.3};
            case "Monarque des Ombres" -> new double[]{3.0, 1.5, 2.5, 1.4};
            case "Archer" -> new double[]{1.8, 0.8, 1.2, 1.2};
            case "Invocateur" -> new double[]{1.2, 1.0, 1.5, 1.0};
            default -> new double[]{1.0, 1.0, 1.0, 1.0};
        };
    }

    public String getDisplayName() {
        return color + displayName;
    }

    public String getColor() {
        return color;
    }

    public double getBaseHealth() {
        return baseHealth;
    }

    public double getBaseDamage() {
        return baseDamage;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public PotionEffectType[] getPassiveEffects() {
        return passiveEffects;
    }

    public int[] getEffectAmplifiers() {
        return effectAmplifiers;
    }

    public int[] getEffectDurations() {
        return effectDurations;
    }

    public Material getArmorType() {
        return armorType;
    }

    public double getDamageMultiplier() {
        return damageMultiplier;
    }

    public double getDefenseMultiplier() {
        return defenseMultiplier;
    }

    public double getManaMultiplier() {
        return manaMultiplier;
    }

    public double getSpeedMultiplier() {
        return speedMultiplier;
    }

    public PlayerRank getRequiredRank() {
        return requiredRank;
    }

    public int getCost() {
        return cost;
    }

    public boolean canUnlock(Player player, PlayerRank playerRank, int playerMoney) {
        return playerRank.getRequiredLevel() >= requiredRank.getRequiredLevel() && playerMoney >= cost;
    }

    public String getUnlockMessage() {
        return "§ePour débloquer la classe " + name + " :\n" +
               "§7- Rang requis : " + requiredRank.getDisplayName() + "\n" +
               "§7- Coût : " + cost + " pièces d'or";
    }

    public static HunterClass fromString(String name) {
        try {
            return HunterClass.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Override
    public String toString() {
        return name().toLowerCase();
    }
} 