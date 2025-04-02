package fr.akashisei.reveildeschasseurs.managers;

import fr.akashisei.reveildeschasseurs.ReveilDesChasseurs;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;

import java.util.*;

public class MobManager {
    private final ReveilDesChasseurs plugin;
    private final MythicBukkit mythicMobs;
    private final Map<String, List<Entity>> dungeonMobs;
    private final Map<String, List<Entity>> questMobs;

    public MobManager(ReveilDesChasseurs plugin) {
        this.plugin = plugin;
        this.mythicMobs = MythicBukkit.inst();
        this.dungeonMobs = new HashMap<>();
        this.questMobs = new HashMap<>();
    }

    public Entity spawnMob(String mobId, Location location, int level) {
        Optional<MythicMob> mythicMob = mythicMobs.getMobManager().getMythicMob(mobId);
        if (mythicMob.isPresent()) {
            ActiveMob activeMob = mythicMobs.getMobManager().spawnMob(
                mobId,
                BukkitAdapter.adapt(location),
                level
            );
            if (activeMob != null) {
                return activeMob.getEntity().getBukkitEntity();
            }
        }
        return null;
    }

    public void spawnDungeonMob(String dungeonId, String mobId, Location location, int level) {
        Entity entity = spawnMob(mobId, location, level);
        if (entity != null) {
            dungeonMobs.computeIfAbsent(dungeonId, k -> new ArrayList<>()).add(entity);
        }
    }

    public void spawnQuestMob(String questId, String mobId, Location location, int level) {
        Entity entity = spawnMob(mobId, location, level);
        if (entity != null) {
            questMobs.computeIfAbsent(questId, k -> new ArrayList<>()).add(entity);
        }
    }

    public void removeDungeonMobs(String dungeonId) {
        List<Entity> mobs = dungeonMobs.remove(dungeonId);
        if (mobs != null) {
            mobs.forEach(Entity::remove);
        }
    }

    public void removeQuestMobs(String questId) {
        List<Entity> mobs = questMobs.remove(questId);
        if (mobs != null) {
            mobs.forEach(Entity::remove);
        }
    }

    public boolean isMythicMob(Entity entity) {
        return mythicMobs.getMobManager().isActiveMob(BukkitAdapter.adapt(entity));
    }

    public String getMobId(Entity entity) {
        if (isMythicMob(entity)) {
            Optional<ActiveMob> activeMob = mythicMobs.getMobManager().getActiveMob(entity.getUniqueId());
            return activeMob.map(ActiveMob::getMobType).orElse(null);
        }
        return null;
    }

    public int getMobLevel(Entity entity) {
        if (isMythicMob(entity)) {
            return mythicMobs.getMobManager()
                .getActiveMob(entity.getUniqueId())
                .map(mob -> (int) Math.round(mob.getLevel()))
                .orElse(1);
        }
        return 1;
    }

    public void setMobHealth(Entity entity, double health) {
        if (entity instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity) entity;
            livingEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(health);
            livingEntity.setHealth(health);
        }
    }

    public void setMobDamage(Entity entity, double damage) {
        if (entity instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity) entity;
            livingEntity.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(damage);
        }
    }

    public void setMobTarget(Entity entity, Player target) {
        if (entity instanceof Mob) {
            ((Mob) entity).setTarget(target);
        }
    }
} 