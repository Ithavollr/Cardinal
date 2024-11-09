package org.evlis.cardinal.events;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.evlis.cardinal.GlobalVars;

public class PlayerTeleport implements Listener {
    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();

        // Check if the player is using a command to teleport
        if (event.getCause() == TeleportCause.COMMAND) {
            int distance = (int)event.getFrom().distance(event.getTo());
            int exp = player.calculateTotalExperiencePoints();
            if(distance >= exp) {
                // Cancel the portal event to prevent travel
                event.setCancelled(true);
                player.sendMessage("§clSYSTEM:§roc You don't have enough experience to travel that far!");
            } else {
                player.setExp((float)(exp - distance));
            }
        }
    }
}