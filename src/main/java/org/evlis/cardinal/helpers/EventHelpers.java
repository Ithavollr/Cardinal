package org.evlis.cardinal.helpers;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class EventHelpers {
    // Used when interacting with an enchanting table
    public static void craftXPBottle(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        // Check if the item in hand is a water bottle
        if (item.getType() != Material.GLASS_BOTTLE) {
            return;
        }
        int exp = player.calculateTotalExperiencePoints();
        // Check if the player has at least 11 XP
        if (exp < 11) {
            player.sendMessage("§clSYSTEM:§roc You don't have enough experience to fill the bottle!");
            return;
        }
        // Deduct 11 XP
        player.setExperienceLevelAndProgress(exp  - 11);
        // Replace one water bottle with one bottle of enchanting
        item.setAmount(item.getAmount() - 1); // Reduce the water bottle stack by 1
        player.getInventory().addItem(new ItemStack(Material.EXPERIENCE_BOTTLE)); // Add a bottle of enchanting
        event.setCancelled(true);
    }
    // Used when interacting with a respawn anchor
    public static void makePortalKey(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        World world = player.getWorld();
        World.Environment environment = world.getEnvironment();
        ItemStack item = player.getInventory().getItemInMainHand();
        // Check if the item in hand is a water bottle
        if (item.getType() == Material.BONE) {
            if (environment == World.Environment.NORMAL) {

            }
            if (environment == World.Environment.THE_END) {
                return; // do nothing in the End
            }
            event.setCancelled(true);
        } else if (environment == World.Environment.NORMAL || environment == World.Environment.THE_END) {
            // cancel explosions in the Overworld and End
            event.setCancelled(true);
        }
    }
}
