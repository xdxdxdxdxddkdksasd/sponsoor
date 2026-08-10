package com.vipcosmetics.gui;

import com.vipcosmetics.Main;
import com.vipcosmetics.particles.ParticleEffect;
import com.vipcosmetics.storage.StorageManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class GuiManager {

    private final Main plugin;
    private final StorageManager storage;

    public static final String MAIN_TITLE = ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "VIP Cosmetic Menu";
    public static final String NAME_COLOR_TITLE = ChatColor.DARK_PURPLE + "Select Name Color";
    public static final String CHAT_COLOR_TITLE = ChatColor.DARK_PURPLE + "Select Chat Color";
    public static final String PARTICLES_TITLE = ChatColor.DARK_PURPLE + "Select Particle Effect";

    public GuiManager(Main plugin) {
        this.plugin = plugin;
        this.storage = plugin.getStorage();
    }

    public void openMainMenu(Player p) {
        Inventory inv = Bukkit.createInventory(null, 27, MAIN_TITLE);
        // Decorations
        for (int i = 0; i < 27; i++) {
            if (i == 11 || i == 13 || i == 15 || i == 10 || i == 12 || i == 14 || i == 16) continue;
            inv.setItem(i, makeGlassPane());
        }

        inv.setItem(11, item(Material.NAME_TAG, ChatColor.YELLOW + "🎨 Цвет ника", Arrays.asList(ChatColor.GRAY + "Выберите цвет ника")));
        inv.setItem(13, item(Material.PAPER, ChatColor.AQUA + "💬 Цвет чата", Arrays.asList(ChatColor.GRAY + "Выберите цвет сообщений")));
        inv.setItem(15, item(Material.FIREWORK_STAR, ChatColor.LIGHT_PURPLE + "✨ Частицы", Arrays.asList(ChatColor.GRAY + "Косметические эффекты вокруг вас")));
        inv.setItem(22, item(Material.DIAMOND_HELMET, ChatColor.GOLD + "🎩 Hat", Arrays.asList(ChatColor.GRAY + "Надеть предмет в руке как шляпу")));
        inv.setItem(4, item(Material.PLAYER_HEAD, ChatColor.GOLD + "👑 Статус VIP", Arrays.asList(ChatColor.GRAY + "Ваш VIP статус")));
        inv.setItem(26, item(Material.BARRIER, ChatColor.RED + "❌ Закрыть", Arrays.asList(ChatColor.GRAY + "Закрыть меню")));

        p.openInventory(inv);
        p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 0.8f, 1f);
    }

    public void openNameColorMenu(Player p) {
        Inventory inv = Bukkit.createInventory(null, 27, NAME_COLOR_TITLE);
        for (int i = 0; i < 27; i++) {
            if (i >= 10 && i <= 16) continue;
            inv.setItem(i, makeGlassPane());
        }

        // Colors mapping
        Map<Integer, ChatColor> slots = new LinkedHashMap<>();
        slots.put(10, ChatColor.WHITE);
        slots.put(11, ChatColor.RED);
        slots.put(12, ChatColor.GOLD); // will map to ORANGE/ GOLD wool
        slots.put(13, ChatColor.YELLOW);
        slots.put(14, ChatColor.GREEN);
        slots.put(15, ChatColor.AQUA);
        slots.put(16, ChatColor.BLUE);

        for (Map.Entry<Integer, ChatColor> e : slots.entrySet()) {
            Material mat = materialForChatColor(e.getValue());
            String display = e.getValue().toString() + " " + e.getValue().name();
            inv.setItem(e.getKey(), item(mat, display, Arrays.asList(ChatColor.GRAY + "Нажмите чтобы выбрать")));
        }

        // extra: purple and pink
        inv.setItem(17, item(Material.PURPLE_WOOL, ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Фиолетовый", null));
        inv.setItem(9, item(Material.PINK_WOOL, ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "Розовый", null));

        inv.setItem(26, item(Material.ARROW, ChatColor.GRAY + "Назад", Arrays.asList(ChatColor.GRAY + "Вернуться в главное меню")));
        p.openInventory(inv);
        p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 0.8f, 1f);
    }

    public void openChatColorMenu(Player p) {
        Inventory inv = Bukkit.createInventory(null, 27, CHAT_COLOR_TITLE);
        for (int i = 0; i < 27; i++) {
            if (i >= 10 && i <= 16) continue;
            inv.setItem(i, makeGlassPane());
        }

        Map<Integer, ChatColor> slots = new LinkedHashMap<>();
        slots.put(10, ChatColor.WHITE);
        slots.put(11, ChatColor.GRAY);
        slots.put(12, ChatColor.RED);
        slots.put(13, ChatColor.YELLOW);
        slots.put(14, ChatColor.GREEN);
        slots.put(15, ChatColor.AQUA);
        slots.put(16, ChatColor.BLUE);

        for (Map.Entry<Integer, ChatColor> e : slots.entrySet()) {
            Material mat = materialForChatColor(e.getValue());
            String display = e.getValue().toString() + " " + e.getValue().name();
            inv.setItem(e.getKey(), item(mat, display, Arrays.asList(ChatColor.GRAY + "Нажмите чтобы выбрать")));
        }

        inv.setItem(26, item(Material.ARROW, ChatColor.GRAY + "Назад", Arrays.asList(ChatColor.GRAY + "Вернуться в главное меню")));
        p.openInventory(inv);
        p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 0.8f, 1f);
    }

    public void openParticlesMenu(Player p) {
        Inventory inv = Bukkit.createInventory(null, 27, PARTICLES_TITLE);
        for (int i = 0; i < 27; i++) {
            if (i >= 10 && i <= 16) continue;
            inv.setItem(i, makeGlassPane());
        }

        ParticleEffect[] effects = ParticleEffect.values();
        int slot = 10;
        for (int idx = 0; idx < effects.length && slot <= 16; idx++, slot++) {
            ParticleEffect eff = effects[idx];
            List<String> lore = new ArrayList<>();
            lore.add("");
            lore.add(ChatColor.GRAY + eff.getDisplayName());
            lore.add(ChatColor.GRAY + (eff.isEnabledFor(p) ? "Статус: Включено" : "Статус: Выключено"));
            inv.setItem(slot, item(eff.getIcon(), ChatColor.translateAlternateColorCodes('&', eff.getColorCode() + " " + eff.getDisplayName()), lore));
        }

        inv.setItem(16, item(Material.BARRIER, ChatColor.RED + "❌ Отключить частицы", Arrays.asList(ChatColor.GRAY + "Отключает любые ваши частицы")));
        inv.setItem(26, item(Material.ARROW, ChatColor.GRAY + "Назад", Arrays.asList(ChatColor.GRAY + "Вернуться в главное меню")));
        p.openInventory(inv);
        p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 0.8f, 1f);
    }

    private ItemStack makeGlassPane() {
        return item(Material.GRAY_STAINED_GLASS_PANE, " ");
    }

    private Material materialForChatColor(ChatColor c) {
        // Map ChatColor to approximate wool/material color for icons
        if (c == null) return Material.WHITE_WOOL;
        switch (c) {
            case WHITE: return Material.WHITE_WOOL;
            case AQUA: return Material.LIGHT_BLUE_WOOL;
            case BLUE: return Material.BLUE_WOOL;
            case DARK_BLUE: return Material.BLUE_WOOL;
            case GREEN: return Material.GREEN_WOOL;
            case DARK_GREEN: return Material.GREEN_WOOL;
            case YELLOW: return Material.YELLOW_WOOL;
            case GOLD: return Material.ORANGE_WOOL;
            case RED: return Material.RED_WOOL;
            case DARK_RED: return Material.RED_WOOL;
            case LIGHT_PURPLE: return Material.PINK_WOOL;
            case DARK_PURPLE: return Material.PURPLE_WOOL;
            case GRAY: return Material.LIGHT_GRAY_WOOL;
            case DARK_GRAY: return Material.GRAY_WOOL;
            case BLACK: return Material.BLACK_WOOL;
            case PINK: return Material.PINK_WOOL;
            case MAGIC: return Material.LIGHT_BLUE_WOOL;
            default: return Material.WHITE_WOOL;
        }
    }

    public static ItemStack item(Material mat, String name) {
        return item(mat, name, null);
    }

    public static ItemStack item(Material mat, String name, List<String> lore) {
        ItemStack it = new ItemStack(mat);
        ItemMeta meta = it.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            if (lore != null) meta.setLore(lore);
            it.setItemMeta(meta);
        }
        return it;
    }

}
