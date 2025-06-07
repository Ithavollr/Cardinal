package org.evlis.cardinal.events;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;

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
                player.setExperienceLevelAndProgress(exp  - distance);
                player.sendMessage("§6Teleport cost: " + distance + "xp");
            }
        } else if (event.getCause() == TeleportCause.NETHER_PORTAL) {
            ItemStack item = player.getInventory().getItemInMainHand();
            if (item.getType() == Material.BONE && item.getItemMeta().hasLore()) {
                String customLore = item.getItemMeta().getLore().get(0);
                if (customLore.equals("§6Bone of the Nether")) {
                    // Allow teleportation through the Nether portal
                    player.sendMessage("§aYou are using the Bone of the Nether to teleport!");
                } else {
                    // Cancel the portal event if the item is not the Bone of the Nether
                    event.setCancelled(true);
                    player.sendMessage("§clSYSTEM:§roc You can only use a Bone of the Nether to teleport through this portal!");
                }
            } else {
                // Cancel the portal event if no valid item is in hand
                event.setCancelled(true);
                player.sendMessage("§clSYSTEM:§roc You need a Bone of the Nether to use this portal!");
            }
        }
    }
}