package org.evlis.cardinal.triggers;

import io.papermc.paper.threadedregions.scheduler.GlobalRegionScheduler;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Lectern;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.evlis.cardinal.helpers.WorldHelpers;


public class Scheduler {
    public void mainLoop(Plugin plugin) {
        World the_end = plugin.getServer().getWorld("world_the_end");
        GlobalRegionScheduler globalRegionScheduler = plugin.getServer().getGlobalRegionScheduler();
        globalRegionScheduler.runAtFixedRate(plugin, (t)-> {
            // confirm the lectern, check against portal
            Block maybeLectern = the_end.getBlockAt(-3, 33, 5);
            if (maybeLectern.getType() == Material.LECTERN) {
                ItemStack maybeBook = ((Lectern) maybeLectern).getInventory().getItem(0);
                if (maybeBook == ItemStack.empty()) {
                    WorldHelpers.updateMasterPortal("world_the_end");
                }
            }
        }, 1L, 100L); // Check every 100 ticks (5 seconds)
    }
}
