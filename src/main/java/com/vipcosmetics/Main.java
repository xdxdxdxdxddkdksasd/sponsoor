package com.vipcosmetics;

import com.vipcosmetics.commands.HatCommand;
import com.vipcosmetics.commands.VipCommand;
import com.vipcosmetics.gui.GuiManager;
import com.vipcosmetics.listeners.JoinQuitListener;
import com.vipcosmetics.storage.StorageManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private static Main instance;
    private StorageManager storageManager;
    private GuiManager guiManager;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        storageManager = new StorageManager(this);
        storageManager.loadAll();

        guiManager = new GuiManager(this);

        // Commands
        VipCommand vipCommand = new VipCommand(this);
        getCommand("vip").setExecutor(vipCommand);
        getCommand("vip").setTabCompleter(vipCommand);

        HatCommand hatCommand = new HatCommand(this);
        getCommand("hat").setExecutor(hatCommand);
        getCommand("hat").setTabCompleter(hatCommand);

        // Listeners
        Bukkit.getPluginManager().registerEvents(new JoinQuitListener(this), this);
        Bukkit.getPluginManager().registerEvents(new com.vipcosmetics.gui.InventoryListener(this), this);

        // Apply visuals for online VIPs from storage (in case of reload)
        storageManager.getVipList().forEach(uuid -> {
            storageManager.applyVisualsIfOnline(uuid);
        });

        getLogger().info("VIPCosmetics enabled.");
    }

    @Override
    public void onDisable() {
        storageManager.saveAll();
        com.vipcosmetics.particles.ParticleManager.getInstance().stopAll();
        getLogger().info("VIPCosmetics disabled.");
    }

    public StorageManager getStorage() {
        return storageManager;
    }

    public GuiManager getGuiManager() {
        return guiManager;
    }

    public static Main getInstance() {
        return instance;
    }
}
