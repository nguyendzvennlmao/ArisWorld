package me.aris.arisworld;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ArisCommand implements CommandExecutor, TabCompleter {

    public String color(String msg) {
        Pattern p = Pattern.compile("&#([A-Fa-f0-9]{6})");
        Matcher m = p.matcher(msg);
        StringBuffer b = new StringBuffer();
        while (m.find()) {
            m.appendReplacement(b, "§x§" + m.group(1).charAt(0) + "§" + m.group(1).charAt(1) +
                    "§" + m.group(1).charAt(2) + "§" + m.group(1).charAt(3) +
                    "§" + m.group(1).charAt(4) + "§" + m.group(1).charAt(5));
        }
        return org.bukkit.ChatColor.translateAlternateColorCodes('&', m.appendTail(b).toString());
    }

    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] a) {
        if (!(s instanceof Player)) return true;
        Player p = (Player) s;
        if (a.length < 2) {
            p.sendMessage(color("&#ff0812[ᴀʀɪꜱ] ꜱᴀɪ ᴄᴜ́ ᴘʜᴀ́ᴘ! /aw <create|tp|delete> <name>"));
            return true;
        }
        String act = a[0].toLowerCase();
        String name = a[1];

        if (act.equals("create")) {
            if (a.length < 3) {
                p.sendMessage(color("&#ff0812[ᴀʀɪꜱ] ʟᴏᴀ̣ɪ: desert | overworld"));
                return true;
            }
            p.sendMessage(color("&#facc15[ᴀʀɪꜱ] ᴆᴀɴɢ ᴛᴀ̣ᴏ ᴡᴏʀʟᴅ " + name));
            WorldCreator cr = new WorldCreator(name);
            if (a[2].equalsIgnoreCase("desert")) {
                cr.generator(new ArisGenerator());
            } else {
                cr.environment(World.Environment.NORMAL);
                cr.type(WorldType.NORMAL);
                cr.generateStructures(true);
            }
            Bukkit.createWorld(cr);
            p.sendMessage(color("&#facc15[ᴀʀɪꜱ] ᴛᴀ̣ᴏ ᴛʜᴀ̀ɴʜ ᴄᴏ̂ɴɢ!"));
        } else if (act.equals("tp")) {
            World w = Bukkit.getWorld(name);
            if (w == null) return true;
            p.teleportAsync(w.getSpawnLocation()).thenAccept(res -> {
                if (res) p.sendMessage(color("&#facc15[ᴀʀɪꜱ] ᴆᴀ̃ ᴆᴇ̂́ɴ " + name));
            });
        } else if (act.equals("delete")) {
            World wd = Bukkit.getWorld(name);
            if (wd == null) return true;
            for (Player wp : wd.getPlayers()) wp.teleportAsync(Bukkit.getWorlds().get(0).getSpawnLocation());
            File f = wd.getWorldFolder();
            Bukkit.unloadWorld(wd, false);
            deleteFile(f);
            p.sendMessage(color("&#facc15[ᴀʀɪꜱ] ᴆᴀ̃ xᴏ́ᴀ " + name));
        }
        return true;
    }

    private void deleteFile(File path) {
        if (path.exists()) {
            File[] fs = path.listFiles();
            if (fs != null) {
                for (File f : fs) {
                    if (f.isDirectory()) deleteFile(f);
                    else f.delete();
                }
            }
            path.delete();
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender s, Command c, String l, String[] a) {
        if (a.length == 1) return Arrays.asList("create", "tp", "delete");
        if (a.length == 2 && !a[0].equals("create")) {
            List<String> ws = new ArrayList<>();
            Bukkit.getWorlds().forEach(w -> ws.add(w.getName()));
            return ws;
        }
        if (a.length == 3 && a[0].equals("create")) return Arrays.asList("desert", "overworld");
        return null;
    }
                                }
