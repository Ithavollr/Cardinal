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
}
