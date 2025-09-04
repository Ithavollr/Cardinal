package org.evlis.cardinal.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.evlis.cardinal.Cardinal;
import org.evlis.cardinal.GlobalVars;
import org.evlis.cardinal.helpers.Cryptography;

import java.util.Objects;

public class PlayerJoin implements Listener {
    Plugin plugin = Cardinal.getInstance();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        String uuid = GlobalVars.seaSalt + event.getPlayer().getName();
        String playerKey = Cryptography.generatePlayerKey(uuid);
        // Send a welcome message to the player when they join
        event.getPlayer().sendMessage("Welcome to " + plugin.getConfig().getString("server-name") +
                "§r, " + event.getPlayer().getName() + "!");
        event.getPlayer().sendMessage(Objects.requireNonNull(plugin.getConfig().getString("server-greeting")));
    }
}
