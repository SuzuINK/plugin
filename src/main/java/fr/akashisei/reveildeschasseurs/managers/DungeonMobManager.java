package fr.akashisei.reveildeschasseurs.managers;

import fr.akashisei.reveildeschasseurs.ReveilDesChasseurs;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.attribute.Attribute;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DungeonMobManager {
    private final ReveilDesChasseurs plugin;
    private final Map<String, FileConfiguration> mobConfigs;
    private final Map<String, FileConfiguration> dungeonConfigs;

    public DungeonMobManager(ReveilDesChasseurs plugin) {
        this.plugin = plugin;
        this.mobConfigs = new HashMap<>();
        this.dungeonConfigs = new HashMap<>();
        loadConfigurations();
    }

    private void loadConfigurations() {
        // Charger la configuration des mobs
        File mobsFile = new File(plugin.getDataFolder(), "mobs/mobs.yml");
        if (mobsFile.exists()) {
            mobConfigs.put("mobs", YamlConfiguration.loadConfiguration(mobsFile));
        }

        // Charger la configuration des donjons
        File dungeonsFile = new File(plugin.getDataFolder(), "dungeons/dungeons.yml");
        if (dungeonsFile.exists()) {
            dungeonConfigs.put("dungeons", YamlConfiguration.loadConfiguration(dungeonsFile));
        }
    }

    public void spawnMob(String mobId, Location location) {
        FileConfiguration mobConfig = mobConfigs.get("mobs");
        if (mobConfig == null) return;

        String rank = mobConfig.getString("mobs." + mobId + ".rank");
        double health = mobConfig.getDouble("mobs." + mobId + ".health");
        double damage = mobConfig.getDouble("mobs." + mobId + ".damage");
        String skin = mobConfig.getString("mobs." + mobId + ".skin");
        List<String> effects = mobConfig.getStringList("mobs." + mobId + ".effects");

        // Créer l'entité
        Entity entity = location.getWorld().spawnEntity(location, EntityType.ZOMBIE); // Base entity type
        if (!(entity instanceof LivingEntity)) return;
        LivingEntity livingEntity = (LivingEntity) entity;

        // Appliquer les attributs
        livingEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(health);
        livingEntity.setHealth(health);
        livingEntity.setCustomName(mobConfig.getString("mobs." + mobId + ".name"));
        livingEntity.setCustomNameVisible(true);

        // Appliquer les métadonnées
        livingEntity.setMetadata("mobId", new FixedMetadataValue(plugin, mobId));
        livingEntity.setMetadata("rank", new FixedMetadataValue(plugin, rank));
        livingEntity.setMetadata("damage", new FixedMetadataValue(plugin, damage));

        // Appliquer le skin
        applySkin(livingEntity, skin);

        // Appliquer les effets visuels
        applyEffects(livingEntity, effects, rank);
    }

    private void applySkin(LivingEntity entity, String skinName) {
        // Utiliser LibsDisguises pour appliquer le skin
        try {
            // Code pour appliquer le skin avec LibsDisguises
            plugin.getLogger().info("Application du skin: " + skinName);
        } catch (Exception e) {
            plugin.getLogger().warning("Erreur lors de l'application du skin: " + e.getMessage());
        }
    }

    private void applyEffects(LivingEntity entity, List<String> effects, String rank) {
        FileConfiguration mobConfig = mobConfigs.get("mobs");
        if (mobConfig == null) return;

        // Récupérer les paramètres d'effets du rang
        double auraSize = mobConfig.getDouble("rank_effects." + rank + ".aura_size");
        String particleDensity = mobConfig.getString("rank_effects." + rank + ".particle_density");

        // Appliquer les effets
        for (String effect : effects) {
            String particleType = mobConfig.getString("effects." + effect + ".particle");
            String color = mobConfig.getString("effects." + effect + ".color");

            // Créer un effet persistant
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (entity.isDead() || !entity.isValid()) {
                        this.cancel();
                        return;
                    }
                    // Appliquer l'effet de particules
                    applyParticleEffect(entity, particleType, color, auraSize, particleDensity);
                }
            }.runTaskTimer(plugin, 0L, 5L);
        }
    }

    private void applyParticleEffect(LivingEntity entity, String particleType, String color, double size, String density) {
        Location entityLoc = entity.getLocation();
        // Utiliser l'API de particules de Bukkit pour créer les effets visuels
        entity.getWorld().spawnParticle(Particle.valueOf(particleType), entityLoc, 10, size, size, size, 0.1);
    }

    public void spawnDungeonMobs(String dungeonId, Location center) {
        FileConfiguration dungeonConfig = dungeonConfigs.get("dungeons");
        if (dungeonConfig == null) return;

        String boss = dungeonConfig.getString("dungeons." + dungeonId + ".boss");
        List<String> mobs = dungeonConfig.getStringList("dungeons." + dungeonId + ".mobs");

        // Spawn le boss
        if (boss != null) {
            Location bossLocation = center.clone().add(0, 0, 5);
            spawnMob(boss.toLowerCase(), bossLocation);
        }

        // Spawn les mobs
        if (mobs != null) {
            for (String mob : mobs) {
                // Générer une position aléatoire autour du centre
                double angle = Math.random() * 2 * Math.PI;
                double radius = 5 + Math.random() * 10;
                double x = center.getX() + radius * Math.cos(angle);
                double z = center.getZ() + radius * Math.sin(angle);
                Location mobLoc = new Location(center.getWorld(), x, center.getY(), z);
                
                spawnMob(mob.toLowerCase(), mobLoc);
            }
        }
    }
} 