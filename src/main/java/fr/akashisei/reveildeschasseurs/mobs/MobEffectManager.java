package fr.akashisei.reveildeschasseurs.mobs;

import fr.akashisei.reveildeschasseurs.ReveilDesChasseurs;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MobEffectManager {
    private final ReveilDesChasseurs plugin;
    private final Random random;
    private final Map<String, MobEffect> mobEffects;

    public MobEffectManager(ReveilDesChasseurs plugin) {
        this.plugin = plugin;
        this.random = new Random();
        this.mobEffects = new HashMap<>();
        initializeEffects();
    }

    private void initializeEffects() {
        // Effets pour les monstres de rang S
        mobEffects.put("SoloLeveling_Monarch", new MobEffect(
            new PotionEffect[]{
                new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 100, 3),
                new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 100, 2)
            },
            Particle.DRAGON_BREATH,
            "§5§lAura Monarchique",
            (player) -> {
                player.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, 100, 1));
                player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 60, 1));
            }
        ));

        // Effets pour les monstres de rang A
        mobEffects.put("SoloLeveling_HighOrcLord", new MobEffect(
            new PotionEffect[]{
                new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 100, 2),
                new PotionEffect(PotionEffectType.SPEED, 100, 1)
            },
            Particle.VILLAGER_ANGRY,
            "§c§lRage Bestiale",
            (player) -> player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 40, 1))
        ));

        // Effets pour les mages noirs
        mobEffects.put("SoloLeveling_DarkMage", new MobEffect(
            new PotionEffect[]{
                new PotionEffect(PotionEffectType.WEAKNESS, 100, 1),
                new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 100, 1)
            },
            Particle.SPELL_WITCH,
            "§5Sort Obscur",
            (player) -> {
                player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 0));
                player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 60, 0));
            }
        ));

        // Effets pour les gobelins
        mobEffects.put("SoloLeveling_Goblin", new MobEffect(
            new PotionEffect[]{
                new PotionEffect(PotionEffectType.SPEED, 100, 1)
            },
            Particle.SMOKE_NORMAL,
            "§7Agilité Gobeline",
            null
        ));
    }

    public void applyMobEffect(ActiveMob mythicMob) {
        String mobType = mythicMob.getMobType();
        MobEffect effect = mobEffects.get(mobType);
        
        if (effect != null && mythicMob.getEntity().getBukkitEntity() instanceof LivingEntity entity) {
            // Appliquer les effets de potion
            for (PotionEffect potionEffect : effect.getPotionEffects()) {
                entity.addPotionEffect(potionEffect);
            }

            // Créer des particules
            Location loc = entity.getLocation();
            new BukkitRunnable() {
                int ticks = 0;
                @Override
                public void run() {
                    if (ticks >= 100 || entity.isDead()) {
                        this.cancel();
                        return;
                    }
                    
                    loc.getWorld().spawnParticle(
                        effect.getParticleEffect(),
                        loc.add(0, 1, 0),
                        10,
                        0.5, 0.5, 0.5,
                        0.1
                    );
                    
                    // Vérifier les joueurs à proximité pour les effets de zone
                    if (effect.getPlayerEffect() != null) {
                        for (Player player : loc.getWorld().getPlayers()) {
                            if (player.getLocation().distance(loc) <= 5) {
                                effect.getPlayerEffect().accept(player);
                            }
                        }
                    }
                    
                    ticks++;
                }
            }.runTaskTimer(plugin, 0L, 2L);
        }
    }

    private static class MobEffect {
        private final PotionEffect[] potionEffects;
        private final Particle particleEffect;
        private final String effectName;
        private final java.util.function.Consumer<Player> playerEffect;

        public MobEffect(PotionEffect[] potionEffects, Particle particleEffect, String effectName, 
                        java.util.function.Consumer<Player> playerEffect) {
            this.potionEffects = potionEffects;
            this.particleEffect = particleEffect;
            this.effectName = effectName;
            this.playerEffect = playerEffect;
        }

        public PotionEffect[] getPotionEffects() {
            return potionEffects;
        }

        public Particle getParticleEffect() {
            return particleEffect;
        }

        public String getEffectName() {
            return effectName;
        }

        public java.util.function.Consumer<Player> getPlayerEffect() {
            return playerEffect;
        }
    }
} 