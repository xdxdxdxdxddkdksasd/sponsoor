package com.vipcosmetics.particles;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public enum ParticleEffect {
    HEART("Сердечки", Particle.HEART, Material.RED_WOOL, "&c", true),
    FLAME("Пламя", Particle.FLAME, Material.BLAZE_POWDER, "&6", true),
    STAR("Звёзды", Particle.END_ROD, Material.SUNFLOWER, "&e", true),
    SOUL("Душевные", Particle.SOUL, Material.SOUL_SOIL, "&a", true),
    CRIT("Критические", Particle.CRIT, Material.IRON_SWORD, "&7", true),
    ENDER("Эндер", Particle.PORTAL, Material.ENDER_PEARL, "&5", true),
    SNOW("Снег", Particle.SNOWFLAKE, Material.SNOWBALL, "&f", true),
    TOTEM("Тотемные", Particle.TOTEM, Material.TOTEM_OF_UNDYING, "&6", true),
    BUBBLE("Пузырьки", Particle.WATER_BUBBLE, Material.BUBBLE_CORAL, "&b", true),
    RAINBOW("Радуга", Particle.VILLAGER_HAPPY, Material.LIGHT_BLUE_WOOL, "&d", true);

    private final String displayName;
    private final Particle particle;
    private final Material icon;
    private final String colorCode;
    private final boolean cosmetic;

    ParticleEffect(String displayName, Particle particle, Material icon, String colorCode, boolean cosmetic) {
        this.displayName = displayName;
        this.particle = particle;
        this.icon = icon;
        this.colorCode = colorCode;
        this.cosmetic = cosmetic;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Particle getParticle() {
        return particle;
    }

    public Material getIcon() {
        return icon;
    }

    public String getColorCode() {
        return colorCode;
    }

    public boolean isCosmetic() {
        return cosmetic;
    }

    private static final Map<Material, ParticleEffect> byIcon = new HashMap<>();

    static {
        for (ParticleEffect e : values()) {
            byIcon.put(e.icon, e);
        }
    }

    public static ParticleEffect fromIcon(Material mat) {
        return byIcon.get(mat);
    }

    public boolean isEnabledFor(Player p) {
        return com.vipcosmetics.particles.ParticleManager.getInstance().isActiveFor(p.getUniqueId(), this);
    }
}
