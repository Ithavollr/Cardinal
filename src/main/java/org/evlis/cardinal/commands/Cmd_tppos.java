package org.evlis.cardinal.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Syntax;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("tppos")
@CommandPermission("cardinal.command.tppos")
public class Cmd_tppos extends BaseCommand {

    private static final double WORLD_LIMIT = 30000000.0;

    @Default
    @Syntax("<x> <y> <z> [yaw]")
    public void onTpPos(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            throw new InvalidCommandArgument("This command can only be run by players.");
        }

        Player player = (Player) sender;

        if (args.length < 3 || args.length > 4) {
            throw new InvalidCommandArgument("Usage: /tppos <x> <y> <z> [yaw]");
        }

        try {
            // Parse x, y, z coordinates
            double x = Double.parseDouble(args[0]);
            double y = Double.parseDouble(args[1]);
            double z = Double.parseDouble(args[2]);

            // Validate coordinates against world limits
            if (Math.abs(x) > WORLD_LIMIT || Math.abs(y) > WORLD_LIMIT || Math.abs(z) > WORLD_LIMIT) {
                throw new InvalidCommandArgument("Coordinates must be within ±" + WORLD_LIMIT + ".");
            }

            // Parse optional yaw
            float yaw = args.length == 4 ? Float.parseFloat(args[3]) : player.getLocation().getYaw();

            // Set the new location
            Location origin = player.getLocation();
            Location destination = new Location(player.getWorld(), x, y, z, yaw, origin.getPitch());

            int distance = (int)origin.distance(destination);
            int exp = player.calculateTotalExperiencePoints();
            if(distance >= exp) {
                player.sendMessage("§c§lSYSTEM:§r§o§c You don't have enough experience to travel that far!");
            } else {
                // Show particles at the current location
                playTeleportEffect(player.getLocation());
                // Teleport the player
                player.teleport(destination);
                // Show particles at the target location
                playTeleportEffect(destination);
                player.setExperienceLevelAndProgress(exp  - distance);
                player.sendMessage("§6Teleport cost: " + distance + "xp");
            }
        } catch (NumberFormatException e) {
            throw new InvalidCommandArgument("All arguments must be valid numbers.");
        }
    }

    //=============/ HELPER FUNCTIONS /=============//
    /**
     * Displays a portal-like effect using particles.
     *
     * @param location The location where the effect should appear.
     */
    private void playTeleportEffect(Location location) {
        // Adjust to spawn particles around the location
        for (int i = 0; i < 50; i++) {
            double offsetX = (Math.random() - 0.5) * 2;
            double offsetY = (Math.random() - 0.5) * 2;
            double offsetZ = (Math.random() - 0.5) * 2;
            location.getWorld().spawnParticle(
                    Particle.PORTAL, // The particle type
                    location.clone().add(offsetX, offsetY, offsetZ), // Offset position
                    1, // Particle count
                    0, 0, 0, // Directional speed (no movement)
                    0 // Extra data (no specific options for this particle)
            );
        }
    }
}
