package org.evlis.cardinal.events;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Lectern;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.evlis.cardinal.helpers.EventHelpers;
import org.evlis.cardinal.helpers.WorldHelpers;


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
            // book interactions with lectern
            if (clickedBlock.getType() == Material.LECTERN) {
                Player player = event.getPlayer();
                World world = player.getWorld();
                Lectern lectern = (Lectern) clickedBlock;
                // World books only apply to The End
                if (world.getName().equals("world_the_end")) {
                    ItemStack maybeBookPlayer = player.getInventory().getItemInMainHand();
                    ItemStack maybeBookLectern = lectern.getInventory().getItem(0);
                    // Check if the player is holding a book. If it has text, validate it.
                    // If the player is taking a book instead, reset the portal.
                    if (maybeBookPlayer.getType() == Material.WRITTEN_BOOK) {
                        EventHelpers.validateBook(event, player, world, maybeBookPlayer, clickedBlock);
                    } else if (maybeBookLectern.getType() == Material.WRITTEN_BOOK && maybeBookPlayer == ItemStack.empty()) {
                        WorldHelpers.updateMasterPortal("world_the_end");
                    }
                }
            }
        }
    }
}
