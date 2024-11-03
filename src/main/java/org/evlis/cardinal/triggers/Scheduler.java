package org.evlis.cardinal.triggers;

import io.papermc.paper.threadedregions.scheduler.GlobalRegionScheduler;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.evlis.cardinal.GlobalVars;

import java.util.List;



public class Scheduler {
    public void ShatterWorld(Plugin plugin) {
        GlobalRegionScheduler globalRegionScheduler = plugin.getServer().getGlobalRegionScheduler();

        globalRegionScheduler.runAtFixedRate(plugin, (t)-> {
            for (World world : Bukkit.getWorlds()) {
                // Check if the world has active players
                List<Player> playerList = world.getPlayers();
                if (playerList.isEmpty()) {
                    continue; // Skip worlds with no active players
                }
                if (GlobalVars.shatteredWorlds.contains(world.getName())){
                    world.setTime(18000);
                }
            }
        }, 1L, 2L); // Check every 2 ticks
        globalRegionScheduler.runAtFixedRate(plugin, (t)-> {
            for (World world : Bukkit.getWorlds()) {
                // Check if the world has active players
                List<Player> playerList = world.getPlayers();
                if (playerList.isEmpty()) {
                    continue; // Skip worlds with no active players
                }
                if (GlobalVars.shatteredWorlds.contains(world.getName())){
                    world.setStorm(true);
                    world.setWeatherDuration(1400);
                    world.setThundering(true);
                    world.setThunderDuration(1400);
                }
            }
        }, 1L, 1200L); // Check every minute
    }
}
