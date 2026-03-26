package me.aris.arisworld;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
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
import java.util.stream.Collectors;

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
            p.sendMessage(color("&#ff0812[ᴀʀɪꜱ] /aw <create|tp|delete> <name> <desert|overworld|spawn>"));
            return true;
        }
        String act = a[0].toLowerCase();
        String name = a[1];

        if (act.equals("create")) {
            if (a.length < 3) return true;
            String type = a[2].toLowerCase();
            p.sendMessage(color("&#facc15[ᴀʀɪꜱ] ᴆᴀɴɢ xᴀ̂ʏ ᴅᴜ̛̣ɴɢ ᴛʜᴇ̂́ ɢɪᴏ̛́ɪ " + type + " ꜱɪᴇ̂ᴜ ᴆᴇ̣ᴘ..."));
            WorldCreator cr = new WorldCreator(name);
            cr.generator(new ArisGenerator(type));
            Bukkit.createWorld(cr);
            p.sendMessage(color("&#facc15[ᴀʀɪꜱ] ᴛᴀ̣ᴏ ᴡᴏʀʟᴅ ᴛʜᴀ̀ɴʜ ᴄᴏ̂ɴɢ!"));
        } else if (act.equals("tp")) {
            World w = Bukkit.getWorld(name);
            if (w != null) {
                p.teleportAsync(w.getSpawnLocation());
            } else {
                p.sendMessage(color("&#ff0812[ᴀʀɪꜱ] ᴡᴏʀʟᴅ ᴋʜᴏ̂ɴɢ ᴛᴏ̂̀ɴ ᴛᴀ̣ɪ!"));
            }
        } else if (act.equals("delete")) {
            World wd = Bukkit.getWorld(name);
            if (wd == null) return true;
            Bukkit.unloadWorld(wd, false);
            deleteFile(wd.getWorldFolder());
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
        if (a.length == 1) {
            return Arrays.asList("create", "tp", "delete").stream()
                    .filter(st -> st.startsWith(a[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        if (a.length == 2) {
            return Bukkit.getWorlds().stream()
                    .map(World::getName)
                    .filter(name -> name.toLowerCase().startsWith(a[1].toLowerCase()))
                    .collect(Collectors.toList());
        }
        if (a.length == 3 && a[0].equalsIgnoreCase("create")) {
            return Arrays.asList("desert", "overworld", "spawn").stream()
                    .filter(st -> st.startsWith(a[2].toLowerCase()))
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
          }
