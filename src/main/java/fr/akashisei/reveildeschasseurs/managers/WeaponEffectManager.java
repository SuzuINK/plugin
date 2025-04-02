package fr.akashisei.reveildeschasseurs.managers;

import fr.akashisei.reveildeschasseurs.ReveilDesChasseurs;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class WeaponEffectManager {
    private final ReveilDesChasseurs plugin;
    private final Map<String, WeaponEffect> effects;
    private final Random random;

    public WeaponEffectManager(ReveilDesChasseurs plugin) {
        this.plugin = plugin;
        this.effects = new HashMap<>();
        this.random = new Random();
        registerEffects();
    }

    private void registerEffects() {
        // Effets des armes de rang S
        effects.put("DARKNESS_BURST", new WeaponEffect(
            (player, target) -> {
                // Effet d'explosion de ténèbres
                Location loc = target.getLocation();
                player.getWorld().spawnParticle(Particle.SMOKE_LARGE, loc, 50, 1, 1, 1, 0.1);
                player.getWorld().spawnParticle(Particle.DRAGON_BREATH, loc, 30, 1, 1, 1, 0.05);
                player.getWorld().playSound(loc, Sound.ENTITY_WITHER_SHOOT, 1.0f, 0.5f);
                
                // Dégâts massifs et effet de faiblesse
                target.getWorld().getNearbyEntities(loc, 3, 3, 3).forEach(entity -> {
                    if (entity instanceof LivingEntity && entity != player) {
                        LivingEntity livingEntity = (LivingEntity) entity;
                        EntityDamageByEntityEvent damageEvent = new EntityDamageByEntityEvent(player, livingEntity, DamageCause.CUSTOM, 12.0);
                        plugin.getServer().getPluginManager().callEvent(damageEvent);
                        if (!damageEvent.isCancelled()) {
                            livingEntity.damage(damageEvent.getDamage());
                        }
                        // Effet de faiblesse pendant 5 secondes
                        livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 100, 1));
                        // Dégâts supplémentaires basés sur la santé manquante de la cible
                        double maxHealth = livingEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
                        double missingHealth = maxHealth - livingEntity.getHealth();
                        EntityDamageByEntityEvent bonusDamageEvent = new EntityDamageByEntityEvent(player, livingEntity, DamageCause.CUSTOM, missingHealth * 0.2);
                        plugin.getServer().getPluginManager().callEvent(bonusDamageEvent);
                        if (!bonusDamageEvent.isCancelled()) {
                            livingEntity.damage(bonusDamageEvent.getDamage());
                        }
                    }
                });
            },
            20 * 15 // Cooldown augmenté à 15 secondes vu la puissance
        ));

        effects.put("SOUL_HARVEST", new WeaponEffect(
            (player, target) -> {
                // Effet de vol d'âme amélioré
                Location loc = target.getLocation();
                Location playerLoc = player.getLocation();
                
                // Dégâts initiaux
                if (target instanceof LivingEntity) {
                    double maxHealth = ((LivingEntity) target).getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
                    double currentHealth = ((LivingEntity) target).getHealth();
                    double damagePercent = (maxHealth - currentHealth) / maxHealth; // % de vie manquante
                    double damage = 8.0 + (damagePercent * 8.0); // 8-16 dégâts selon la vie manquante
                    EntityDamageByEntityEvent damageEvent = new EntityDamageByEntityEvent(player, target, DamageCause.CUSTOM, damage);
                    plugin.getServer().getPluginManager().callEvent(damageEvent);
                    if (!damageEvent.isCancelled()) {
                        ((LivingEntity) target).damage(damageEvent.getDamage());
                    }
                }
                
                new BukkitRunnable() {
                    double t = 0;
                    int ticks = 0;
                    public void run() {
                        t += 0.05;
                        ticks++;
                        Vector direction = playerLoc.toVector().subtract(loc.toVector());
                        Location current = loc.clone().add(direction.multiply(t));
                        
                        player.getWorld().spawnParticle(Particle.SOUL, current, 1, 0, 0, 0, 0);
                        
                        // Dégâts continus
                        if (ticks % 10 == 0 && target instanceof LivingEntity) { // Tous les 0.5 secondes
                            EntityDamageByEntityEvent tickDamageEvent = new EntityDamageByEntityEvent(player, target, DamageCause.CUSTOM, 2.0);
                            plugin.getServer().getPluginManager().callEvent(tickDamageEvent);
                            if (!tickDamageEvent.isCancelled()) {
                                ((LivingEntity) target).damage(tickDamageEvent.getDamage());
                            }
                        }
                        
                        if (t > 1) {
                            // Soins basés sur les dégâts infligés
                            double maxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
                            double healAmount = Math.min(8.0, maxHealth - player.getHealth());
                            player.setHealth(Math.min(maxHealth, player.getHealth() + healAmount));
                            // Bonus de régénération
                            player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 60, 1));
                            this.cancel();
                        }
                    }
                }.runTaskTimer(plugin, 0L, 1L);
            },
            20 * 20 // Cooldown de 20 secondes
        ));

        // Effets des armes de rang A
        effects.put("WIND_SLASH", new WeaponEffect(
            (player, target) -> {
                // Effet de tranche de vent amélioré
                Vector direction = player.getLocation().getDirection();
                Location loc = player.getLocation().add(0, 1.5, 0);
                
                new BukkitRunnable() {
                    double t = 0;
                    public void run() {
                        t += 0.5;
                        double x = direction.getX() * t;
                        double z = direction.getZ() * t;
                        Location current = loc.clone().add(x, 0, z);
                        
                        player.getWorld().spawnParticle(Particle.CLOUD, current, 10, 0.2, 0.2, 0.2, 0.1);
                        player.getWorld().spawnParticle(Particle.SWEEP_ATTACK, current, 1, 0, 0, 0, 0);
                        
                        // Dégâts progressifs
                        current.getWorld().getNearbyEntities(current, 1, 1, 1).forEach(entity -> {
                            if (entity instanceof LivingEntity && entity != player) {
                                LivingEntity livingEntity = (LivingEntity) entity;
                                double baseDamage = 7.0;
                                double speedMultiplier = 1.0 + (t * 0.1); // Augmente avec la distance
                                EntityDamageByEntityEvent damageEvent = new EntityDamageByEntityEvent(player, livingEntity, DamageCause.CUSTOM, baseDamage * speedMultiplier);
                                plugin.getServer().getPluginManager().callEvent(damageEvent);
                                if (!damageEvent.isCancelled()) {
                                    livingEntity.damage(damageEvent.getDamage());
                                }
                                // Ralentissement
                                livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 40, 1));
                                // Effet de knockback
                                Vector knockback = direction.clone().multiply(0.5);
                                livingEntity.setVelocity(livingEntity.getVelocity().add(knockback));
                            }
                        });
                        
                        if (t >= 5) {
                            this.cancel();
                        }
                    }
                }.runTaskTimer(plugin, 0L, 1L);
            },
            20 * 10 // Cooldown de 10 secondes
        ));

        effects.put("FLAME_BURST", new WeaponEffect(
            (player, target) -> {
                // Effet d'explosion de flammes amélioré
                Location loc = target.getLocation();
                player.getWorld().spawnParticle(Particle.FLAME, loc, 40, 1, 1, 1, 0.1);
                player.getWorld().spawnParticle(Particle.LAVA, loc, 10, 0.5, 0.5, 0.5, 0.1);
                player.getWorld().playSound(loc, Sound.ENTITY_BLAZE_SHOOT, 1.0f, 1.0f);
                
                // Dégâts de zone avec effets
                target.getWorld().getNearbyEntities(loc, 3, 3, 3).forEach(entity -> {
                    if (entity instanceof LivingEntity && entity != player) {
                        LivingEntity livingEntity = (LivingEntity) entity;
                        // Dégâts de base
                        EntityDamageByEntityEvent damageEvent = new EntityDamageByEntityEvent(player, livingEntity, DamageCause.CUSTOM, 6.0);
                        plugin.getServer().getPluginManager().callEvent(damageEvent);
                        if (!damageEvent.isCancelled()) {
                            livingEntity.damage(damageEvent.getDamage());
                        }
                        // Brûlure prolongée
                        livingEntity.setFireTicks(200); // 10 secondes de feu
                        // Dégâts supplémentaires si déjà en feu
                        if (livingEntity.getFireTicks() > 0) {
                            EntityDamageByEntityEvent bonusDamageEvent = new EntityDamageByEntityEvent(player, livingEntity, DamageCause.CUSTOM, 4.0);
                            plugin.getServer().getPluginManager().callEvent(bonusDamageEvent);
                            if (!bonusDamageEvent.isCancelled()) {
                                livingEntity.damage(bonusDamageEvent.getDamage());
                            }
                        }
                        // Effet de faiblesse dû à la chaleur
                        livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 100, 0));
                    }
                });
            },
            20 * 12 // Cooldown de 12 secondes
        ));

        // Effets des armes de rang B
        effects.put("LIGHT_BURST", new WeaponEffect(
            (player, target) -> {
                // Effet d'explosion de lumière amélioré
                Location loc = target.getLocation();
                player.getWorld().spawnParticle(Particle.END_ROD, loc, 30, 1, 1, 1, 0.1);
                player.getWorld().playSound(loc, Sound.BLOCK_BEACON_ACTIVATE, 1.0f, 2.0f);
                
                // Dégâts et effets
                target.getWorld().getNearbyEntities(loc, 4, 4, 4).forEach(entity -> {
                    if (entity instanceof LivingEntity && entity != player) {
                        LivingEntity livingEntity = (LivingEntity) entity;
                        // Dégâts de base
                        EntityDamageByEntityEvent damageEvent = new EntityDamageByEntityEvent(player, livingEntity, DamageCause.CUSTOM, 4.0);
                        plugin.getServer().getPluginManager().callEvent(damageEvent);
                        if (!damageEvent.isCancelled()) {
                            livingEntity.damage(damageEvent.getDamage());
                        }
                        // Aveuglement temporaire
                        livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 0));
                        // Glowing pour traquer les ennemis
                        livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 100, 0));
                    }
                });
            },
            20 * 15 // Cooldown de 15 secondes
        ));
    }

    public void triggerEffect(String effectId, Player player, LivingEntity target) {
        WeaponEffect effect = effects.get(effectId);
        if (effect != null) {
            effect.execute(player, target);
        }
    }

    private static class WeaponEffect {
        private final EffectAction action;
        private final int cooldown;

        public WeaponEffect(EffectAction action, int cooldown) {
            this.action = action;
            this.cooldown = cooldown;
        }

        public void execute(Player player, LivingEntity target) {
            action.execute(player, target);
        }

        public int getCooldown() {
            return cooldown;
        }
    }

    @FunctionalInterface
    private interface EffectAction {
        void execute(Player player, LivingEntity target);
    }
} 