package com.vipcosmetics.storage;

import com.vipcosmetics.Main;
import com.vipcosmetics.particles.ParticleEffect;
import com.vipcosmetics.particles.ParticleManager;
import com.vipcosmetics.player.VIPPlayerData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class StorageManager {

    private final Main plugin;
    private File playerFile;
    private YamlConfiguration playerCfg;

    private File vipFile;
    private YamlConfiguration vipCfg;

    public StorageManager(Main plugin) {
        this.plugin = plugin;
        setupFiles();
    }

    private void setupFiles() {
        if (!plugin.getDataFolder().exists()) plugin.getDataFolder().mkdirs();
        playerFile = new File(plugin.getDataFolder(), "players.yml");
        vipFile = new File(plugin.getDataFolder(), "vip.yml");
        try {
            if (!playerFile.exists()) playerFile.createNewFile();
            if (!vipFile.exists()) vipFile.createNewFile();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        playerCfg = YamlConfiguration.loadConfiguration(playerFile);
        vipCfg = YamlConfiguration.loadConfiguration(vipFile);
    }

    public void loadAll() {
        playerCfg = YamlConfiguration.loadConfiguration(playerFile);
        vipCfg = YamlConfiguration.loadConfiguration(vipFile);
    }

    public void saveAll() {
        try {
            playerCfg.save(playerFile);
            vipCfg.save(vipFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public VIPPlayerData getPlayerData(UUID uuid) {
        String path = uuid.toString();
        if (!playerCfg.contains(path)) return null;
        VIPPlayerData d = new VIPPlayerData();
        String nameCol = playerCfg.getString(path + ".nameColor", null);
        if (nameCol != null) {
            try { d.setNameColor(ChatColor.valueOf(nameCol)); } catch (Exception ignored) {}
        }
        String chatCol = playerCfg.getString(path + ".chatColor", null);
        if (chatCol != null) {
            try { d.setChatColor(ChatColor.valueOf(chatCol)); } catch (Exception ignored) {}
        }
        String particle = playerCfg.getString(path + ".particle", null);
        if (particle != null) {
            try { d.setParticle(ParticleEffect.valueOf(particle)); } catch (Exception ignored) {}
        }
        return d;
    }

    public void setPlayerData(UUID uuid, VIPPlayerData data) {
        String path = uuid.toString();
        if (data.getNameColor() != null) playerCfg.set(path + ".nameColor", data.getNameColor().name());
        else playerCfg.set(path + ".nameColor", null);
        if (data.getChatColor() != null) playerCfg.set(path + ".chatColor", data.getChatColor().name());
        else playerCfg.set(path + ".chatColor", null);
        if (data.getParticle() != null) playerCfg.set(path + ".particle", data.getParticle().name());
        else playerCfg.set(path + ".particle", null);
        saveAll();
    }

    public void setPlayerParticle(UUID uuid, ParticleEffect eff) {
        VIPPlayerData d = getPlayerData(uuid);
        if (d == null) d = new VIPPlayerData();
        d.setParticle(eff);
        setPlayerData(uuid, d);
        // start/stop tasks
        if (eff == null) ParticleManager.getInstance().stopFor(uuid);
        else ParticleManager.getInstance().startFor(uuid, eff);
    }

    public void addVip(UUID uuid) {
        List<String> list = vipCfg.getStringList("vips");
        if (!list.contains(uuid.toString())) {
            list.add(uuid.toString());
            vipCfg.set("vips", list);
            saveAll();
        }
    }

    public void removeVip(UUID uuid) {
        List<String> list = vipCfg.getStringList("vips");
        if (list.contains(uuid.toString())) {
            list.remove(uuid.toString());
            vipCfg.set("vips", list);
            saveAll();
        }
    }

    public boolean isVip(UUID uuid) {
        List<String> list = vipCfg.getStringList("vips");
        return list.contains(uuid.toString());
    }

    public List<UUID> getVipList() {
        return vipCfg.getStringList("vips").stream().map(UUID::fromString).collect(Collectors.toList());
    }

    public void applyVisualsIfOnline(UUID uuid) {
        Player p = Bukkit.getPlayer(uuid);
        if (p == null) return;
        VIPPlayerData data = getPlayerData(uuid);
        // scoreboard team for name color and prefix
        Scoreboard sb = Bukkit.getScoreboardManager().getMainScoreboard();
        String teamName = "vip_" + uuid.toString().substring(0, 15);
        Team team = sb.getTeam(teamName);
        if (team == null) team = sb.registerNewTeam(teamName);
        team.setPrefix(org.bukkit.ChatColor.GOLD + "[VIP] ");
        if (data != null && data.getNameColor() != null) {
            team.setColor(data.getNameColor());
            team.setSuffix("");
        } else {
            team.setColor(org.bukkit.ChatColor.WHITE);
            team.setSuffix("");
        }
        // ensure player is added
        if (!team.hasEntry(p.getName())) team.addEntry(p.getName());
        // player list name
        String display = ChatColor.GOLD + "[VIP] " + (data != null && data.getNameColor() != null ? data.getNameColor().toString() : "") + p.getName();
        if (display.length() > 16) display = display.substring(0, 16);
        p.setPlayerListName(display);
        // chat color will be applied in Chat Listener.

        // Particles
        if (data != null && data.getParticle() != null) {
            ParticleManager.getInstance().startFor(uuid, data.getParticle());
        } else {
            ParticleManager.getInstance().stopFor(uuid);
        }
    }

    public void removeVisualsIfOnline(UUID uuid) {
        Player p = Bukkit.getPlayer(uuid);
        if (p == null) return;
        // remove from scoreboard team
        Scoreboard sb = Bukkit.getScoreboardManager().getMainScoreboard();
        for (Team t : sb.getTeams()) {
            if (t.hasEntry(p.getName())) {
                t.removeEntry(p.getName());
            }
        }
        p.setPlayerListName(p.getName());
        ParticleManager.getInstance().stopFor(uuid);
    }
}
