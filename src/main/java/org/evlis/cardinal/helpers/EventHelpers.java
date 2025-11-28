package org.evlis.cardinal.helpers;


import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.plugin.Plugin;
import org.evlis.cardinal.Cardinal;
import org.evlis.cardinal.WorldOptions;
import java.util.Set;


public class EventHelpers {
    static Plugin plugin = Cardinal.getInstance();
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
    // Used when interacting with a lectern to create a world
    public static void validateBook(PlayerInteractEvent event, Player player, World world, ItemStack book, Block lectern) {
        // Get the book's metadata from the item in hand.
        BookMeta bookMeta = (BookMeta) book.getItemMeta();
        if (bookMeta == null) {
            return;
        }
        // Combine all pages of the book into a single string, separated by newlines.
        String bookContent = String.join("\n", bookMeta.getPages());
        // If the book is empty, there's no need to parse it.
        if (bookContent.trim().isEmpty()) {
            player.sendMessage("§c§lSYSTEM:§r§o§c " + "your book is empty...");
            return;
        }
        // Check book contents, get world options:
        WorldOptions options = BookValidator.parse(bookContent, player);
        if (options != null) {
            // Check if the world exists:
            Set<String> worldlist = WorldHelpers.list();
            if (worldlist.contains(options.getWorldName())) {
                WorldHelpers.updateMasterPortal(options.getWorldName());
            } else { //========[ CREATE THE WORLD ]========//
                boolean success = false;
                try {
                    player.sendMessage("§c§lSYSTEM:§r§o§a " + "Creating world '" + options.getWorldName() + "'.");
                    world.spawnParticle(Particle.END_ROD,
                            lectern.getLocation().add(0, 0.9, 0), // Location at book base
                            12, 0.0, 0.0, 0.0, 0.0 // Count 1, slight random offset, speed 0);
                    );
                    success = WorldHelpers.create(options);
                } catch (Exception e){
                    player.sendMessage("§c§lSYSTEM:§r§o§c An error occurred during world creation.");
                    plugin.getLogger().severe("Error during world creation: " + e);
                }
                if (success) {
                    player.sendMessage("§c§lSYSTEM:§r§o§a new world created.");
                    WorldHelpers.updateMasterPortal(options.getWorldName());
                } else {
                    player.sendMessage("§c§lSYSTEM:§r§o§c world could not be created.");
                }
            }
        }
    }
}
