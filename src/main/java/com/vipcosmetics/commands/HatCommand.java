package com.vipcosmetics.commands;

import com.vipcosmetics.Main;
import com.vipcosmetics.storage.StorageManager;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class HatCommand implements CommandExecutor, TabCompleter {

    private final Main plugin;
    private final StorageManager storage;

    public HatCommand(Main plugin) {
        this.plugin = plugin;
        this.storage = plugin.getStorage();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Команду может использовать только игрок.");
            return true;
        }
        Player player = (Player) sender;
        if (!player.hasPermission("vip.hat")) {
            player.sendMessage(ChatColor.RED + "У вас нет прав на /hat");
            return true;
        }
        if (!storage.isVip(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "Команда доступна только VIP.");
            return true;
        }

        ItemStack inHand = player.getInventory().getItemInMainHand();
        if (inHand == null || inHand.getType().isAir()) {
            player.sendMessage(ChatColor.YELLOW + "В основной руке нет предмета.");
            return true;
        }

        // Put item to helmet slot
        ItemStack prevHelmet = player.getInventory().getHelmet();
        player.getInventory().setHelmet(inHand.clone());
        player.getInventory().setItemInMainHand(null);

        if (prevHelmet != null && !prevHelmet.getType().isAir()) {
            // drop previous helmet to inventory or ground
            player.getInventory().addItem(prevHelmet);
        }

        player.sendMessage(ChatColor.GREEN + "Предмет надет как шляпа.");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return new ArrayList<>();
    }
}
