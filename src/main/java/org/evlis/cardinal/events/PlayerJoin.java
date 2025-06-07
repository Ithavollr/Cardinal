package org.evlis.cardinal.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class PlayerJoin {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        // Send a welcome message to the player when they join
        event.getPlayer().sendMessage("§aWelcome to the server! Enjoy your stay!");

    }
}
