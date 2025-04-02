package fr.akashisei.reveildeschasseurs.utils;

import fr.akashisei.reveildeschasseurs.ReveilDesChasseurs;
import fr.akashisei.reveildeschasseurs.quests.Quest;
import fr.akashisei.reveildeschasseurs.quests.QuestReward;
import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class QuestNotificationManager {
    private final ReveilDesChasseurs plugin;

    public QuestNotificationManager(ReveilDesChasseurs plugin) {
        this.plugin = plugin;
    }

    public void showQuestAccepted(Player player, Quest quest) {
        // Effets sonores et visuels
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
        player.spawnParticle(Particle.VILLAGER_HAPPY, player.getLocation().add(0, 2, 0), 20, 0.5, 0.5, 0.5, 0);

        // Messages
        player.sendMessage(ChatColor.GOLD + "[Quête] " + ChatColor.GREEN + "Vous avez accepté la quête : " + 
            ChatColor.YELLOW + quest.getName());
        player.sendMessage(ChatColor.GRAY + quest.getDescription());
        player.sendMessage(ChatColor.GRAY + "Niveau requis : " + ChatColor.YELLOW + quest.getRequiredLevel());
        player.sendMessage(ChatColor.GRAY + "Type : " + ChatColor.YELLOW + quest.getType().getDisplayName());
        player.sendMessage(ChatColor.GRAY + "Objectif : " + ChatColor.YELLOW + "0" + ChatColor.GRAY + "/" + 
            ChatColor.YELLOW + quest.getRequired() + ChatColor.GRAY);
    }

    public void showProgressUpdate(Player player, Quest quest, int progress, int required) {
        // Effets sonores et visuels
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5f, 1.0f);
        player.spawnParticle(Particle.END_ROD, player.getLocation().add(0, 2, 0), 10, 0.3, 0.3, 0.3, 0);

        // Message de progression
        player.sendMessage(ChatColor.GOLD + "[Quête] " + ChatColor.GRAY + "Progression de " + 
            ChatColor.YELLOW + quest.getName() + ChatColor.GRAY + " : " + 
            ChatColor.YELLOW + progress + ChatColor.GRAY + "/" + 
            ChatColor.YELLOW + required);
    }

    public void showQuestComplete(Player player, Quest quest) {
        // Effets sonores et visuels
        player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.0f);
        player.spawnParticle(Particle.TOTEM, player.getLocation().add(0, 2, 0), 50, 0.5, 0.5, 0.5, 0.1);

        // Messages
        player.sendMessage(ChatColor.GOLD + "[Quête] " + ChatColor.GREEN + "Vous avez terminé la quête : " + 
            ChatColor.YELLOW + quest.getName());
        
        QuestReward reward = quest.getRewardReward();
        player.sendMessage(ChatColor.GOLD + "Récompenses :");
        player.sendMessage(ChatColor.GRAY + "- " + ChatColor.YELLOW + reward.getBaseExp() + 
            ChatColor.GRAY + " points d'expérience");
        player.sendMessage(ChatColor.GRAY + "- " + ChatColor.YELLOW + reward.getBaseMoney() + 
            ChatColor.GRAY + " pièces");

        // Afficher les items garantis
        for (ItemStack item : reward.getGuaranteedItems()) {
            player.sendMessage(ChatColor.GRAY + "- " + ChatColor.YELLOW + item.getAmount() + "x " + 
                formatItemName(item));
        }
    }

    private String formatItemName(ItemStack item) {
        String name = item.getType().name().toLowerCase().replace("_", " ");
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    public void showQuestFailed(Player player, Quest quest) {
        // Son d'échec
        player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);

        // Particules d'échec
        player.spawnParticle(Particle.SMOKE_NORMAL, player.getLocation().add(0, 2, 0), 20, 0.5, 0.5, 0.5, 0.1);

        // Message d'échec
        player.sendMessage(ChatColor.RED + "Vous avez échoué la quête " + ChatColor.YELLOW + quest.getName() + 
            ChatColor.RED + " !");
    }
} 