package org.evlis.cardinal.events;
import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.evlis.cardinal.helpers.EventHelpers;


public class PlayerInteract implements Listener {
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        //=== RIGHT-CLICK INTERACTIONS: ===//
        if (!event.getAction().isRightClick()) return;
        if (event.getHand() != EquipmentSlot.HAND) return; // filter out off-hand events

        //=========|| HERE ARE THE BLOCK INTERACTIONS ||=========//
        if (event.getClickedBlock() != null) {
            // try to craft a bottle o' enchanting
            if (event.getClickedBlock().getType() == Material.ENCHANTING_TABLE) {
                EventHelpers.craftXPBottle(event);
            }
            // try to teleport to home dimension
            if (event.getClickedBlock().getType() == Material.RESPAWN_ANCHOR) {
                EventHelpers.makePortalKey(event);
            }
        }
    }


}
