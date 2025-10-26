package org.evlis.cardinal.helpers;

import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.WorldType;
import org.bukkit.plugin.Plugin;
import org.evlis.cardinal.Cardinal;
import org.mvplugins.multiverse.core.MultiverseCore;
import org.mvplugins.multiverse.core.MultiverseCoreApi;
import org.mvplugins.multiverse.core.utils.result.Attempt;
import org.mvplugins.multiverse.core.world.LoadedMultiverseWorld;
import org.mvplugins.multiverse.core.world.options.CreateWorldOptions;
import org.mvplugins.multiverse.core.world.reasons.CreateFailureReason;

public class WorldHelpers {
    static Plugin plugin = Cardinal.getInstance();
    static MultiverseCoreApi mv = Cardinal.getMVCore();

    public static boolean create(String worldName, int worldSeed, Difficulty difficulty) {
        Attempt<LoadedMultiverseWorld, CreateFailureReason> newWorld = mv.getWorldManager().createWorld(
                CreateWorldOptions
                        .worldName("test")
                        .seed(worldSeed)
                        .generateStructures(true)
                        .doFolderCheck(true)
                        .worldType(WorldType.NORMAL)
                        .environment(World.Environment.NORMAL)
        );
        if (newWorld.isSuccess()) {
            LoadedMultiverseWorld loadedWorld = newWorld.get();
            loadedWorld.setDifficulty(difficulty);
            loadedWorld.setGameMode(GameMode.SURVIVAL);
            plugin.getLogger().info("World " + newWorld.get().getName() + " created successfully.");
            return true;
        } else {
            plugin.getLogger().severe("Failed to create world " + worldName + ": " + newWorld.getFailureReason());
            return false;
        }
    }
    public void list(){
        for (World world : plugin.getServer().getWorlds()) {
            plugin.getLogger().info("World: " + world.getName() + " - Environment: " + world.getEnvironment());
        }
    }
}
