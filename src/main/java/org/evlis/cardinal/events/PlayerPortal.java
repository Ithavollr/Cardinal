package org.evlis.cardinal.events;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerPortalEvent;

public class PlayerPortal implements Listener {
    @EventHandler
    public void onNetherTeleport(PlayerPortalEvent event) {
        Player player = event.getPlayer();
        World originWorld = player.getWorld();
        World.Environment destinationEnv = event.getTo().getWorld().getEnvironment();

        // Check if the player is in the restricted world and trying to go to the Nether
        if ("restricted_world_name".equals(originWorld.getName()) &&
                destinationEnv == World.Environment.NETHER) {
            // Cancel the portal event to prevent travel
            event.setCancelled(true);
            player.sendMessage("§cYou cannot escape to the Nether from this world!");
        }
    }
}
