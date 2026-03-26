package me.aris.arisworld;

import org.bukkit.plugin.java.JavaPlugin;

public class ArisWorld extends JavaPlugin {
    @Override
    public void onEnable() {
        ArisCommand cmd = new ArisCommand();
        getCommand("aw").setExecutor(cmd);
        getCommand("aw").setTabCompleter(cmd);
    }
}
