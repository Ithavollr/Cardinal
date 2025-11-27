package org.evlis.cardinal.helpers;

import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.evlis.cardinal.Cardinal;
import org.evlis.cardinal.WorldOptions;
import org.mvplugins.multiverse.core.MultiverseCore;
import org.mvplugins.multiverse.core.MultiverseCoreApi;
import org.mvplugins.multiverse.core.utils.result.Attempt;
import org.mvplugins.multiverse.core.world.LoadedMultiverseWorld;
import org.mvplugins.multiverse.core.world.options.CreateWorldOptions;
import org.mvplugins.multiverse.core.world.reasons.CreateFailureReason;
import org.mvplugins.multiverse.inventories.MultiverseInventoriesApi;
import org.mvplugins.multiverse.inventories.profile.group.WorldGroup;
import org.mvplugins.multiverse.inventories.profile.group.WorldGroupManager;
import org.mvplugins.multiverse.inventories.share.Sharables;
import org.mvplugins.multiverse.netherportals.MultiverseNetherPortals;
import org.mvplugins.multiverse.portals.MVPortal;
import org.mvplugins.multiverse.portals.MultiversePortals;
import org.mvplugins.multiverse.portals.MultiversePortalsApi;
import org.mvplugins.multiverse.portals.config.PortalsConfig;
import org.mvplugins.multiverse.portals.utils.PortalFiller;
import org.mvplugins.multiverse.portals.utils.PortalManager;

import javax.smartcardio.Card;
import java.util.HashSet;
import java.util.Set;

import static org.evlis.cardinal.GlobalVars.VALID_KEYS;

public class WorldHelpers {
    static Plugin plugin = Cardinal.getInstance();
    static MultiverseCoreApi mv = Cardinal.getMVCore();
    static MultiversePortalsApi mvp = Cardinal.getMVPortal();
    static MultiverseInventoriesApi mvi = Cardinal.getMVInventory();

    public static boolean create(WorldOptions options) {
        Attempt<LoadedMultiverseWorld, CreateFailureReason> newWorld = mv.getWorldManager().createWorld(
                CreateWorldOptions
                        .worldName(options.getWorldName())
                        .seed(options.getSeed())
                        .generateStructures(true)
                        .doFolderCheck(true)
                        .worldType(options.getWorldType())
                        .environment(options.getEnvironment())
        );
        if (newWorld.isSuccess()) {
            LoadedMultiverseWorld loadedWorld = newWorld.get();
            loadedWorld.setDifficulty(options.getDifficulty());
            loadedWorld.setGameMode(GameMode.SURVIVAL);
            plugin.getLogger().info("World " + newWorld.get().getName() + " created successfully.");
            plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "mvnp link nether " + newWorld.get().getName() + " world_nether");
            plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), "mvnp link end " + newWorld.get().getName() + " world_the_end");
            WorldGroupManager groupMgr = mvi.getWorldGroupManager();
            WorldGroup survival = groupMgr.getGroup("survival");
            survival.addWorld(newWorld.get().getName());
            survival.getShares().addAll(Sharables.allOf());
            groupMgr.updateGroup(survival);
            //mvnp.addWorldLink(options.getWorldName(), "world_nether", PortalType.NETHER);
            //mvnp.addWorldLink(options.getWorldName(), "world_the_end", PortalType.END_GATEWAY);
            return true;
        } else {
            plugin.getLogger().severe("Failed to create world " + options.getWorldName() + ": " + newWorld.getFailureReason());
            return false;
        }
    }
    public static Set<String> list(){
        Set<String> worlds = new HashSet<>();
        for (World world : plugin.getServer().getWorlds()) {
            worlds.add(world.getName());
        }
        return worlds;
    }
    public static void updateMasterPortal(Player player, String dest) {
        try {
            PortalManager mgr = mvp.getPortalManager();
            MVPortal mport = mgr.getPortal("master_portal");
            mport.setDestination(dest);
            player.sendMessage("§c§lSYSTEM:§r§o§d " + "the portal now leads to: " + dest);
        } catch (Exception e) {
            player.sendMessage("§c§lSYSTEM:§r§o§c " + "failed to set portal destination..");
            plugin.getLogger().severe("Failed to set portal destination: " + e);
        }
    }
}
