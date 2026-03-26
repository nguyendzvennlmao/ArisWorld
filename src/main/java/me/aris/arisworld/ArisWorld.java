package me.aris.arisworld;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;
import java.util.List;

public class ArisWorld extends JavaPlugin {
    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadSavedWorlds();
        ArisCommand cmd = new ArisCommand(this);
        getCommand("aw").setExecutor(cmd);
        getCommand("aw").setTabCompleter(cmd);
    }

    private void loadSavedWorlds() {
        FileConfiguration config = getConfig();
        List<String> worlds = config.getStringList("worlds");
        for (String wData : worlds) {
            String[] split = wData.split(":");
            if (split.length < 2) continue;
            String name = split[0];
            String type = split[1];
            if (new File(Bukkit.getWorldContainer(), name).exists()) {
                WorldCreator cr = new WorldCreator(name);
                if (type.equals("nether")) cr.environment(World.Environment.NETHER);
                else if (type.equals("the_end")) cr.environment(World.Environment.THE_END);
                else if (!type.equals("overworld")) cr.generator(new ArisGenerator(type));
                Bukkit.createWorld(cr);
            }
        }
    }
                  }
