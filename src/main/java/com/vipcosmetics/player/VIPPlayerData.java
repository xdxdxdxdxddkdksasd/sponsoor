package com.vipcosmetics.player;

import org.bukkit.ChatColor;
import com.vipcosmetics.particles.ParticleEffect;

public class VIPPlayerData {
    private ChatColor nameColor;
    private ChatColor chatColor;
    private ParticleEffect particle;

    public VIPPlayerData() {}

    public ChatColor getNameColor() {
        return nameColor;
    }

    public void setNameColor(ChatColor nameColor) {
        this.nameColor = nameColor;
    }

    public ChatColor getChatColor() {
        return chatColor;
    }

    public void setChatColor(ChatColor chatColor) {
        this.chatColor = chatColor;
    }

    public ParticleEffect getParticle() {
        return particle;
    }

    public void setParticle(ParticleEffect particle) {
        this.particle = particle;
    }
}
