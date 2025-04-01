package fr.akashisei.reveildeschasseurs.managers;

import fr.akashisei.reveildeschasseurs.models.HunterClass;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ClassChangeManager {
    private final EconomyManager economyManager;
    private final PlayerRankManager rankManager;

    public ClassChangeManager(EconomyManager economyManager, PlayerRankManager rankManager) {
        this.economyManager = economyManager;
        this.rankManager = rankManager;
    }

    public void showAvailableClasses(Player player) {
        player.sendMessage(ChatColor.GOLD + "=== Classes disponibles ===");
        for (HunterClass hunterClass : HunterClass.values()) {
            if (rankManager.getPlayerRank(player).ordinal() >= hunterClass.getRequiredRank().ordinal()) {
                player.sendMessage(
                    hunterClass.getColor() + hunterClass.getDisplayName() + 
                    ChatColor.GRAY + " - Coût: " + ChatColor.GOLD + hunterClass.getCost() + " pièces"
                );
            }
        }
    }

    public boolean changeClass(Player player, HunterClass newClass) {
        if (rankManager.getPlayerRank(player).ordinal() < newClass.getRequiredRank().ordinal()) {
            player.sendMessage(ChatColor.RED + "Votre rang n'est pas assez élevé pour cette classe !");
            return false;
        }

        if (!economyManager.hasEnough(player, newClass.getCost())) {
            player.sendMessage(ChatColor.RED + "Vous n'avez pas assez de pièces pour changer de classe !");
            return false;
        }

        // Retirer l'argent
        economyManager.withdraw(player, newClass.getCost());

        // Appliquer les effets de base de la classe
        applyClassEffects(player, newClass);

        return true;
    }

    private void applyClassEffects(Player player, HunterClass hunterClass) {
        // Reset player stats
        player.setMaxHealth(20.0);
        player.setHealth(player.getMaxHealth());
        player.setFoodLevel(20);
        player.setSaturation(20);

        // Remove all potion effects
        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }

        // Apply class passive effects
        PotionEffectType[] passiveEffects = hunterClass.getPassiveEffects();
        for (PotionEffectType effectType : passiveEffects) {
            player.addPotionEffect(new PotionEffect(effectType, Integer.MAX_VALUE, 0, false, false));
        }
    }
} 