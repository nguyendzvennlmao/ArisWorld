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
        Player player = event.getPlayer();
        World world = player.getWorld();
        
        List<String> enabledWorlds = plugin.getConfig().getStringList("anti-freecam.enabled-worlds");
        if (enabledWorlds == null || !enabledWorlds.contains(world.getName())) return;

        int hideY = plugin.getConfig().getInt("anti-freecam.hide-below-y");
        
        if (player.getEyeLocation().getY() < hideY) {
            if (player.getGameMode() != org.bukkit.GameMode.SPECTATOR) return;
            for (Player target : Bukkit.getOnlinePlayers()) {
                player.hidePlayer(plugin, target);
            }
        } else {
            for (Player target : Bukkit.getOnlinePlayers()) {
                player.showPlayer(plugin, target);
            }
        }
    }
                }
