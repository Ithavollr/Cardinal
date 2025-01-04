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

    @Default
    public void defCommand(CommandSender sender) {
        // Display GlobalVars status
        sender.sendMessage("You are running Cardinal v" + plugin.getPluginMeta().getVersion());
        sender.sendMessage("Available commands: fly, tppos");
    }
}
