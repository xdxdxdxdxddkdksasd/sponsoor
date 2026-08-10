package com.vipcosmetics.commands;

import com.vipcosmetics.Main;
import com.vipcosmetics.storage.StorageManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class VipCommand implements CommandExecutor, TabCompleter {

    private final Main plugin;
    private final StorageManager storage;

    public VipCommand(Main plugin) {
        this.plugin = plugin;
        this.storage = plugin.getStorage();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            // Open GUI (only for players with permission)
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "Only players can use this command without arguments.");
                return true;
            }
            Player p = (Player) sender;
            if (!p.hasPermission("vip.use")) {
                p.sendMessage(ChatColor.RED + "У вас нет доступа к VIP GUI.");
                return true;
            }
            plugin.getGuiManager().openMainMenu(p);
            return true;
        }

        // admin subcommands
        if (!sender.hasPermission("vip.admin")) {
            sender.sendMessage(ChatColor.RED + "Недостаточно прав.");
            return true;
        }

        String sub = args[0].toLowerCase();
        if (sub.equals("give")) {
            if (args.length < 2) {
                sender.sendMessage(ChatColor.YELLOW + "Использование: /vip give <игрок>");
                return true;
            }
            Player target = Bukkit.getPlayerExact(args[1]);
            if (target == null) {
                sender.sendMessage(ChatColor.RED + "Игрок не в сети.");
                return true;
            }
            storage.addVip(target.getUniqueId());
            storage.applyVisualsIfOnline(target.getUniqueId());
            sender.sendMessage(ChatColor.GREEN + "VIP выдан игроку " + target.getName());
            target.sendMessage(ChatColor.GREEN + "Вам выдан VIP!");
            return true;
        } else if (sub.equals("remove")) {
            if (args.length < 2) {
                sender.sendMessage(ChatColor.YELLOW + "Использование: /vip remove <игрок>");
                return true;
            }
            Player target = Bukkit.getPlayerExact(args[1]);
            if (target == null) {
                sender.sendMessage(ChatColor.RED + "Игрок не в сети.");
                return true;
            }
            storage.removeVip(target.getUniqueId());
            storage.removeVisualsIfOnline(target.getUniqueId());
            sender.sendMessage(ChatColor.GREEN + "VIP снят у игрока " + target.getName());
            target.sendMessage(ChatColor.RED + "У вас снят VIP.");
            return true;
        } else if (sub.equals("check")) {
            if (args.length < 2) {
                sender.sendMessage(ChatColor.YELLOW + "Использование: /vip check <игрок>");
                return true;
            }
            Player target = Bukkit.getPlayerExact(args[1]);
            if (target == null) {
                sender.sendMessage(ChatColor.RED + "Игрок не в сети.");
                return true;
            }
            boolean isVip = storage.isVip(target.getUniqueId());
            sender.sendMessage(ChatColor.AQUA + target.getName() + (isVip ? ChatColor.GREEN + " имеет VIP" : ChatColor.RED + " не имеет VIP"));
            return true;
        }

        sender.sendMessage(ChatColor.YELLOW + "Подкоманды: give, remove, check");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> res = new ArrayList<>();
        if (args.length == 1) {
            if (sender.hasPermission("vip.admin")) {
                res.add("give");
                res.add("remove");
                res.add("check");
            }
            return res;
        }
        if (args.length == 2) {
            // suggest online players
            String prefix = args[1].toLowerCase();
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.getName().toLowerCase().startsWith(prefix)) res.add(p.getName());
            }
            return res;
        }
        return res;
    }
}
