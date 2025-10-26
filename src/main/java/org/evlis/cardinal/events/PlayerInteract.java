package org.evlis.cardinal.events;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.evlis.cardinal.helpers.EventHelpers;


public class PlayerInteract implements Listener {
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        //=== RIGHT-CLICK INTERACTIONS: ===//
        if (!event.getAction().isRightClick()) return;
        if (event.getHand() != EquipmentSlot.HAND) return; // filter out off-hand events

        //=========|| HERE ARE THE BLOCK INTERACTIONS ||=========//
        if (event.getClickedBlock() != null) {
            Block clickedBlock = event.getClickedBlock();
            // try to craft a bottle o' enchanting
            if (clickedBlock.getType() == Material.ENCHANTING_TABLE) {
                EventHelpers.craftXPBottle(event);
            }
            // try to teleport to home dimension
            if (clickedBlock.getType() == Material.RESPAWN_ANCHOR) {
                EventHelpers.makePortalKey(event);
            }
            // book interactions with lectern
            if (clickedBlock.getType() == Material.LECTERN) {
                Player player = event.getPlayer();
                World world = player.getWorld();
                // World books only apply to The End
                if (world.getName().equals("world_the_end")) {
                    ItemStack maybeBook = player.getInventory().getItemInMainHand();
                    // Check if the player is holding a book. If not, do nothing.
                    if (maybeBook.getType() == Material.WRITTEN_BOOK) {
                        EventHelpers.validateBook(event, player, world, maybeBook);
                    }
                }
            }
        }
    }
}
