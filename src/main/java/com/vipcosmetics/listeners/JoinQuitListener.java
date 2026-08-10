package com.vipcosmetics.listeners;

import com.vipcosmetics.Main;
import com.vipcosmetics.particles.ParticleEffect;
import com.vipcosmetics.particles.ParticleManager;
import com.vipcosmetics.player.VIPPlayerData;
import com.vipcosmetics.storage.StorageManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;

public class JoinQuitListener implements Listener {

    private final Main plugin;
    private final StorageManager storage;

    public JoinQuitListener(Main plugin) {
        this.plugin = plugin;
        this.storage = plugin.getStorage();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        UUID id = p.getUniqueId();
        // Apply VIP visuals if VIP
        if (storage.isVip(id)) {
            storage.applyVisualsIfOnline(id);
        } else {
            // Ensure no particle running if not VIP
            ParticleManager.getInstance().stopFor(id);
        }
    }
}
