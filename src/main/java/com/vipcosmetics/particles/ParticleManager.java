package com.vipcosmetics.particles;

import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ParticleManager {

    private static ParticleManager instance;
    private final Map<UUID, BukkitTask> tasks = new ConcurrentHashMap<>();
    private final Map<UUID, ParticleEffect> activeEffect = new ConcurrentHashMap<>();

    private ParticleManager() {}

    public static ParticleManager getInstance() {
        if (instance == null) instance = new ParticleManager();
        return instance;
    }

    public void startFor(UUID uuid, ParticleEffect effect) {
        stopFor(uuid);
        activeEffect.put(uuid, effect);
        Bukkit.getScheduler().runTaskLater(org.bukkit.Bukkit.getPluginManager().getPlugin("VIPCosmetics"), () -> {
            BukkitTask task = Bukkit.getScheduler().runTaskTimer(org.bukkit.Bukkit.getPluginManager().getPlugin("VIPCosmetics"),
                    () -> {
                        org.bukkit.entity.Player p = Bukkit.getPlayer(uuid);
                        if (p == null || !p.isOnline()) {
                            stopFor(uuid);
                            return;
                        }
                        // spawn optimized small circle
                        double r = 0.6;
                        int points = 8;
                        for (int i = 0; i < points; i++) {
                            double angle = 2 * Math.PI * i / points + (System.currentTimeMillis() % 10000) / 10000.0;
                            double x = r * Math.cos(angle);
                            double z = r * Math.sin(angle);
                            p.getWorld().spawnParticle(effect.getParticle(),
                                    p.getLocation().add(x, 1.0, z),
                                    1, 0, 0, 0, 0.0);
                        }
                    }, 0L, 10L);
            tasks.put(uuid, task);
        }, 1L);
    }

    public void stopFor(UUID uuid) {
        BukkitTask existing = tasks.remove(uuid);
        if (existing != null) existing.cancel();
        activeEffect.remove(uuid);
    }

    public void stopAll() {
        for (BukkitTask t : tasks.values()) {
            t.cancel();
        }
        tasks.clear();
        activeEffect.clear();
    }

    public boolean isActiveFor(UUID uuid, ParticleEffect eff) {
        ParticleEffect a = activeEffect.get(uuid);
        return a != null && a == eff;
    }

    public ParticleEffect getActive(UUID uuid) {
        return activeEffect.get(uuid);
    }
}
