package org.evlis.cardinal;

import java.util.Set;
import co.aikar.commands.PaperCommandManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;
import org.evlis.cardinal.commands.*;
import org.evlis.cardinal.events.*;
import org.evlis.cardinal.triggers.Scheduler;

public class Cardinal extends JavaPlugin {

    public static Cardinal Instance;

    public EntitySpawn entitySpawn;
    public PlayerPortal playerPortal;
    public PlayerInteract playerInteract;
    public WorldChange worldChange;

    @Override
    public void onLoad() {
        Instance = this;
    }

    @Override
    public void onEnable() {
        getLogger().info("Starting Cardinal on Minecraft version: " + Bukkit.getVersion());
        getLogger().info("And Bukkit version: " + Bukkit.getBukkitVersion());
        // Start Scheduler & any Shattered Worlds
        Scheduler schedule = new Scheduler();
        schedule.ShatterWorld(this);
        // Initialize Event Variables
        entitySpawn = new EntitySpawn();
        playerInteract = new PlayerInteract();
        playerPortal = new PlayerPortal();
        worldChange = new WorldChange();
        // Register Event Listeners
        Bukkit.getServer().getPluginManager().registerEvents(entitySpawn, this);
        Bukkit.getServer().getPluginManager().registerEvents(playerPortal, this);
        Bukkit.getServer().getPluginManager().registerEvents(playerInteract, this);
        Bukkit.getServer().getPluginManager().registerEvents(worldChange, this);
        // Register Commands
        registerCommands();
    }

    public void registerCommands() {
        PaperCommandManager manager = new PaperCommandManager(this);
        manager.registerCommand(new CoreCommands(this));
        manager.registerCommand(new Cmd_fly());
        manager.registerCommand(new Cmd_tppos());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
