package org.evlis.cardinal.helpers;

import org.bukkit.World;
import org.bukkit.plugin.Plugin;
import org.evlis.cardinal.Cardinal;
import org.mvplugins.multiverse.core.MultiverseCore;
import org.mvplugins.multiverse.core.MultiverseCoreApi;
import org.mvplugins.multiverse.core.world.options.CreateWorldOptions;

public class WorldHelpers {
    static Plugin plugin = Cardinal.getInstance();
    static MultiverseCoreApi mv = Cardinal.getMVCore();

    public static void create(String worldName, int worldSeed) {
        mv.getWorldManager().createWorld(
                CreateWorldOptions
                        .worldName("test")
                        .environment(World.Environment.NORMAL)
        );
    }
    public void list(){
        for (World world : plugin.getServer().getWorlds()) {
            plugin.getLogger().info("World: " + world.getName() + " - Environment: " + world.getEnvironment());
        }
    }
}
