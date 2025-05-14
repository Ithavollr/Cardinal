package org.evlis.cardinal.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("fly")
@CommandPermission("cardinal.command.fly")
public class Cmd_fly extends BaseCommand {
    @Default
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

    @Subcommand("speed")
    @Description("Change a player's fly speed.")
    public void onFlySpeed(CommandSender sender, @Optional Player target, String speed) {
        // Determine the target player
        Player targetPlayer;
        if (target == null) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("You must specify a player when running this command from the console.");
                return;
            }
            targetPlayer = (Player) sender;
        } else {
            targetPlayer = target;
        }

        // set flying speed
        try {
            float flyspeed = Float.parseFloat(speed);
            if (flyspeed > 10 || flyspeed < 1) {
                targetPlayer.sendMessage("Fly speed must be a number between 1 and 10.");
                return;
            }
            targetPlayer.setFlySpeed(flyspeed / 10f);
        } catch (NumberFormatException e) {
            targetPlayer.sendMessage("ERROR: not a number or incorrect arguments.");
        }
    }
}
