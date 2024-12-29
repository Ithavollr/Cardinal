package org.evlis.cardinal.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.annotation.*;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

@CommandAlias("cardinal")
@CommandPermission("cardinal.command.*")
public class CoreCommands extends BaseCommand {

    private final Plugin plugin; // stores the reference to our main plugin

    public CoreCommands(Plugin plugin) {
        this.plugin = plugin;
    }

    private static final int WORLD_LIMIT = 30000000;

    @Default
    public void defCommand(CommandSender sender) {
        // Display GlobalVars status
        sender.sendMessage("You are running Cardinal v" + plugin.getPluginMeta().getVersion());
        sender.sendMessage("Available commands: fly, tppos");
    }

    @Subcommand("fly")
    @Description("Toggle fly on or off for a player.")
    public void onFly(CommandSender sender, @Optional Player target) {
        // Determine the target player
        Player playerToToggle;
        if (target == null) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("You must specify a player when running this command from the console.");
                return;
            }
            playerToToggle = (Player) sender;
        } else {
            playerToToggle = target;
        }

        // Toggle flight ability
        boolean isFlying = playerToToggle.getAllowFlight();
        playerToToggle.setAllowFlight(!isFlying);
        playerToToggle.setFlying(!isFlying); // Also toggle the flying state if currently flying

        // Feedback to the user
        String status = isFlying ? "disabled" : "enabled";
        sender.sendMessage("Flying " + status + " for " + playerToToggle.getName() + ".");
        if (target != null && sender != playerToToggle) {
            playerToToggle.sendMessage("Flying has been " + status + " by " + sender.getName() + ".");
        }
    }

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
            int x = Integer.parseInt(args[0]);
            int y = Integer.parseInt(args[1]);
            int z = Integer.parseInt(args[2]);

            // Validate coordinates against world limits
            if (Math.abs(x) > WORLD_LIMIT || Math.abs(y) > WORLD_LIMIT || Math.abs(z) > WORLD_LIMIT) {
                throw new InvalidCommandArgument("Coordinates must be within ±" + WORLD_LIMIT + ".");
            }

            // Parse optional yaw
            float yaw = args.length == 4 ? Float.parseFloat(args[3]) : player.getLocation().getYaw();

            // Set the new location
            Location destination = new Location(player.getWorld(), x, y, z, yaw, player.getLocation().getPitch());

            // Show particles at the current location
            playTeleportEffect(player.getLocation());
            // Teleport the player
            player.teleport(destination);
            // Show particles at the target location
            playTeleportEffect(destination);

            player.sendMessage("Teleported to: " + x + ", " + y + ", " + z + (args.length == 4 ? " with yaw " + yaw : ""));
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
