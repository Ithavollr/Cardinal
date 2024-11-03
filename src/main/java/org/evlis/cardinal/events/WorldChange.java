package org.evlis.cardinal.events;

import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.evlis.cardinal.GlobalVars;

public class WorldChange implements Listener {
    @EventHandler
    public void onChangeWorlds(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        World world = player.getWorld();

        // automatically enable fly in the lobby
        if (world.getName().equals("lobby004")) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "fly " + player.getName());
            //player.setFlying(true);
        }
        // set insomnia upon entering a shattered world
        if (GlobalVars.shatteredWorlds.contains(world.getName())) {
            player.incrementStatistic(Statistic.TIME_SINCE_REST, 72000 );
        }
    }
}
