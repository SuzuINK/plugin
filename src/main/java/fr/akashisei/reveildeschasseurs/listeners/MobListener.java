package fr.akashisei.reveildeschasseurs.listeners;

import fr.akashisei.reveildeschasseurs.ReveilDesChasseurs;
import fr.akashisei.reveildeschasseurs.models.PlayerData;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.ChatColor;

public class MobListener implements Listener {
    private final ReveilDesChasseurs plugin;

    public MobListener(ReveilDesChasseurs plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onMobDeath(EntityDeathEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof LivingEntity)) {
            return;
        }
        
        LivingEntity livingEntity = (LivingEntity) entity;
        Player killer = livingEntity.getKiller();
        
        if (killer == null) {
            return;
        }

        // Vérifier si c'est un mob MythicMobs
        ActiveMob mythicMob = MythicBukkit.inst().getMobManager().getActiveMob(entity.getUniqueId()).orElse(null);
        if (mythicMob == null) {
            return;
        }

        // Obtenir les données du joueur
        PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(killer.getUniqueId());
        
        // Calculer les récompenses en fonction du type de mob
        String mobType = mythicMob.getType().getInternalName();
        int expReward = 0;
        int moneyReward = 0;

        switch (mobType) {
            case "BossMob":
                expReward = 1000;
                moneyReward = 500;
                break;
            case "EliteMob":
                expReward = 500;
                moneyReward = 250;
                break;
            case "NormalMob":
                expReward = 100;
                moneyReward = 50;
                break;
        }

        // Donner les récompenses
        playerData.addExperience(expReward);
        playerData.addMoney(moneyReward);

        // Afficher les récompenses
        killer.sendMessage(ChatColor.GREEN + "Vous avez tué un " + ChatColor.GOLD + mobType + ChatColor.GREEN + "!");
        killer.sendMessage(ChatColor.WHITE + "XP gagné : " + ChatColor.GREEN + expReward);
        killer.sendMessage(ChatColor.WHITE + "Argent gagné : " + ChatColor.GREEN + moneyReward + " coins");

        // Sauvegarder les données du joueur
        plugin.getPlayerDataManager().savePlayerData(killer);
    }
} 