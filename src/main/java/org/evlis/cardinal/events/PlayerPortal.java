package org.evlis.cardinal.events;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;
import org.evlis.cardinal.GlobalVars;

public class PlayerPortal implements Listener {
    @EventHandler
    public void onNetherTeleport(PlayerPortalEvent event) {
        Player player = event.getPlayer();
        World world = player.getWorld();
        World.Environment destinationEnv = event.getTo().getWorld().getEnvironment();

        // Check if the player is in the restricted world and trying to go to the Nether
        if (GlobalVars.shatteredWorlds.contains(world.getName()) &&
                destinationEnv == World.Environment.NETHER) {
            // Cancel the portal event to prevent travel
            event.setCancelled(true);
            player.sendMessage("§cThis world is shattered!! Nether portals are broken, you cannot escape!");
        }
    }
}
