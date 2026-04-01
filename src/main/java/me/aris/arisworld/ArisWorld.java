package me.aris.arisworld;

import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ArisWorld extends JavaPlugin implements CommandExecutor, TabCompleter {

    @Override
    public void onEnable() {
        saveDefaultConfig();
        getCommand("world").setExecutor(this);
        getCommand("world").setTabCompleter(this);
        getServer().getPluginManager().registerEvents(new AntiCheatListener(this), this);
        
        Bukkit.getGlobalRegionScheduler().run(this, task -> {
            loadExistingWorlds();
        });
    }

    private void loadExistingWorlds() {
        File container = getServer().getWorldContainer();
        File[] files = container.listFiles();
        if (files == null) return;
        for (File f : files) {
            if (f.isDirectory() && new File(f, "level.dat").exists()) {
                String n = f.getName();
                if (Bukkit.getWorld(n) == null) {
                    new WorldCreator(n).createWorld();
                }
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] args) {
        if (!(s instanceof Player p)) return true;
        if (args.length < 2) return false;

        String action = args[0].toLowerCase();
        String name = args[1];

        switch (action) {
            case "create" -> {
                String type = args.length > 2 ? args[2].toLowerCase() : "normal";
                WorldCreator cr = new WorldCreator(name);
                if (type.equals("samac")) cr.generator(new DesertCustomGenerator());
                else if (type.equals("void")) cr.generator(new ChunkGenerator() {});
                else if (type.equals("flat")) cr.type(WorldType.FLAT);
                else if (type.equals("nether")) cr.environment(World.Environment.NETHER);
                else if (type.equals("end")) cr.environment(World.Environment.THE_END);
                
                Bukkit.getGlobalRegionScheduler().run(this, task -> {
                    Bukkit.createWorld(cr);
                    p.sendMessage("§aCreated " + name);
                });
            }
            case "tp" -> {
                World w = Bukkit.getWorld(name);
                if (w != null) p.teleportAsync(w.getSpawnLocation());
            }
            case "import" -> {
                if (new File(getServer().getWorldContainer(), name).exists()) {
                    Bukkit.getGlobalRegionScheduler().run(this, task -> {
                        Bukkit.createWorld(new WorldCreator(name));
                        p.sendMessage("§aImported!");
                    });
                }
            }
            case "delete" -> {
                World w = Bukkit.getWorld(name);
                if (w != null) {
                    Bukkit.getGlobalRegionScheduler().run(this, task -> {
                        Bukkit.unloadWorld(w, false);
                        p.sendMessage("§cUnloaded!");
                    });
                }
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender s, Command c, String a, String[] args) {
        if (args.length == 1) return Arrays.asList("create", "delete", "import", "tp");
        if (args.length == 2) {
            List<String> worlds = new ArrayList<>();
            Bukkit.getWorlds().forEach(w -> worlds.add(w.getName()));
            return worlds;
        }
        if (args.length == 3 && args[0].equalsIgnoreCase("create")) {
            return Arrays.asList("samac", "void", "normal", "nether", "end", "flat");
        }
        return null;
    }
                   }
