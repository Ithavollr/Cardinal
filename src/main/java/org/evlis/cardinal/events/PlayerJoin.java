package org.evlis.cardinal.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.evlis.cardinal.GlobalVars;
import org.evlis.cardinal.helpers.Cryptography;

public class PlayerJoin implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        String uuid = GlobalVars.seaSalt + event.getPlayer().getName();
        String playerKey = Cryptography.generatePlayerKey(uuid);
        // Send a welcome message to the player when they join
        event.getPlayer().sendMessage("§aWelcome to the server! Your home key is: " + playerKey);
    }
}
