package me.aris.arisworld;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import java.util.List;

public class AntiCheatListener implements Listener {
    private final ArisWorld plugin;

    public AntiCheatListener(ArisWorld plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player p = event.getPlayer();
        World w = p.getWorld();
        
        List<String> worlds = plugin.getConfig().getStringList("anti-freecam.enabled-worlds");
        if (worlds == null || !worlds.contains(w.getName())) return;

        int limitY = plugin.getConfig().getInt("anti-freecam.hide-below-y");
        
        if (p.getEyeLocation().getY() < limitY) {
            if (p.getGameMode() != org.bukkit.GameMode.SPECTATOR) return;
            for (Player t : Bukkit.getOnlinePlayers()) {
                p.hidePlayer(plugin, t);
            }
        } else {
            for (Player t : Bukkit.getOnlinePlayers()) {
                p.showPlayer(plugin, t);
            }
        }
    }
}
