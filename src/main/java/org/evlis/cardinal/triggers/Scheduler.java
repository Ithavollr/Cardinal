package org.evlis.cardinal.triggers;

import io.papermc.paper.threadedregions.scheduler.GlobalRegionScheduler;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.evlis.cardinal.GlobalVars;

import java.util.List;



public class Scheduler {
    public void DecayWorld(Plugin plugin) {
        GlobalRegionScheduler globalRegionScheduler = plugin.getServer().getGlobalRegionScheduler();

        globalRegionScheduler.runAtFixedRate(plugin, (t)-> {
            for (World world : Bukkit.getWorlds()) {
                // Check if the world has active players
                List<Player> playerList = world.getPlayers();
                if (playerList.isEmpty()) {
                    continue; // Skip worlds with no active players
                } else if (GlobalVars.lostWorlds.contains(world.getName())){
                    world.setTime(18000);
                }
            }
        }, 1L, 20L); // Check every 20 ticks (1 second)
    }
}
