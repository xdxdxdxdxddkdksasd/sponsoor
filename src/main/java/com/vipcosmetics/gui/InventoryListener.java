package com.vipcosmetics.gui;

import com.vipcosmetics.Main;
import com.vipcosmetics.particles.ParticleEffect;
import com.vipcosmetics.player.VIPPlayerData;
import com.vipcosmetics.storage.StorageManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class InventoryListener implements Listener {

    private final Main plugin;
    private final StorageManager storage;

    public InventoryListener(Main plugin) {
        this.plugin = plugin;
        this.storage = plugin.getStorage();
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getClickedInventory() == null) return;
        if (!(e.getWhoClicked() instanceof Player)) return;
        Player p = (Player) e.getWhoClicked();
        if (e.getView().getTitle().equals(GuiManager.MAIN_TITLE)) {
            e.setCancelled(true);
            if (e.getCurrentItem() == null) return;
            Material mat = e.getCurrentItem().getType();
            if (mat == Material.NAME_TAG) {
                p.closeInventory();
                plugin.getGuiManager().openNameColorMenu(p);
            } else if (mat == Material.PAPER) {
                p.closeInventory();
                plugin.getGuiManager().openChatColorMenu(p);
            } else if (mat == Material.FIREWORK_STAR) {
                p.closeInventory();
                plugin.getGuiManager().openParticlesMenu(p);
            } else if (mat == Material.DIAMOND_HELMET) {
                p.performCommand("hat");
            } else if (mat == Material.BARRIER) {
                p.closeInventory();
            } else if (mat == Material.PLAYER_HEAD) {
                VIPPlayerData data = storage.getPlayerData(p.getUniqueId());
                p.sendMessage(ChatColor.GOLD + "VIP: " + (storage.isVip(p.getUniqueId()) ? ChatColor.GREEN + "Активен" : ChatColor.RED + "Неактивен"));
                if (data != null) {
                    p.sendMessage(ChatColor.GRAY + "Имя: " + (data.getNameColor() == null ? "по умолчанию" : data.getNameColor().name()));
                    p.sendMessage(ChatColor.GRAY + "Чат: " + (data.getChatColor() == null ? "по умолчанию" : data.getChatColor().name()));
                    p.sendMessage(ChatColor.GRAY + "Частицы: " + (data.getParticle() == null ? "нет" : data.getParticle().name()));
                }
                p.closeInventory();
            }
        } else if (e.getView().getTitle().equals(GuiManager.NAME_COLOR_TITLE)) {
            e.setCancelled(true);
            if (e.getCurrentItem() == null) return;
            String name = e.getCurrentItem().getItemMeta().getDisplayName();
            if (name == null) return;
            // Determine color by display name (we used ChatColor in name)
            ChatColor chosen = null;
            for (ChatColor c : ChatColor.values()) {
                if (name.contains(c.toString())) {
                    chosen = c;
                    break;
                }
            }
            if (chosen == null) {
                // handle purple/pink items by material
                if (e.getCurrentItem().getType() == Material.PURPLE_WOOL) chosen = ChatColor.LIGHT_PURPLE;
                if (e.getCurrentItem().getType() == Material.PINK_WOOL) chosen = ChatColor.LIGHT_PURPLE;
            }
            VIPPlayerData data = storage.getPlayerData(p.getUniqueId());
            if (data == null) {
                data = new VIPPlayerData();
            }
            data.setNameColor(chosen);
            storage.setPlayerData(p.getUniqueId(), data);
            storage.applyVisualsIfOnline(p.getUniqueId());
            p.sendMessage(ChatColor.GREEN + "Цвет ника обновлён.");
            p.closeInventory();
            plugin.getGuiManager().openMainMenu(p);
        } else if (e.getView().getTitle().equals(GuiManager.CHAT_COLOR_TITLE)) {
            e.setCancelled(true);
            if (e.getCurrentItem() == null) return;
            String name = e.getCurrentItem().getItemMeta().getDisplayName();
            if (name == null) return;
            ChatColor chosen = null;
            for (ChatColor c : ChatColor.values()) {
                if (name.contains(c.name()) || name.contains(c.toString())) {
                    chosen = c;
                    break;
                }
            }
            if (chosen == null) {
                // try to map by color codes in display
                for (ChatColor c : ChatColor.values()) {
                    if (e.getCurrentItem().getItemMeta().getDisplayName().contains(c.toString())) {
                        chosen = c; break;
                    }
                }
            }
            VIPPlayerData data = storage.getPlayerData(p.getUniqueId());
            if (data == null) data = new VIPPlayerData();
            data.setChatColor(chosen);
            storage.setPlayerData(p.getUniqueId(), data);
            p.sendMessage(ChatColor.GREEN + "Цвет чата обновлён.");
            p.closeInventory();
            plugin.getGuiManager().openMainMenu(p);
        } else if (e.getView().getTitle().equals(GuiManager.PARTICLES_TITLE)) {
            e.setCancelled(true);
            if (e.getCurrentItem() == null) return;
            Material mat = e.getCurrentItem().getType();
            if (mat == Material.BARRIER) {
                // disable all
                storage.setPlayerParticle(p.getUniqueId(), null);
                com.vipcosmetics.particles.ParticleManager.getInstance().stopFor(p.getUniqueId());
                p.sendMessage(ChatColor.GREEN + "Частицы отключены.");
                p.closeInventory();
                plugin.getGuiManager().openMainMenu(p);
                return;
            }
            // find ParticleEffect by material
            ParticleEffect chosen = ParticleEffect.fromIcon(mat);
            if (chosen == null) return;
            storage.setPlayerParticle(p.getUniqueId(), chosen);
            com.vipcosmetics.particles.ParticleManager.getInstance().startFor(p.getUniqueId(), chosen);
            p.sendMessage(ChatColor.GREEN + "Вы выбрали частицы: " + chosen.getDisplayName());
            p.closeInventory();
            plugin.getGuiManager().openMainMenu(p);
        }
    }
}
