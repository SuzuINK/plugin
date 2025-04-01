package fr.akashisei.reveildeschasseurs.listeners;

import fr.akashisei.reveildeschasseurs.ReveilDesChasseurs;
import fr.akashisei.reveildeschasseurs.dungeon.DungeonInstance;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class DungeonListener implements Listener {
    private final ReveilDesChasseurs plugin;

    public DungeonListener(ReveilDesChasseurs plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        DungeonInstance instance = plugin.getDungeonManager().getPlayerInstance(player);
        
        if (instance != null) {
            checkTraps(player, instance);
        }
    }

    private void checkTraps(Player player, DungeonInstance instance) {
        Location playerLoc = player.getLocation();
        Block block = playerLoc.getBlock();
        Material blockType = block.getType();

        // Vérifier les pièges
        switch (blockType) {
            case LAVA:
                player.damage(4.0); // Dégâts de lave
                break;
            case TNT:
                block.setType(Material.AIR);
                World world = player.getWorld();
                TNTPrimed tnt = world.spawn(playerLoc, TNTPrimed.class);
                tnt.setFuseTicks(40); // 2 secondes
                break;
            case DISPENSER:
                // Tirer des flèches
                World w = player.getWorld();
                Arrow arrow = w.spawnArrow(playerLoc.add(0, 2, 0), new Vector(0, -1, 0), 1.5f, 12.0f);
                arrow.setDamage(4.0);
                break;
            case SOUL_SAND:
                // Effet de poison
                player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 100, 1));
                break;
            case END_PORTAL_FRAME:
                // Téléporter aléatoirement
                Location randomLoc = getRandomLocation(instance);
                player.teleport(randomLoc);
                break;
            default:
                // Aucun piège pour les autres types de blocs
                break;
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        
        Player player = (Player) event.getEntity();
        DungeonInstance instance = plugin.getDungeonManager().getPlayerInstance(player);
        
        if (instance != null) {
            // Si le joueur meurt dans le donjon
            if (player.getHealth() - event.getFinalDamage() <= 0) {
                plugin.getDungeonManager().endDungeon(player, false);
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) return;
        
        Player player = (Player) event.getDamager();
        DungeonInstance instance = plugin.getDungeonManager().getPlayerInstance(player);
        
        if (instance != null) {
            // Augmenter les dégâts dans le donjon
            event.setDamage(event.getDamage() * 1.5);
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity().getKiller() == null) return;
        
        Player player = event.getEntity().getKiller();
        DungeonInstance instance = plugin.getDungeonManager().getPlayerInstance(player);
        
        if (instance != null) {
            // Augmenter les drops dans le donjon
            for (ItemStack drop : event.getDrops()) {
                drop.setAmount(drop.getAmount() * 2);
            }
            
            // Donner des effets de potion aléatoires
            PotionEffectType[] effects = {
                PotionEffectType.SPEED,
                PotionEffectType.INCREASE_DAMAGE,
                PotionEffectType.REGENERATION,
                PotionEffectType.DAMAGE_RESISTANCE
            };
            
            player.addPotionEffect(new PotionEffect(
                effects[(int)(Math.random() * effects.length)],
                200, // 10 secondes
                1
            ));
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        DungeonInstance instance = plugin.getDungeonManager().getPlayerInstance(player);
        
        if (instance != null) {
            plugin.getDungeonManager().endDungeon(player, false);
        }
    }

    private Location getRandomLocation(DungeonInstance instance) {
        Location spawnPoint = instance.getSpawnPoint();
        int radius = 20; // Rayon de 20 blocs autour du point de spawn
        
        int x = spawnPoint.getBlockX() + (int)(Math.random() * radius * 2) - radius;
        int z = spawnPoint.getBlockZ() + (int)(Math.random() * radius * 2) - radius;
        
        return new Location(spawnPoint.getWorld(), x, spawnPoint.getY(), z);
    }
} 