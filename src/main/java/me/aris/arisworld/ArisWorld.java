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
        loadExistingWorlds();
    }

    private void loadExistingWorlds() {
        File container = getServer().getWorldContainer();
        File[] files = container.listFiles();
        if (files == null) return;
        for (File file : files) {
            if (file.isDirectory() && new File(file, "level.dat").exists()) {
                String name = file.getName();
                if (Bukkit.getWorld(name) == null) {
                    new WorldCreator(name).createWorld();
                }
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player player)) return true;
        if (args.length < 2) return false;

        String action = args[0].toLowerCase();
        String worldName = args[1];

        switch (action) {
            case "create" -> {
                String type = args.length > 2 ? args[2].toLowerCase() : "normal";
                WorldCreator creator = new WorldCreator(worldName);
                if (type.equals("samac")) creator.generator(new DesertCustomGenerator());
                else if (type.equals("void")) creator.generator(new ChunkGenerator() {});
                else if (type.equals("flat")) creator.type(WorldType.FLAT);
                else if (type.equals("nether")) creator.environment(World.Environment.NETHER);
                else if (type.equals("end")) creator.environment(World.Environment.THE_END);
                Bukkit.createWorld(creator);
                player.sendMessage("§aCreated " + worldName);
            }
            case "tp" -> {
                World w = Bukkit.getWorld(worldName);
                if (w != null) player.teleportAsync(w.getSpawnLocation());
            }
            case "import" -> {
                if (new File(getServer().getWorldContainer(), worldName).exists()) {
                    Bukkit.createWorld(new WorldCreator(worldName));
                    player.sendMessage("§aImported!");
                }
            }
            case "delete" -> {
                World w = Bukkit.getWorld(worldName);
                if (w != null) Bukkit.unloadWorld(w, false);
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        if (args.length == 1) return Arrays.asList("create", "delete", "import", "tp");
        if (args.length == 3 && args[0].equalsIgnoreCase("create")) {
            return Arrays.asList("samac", "void", "normal", "nether", "end", "flat");
        }
        return null;
    }
                    }
