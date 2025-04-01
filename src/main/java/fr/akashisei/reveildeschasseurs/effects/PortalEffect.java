package fr.akashisei.reveildeschasseurs.effects;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;
import fr.akashisei.reveildeschasseurs.ReveilDesChasseurs;

public class PortalEffect {
    private final ReveilDesChasseurs plugin;
    private final Location location;
    private final Particle particle;
    private final Sound sound;
    private final int duration;
    private final int radius;
    private BukkitRunnable task;

    public PortalEffect(ReveilDesChasseurs plugin, Location location, Particle particle, Sound sound, int duration, int radius) {
        this.plugin = plugin;
        this.location = location;
        this.particle = particle;
        this.sound = sound;
        this.duration = duration;
        this.radius = radius;
    }

    public void play() {
        // Jouer le son
        location.getWorld().playSound(location, sound, 1.0f, 1.0f);

        // Créer l'effet de particules
        task = new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (ticks >= duration) {
                    this.cancel();
                    return;
                }

                // Créer un cercle de particules
                for (int i = 0; i < 360; i += 10) {
                    double angle = i * Math.PI / 180;
                    double x = radius * Math.cos(angle);
                    double z = radius * Math.sin(angle);
                    Location particleLoc = location.clone().add(x, 0, z);
                    particleLoc.getWorld().spawnParticle(particle, particleLoc, 1, 0, 0, 0, 0);
                }

                // Ajouter des particules verticales
                for (int i = 0; i < 5; i++) {
                    Location particleLoc = location.clone().add(0, 0.5 * i, 0);
                    particleLoc.getWorld().spawnParticle(particle, particleLoc, 1, 0, 0, 0, 0);
                }

                ticks++;
            }
        };
        task.runTaskTimer(plugin, 0L, 1L);
    }

    public void stop() {
        if (task != null && !task.isCancelled()) {
            task.cancel();
        }
    }
} 