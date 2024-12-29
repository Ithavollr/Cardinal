package org.evlis.cardinal;

import co.aikar.commands.PaperCommandManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.evlis.cardinal.commands.*;
import org.evlis.cardinal.events.*;
import org.evlis.cardinal.triggers.Scheduler;

public class Cardinal extends JavaPlugin {

    public EntitySpawn entitySpawn;
    public PlayerPortal playerPortal;
    public PlayerTeleport playerTeleport;
    public PlayerInteract playerInteract;
    public WorldChange worldChange;

    @Override
    public void onEnable() {
        // Start Scheduler & any Shattered Worlds
        Scheduler schedule = new Scheduler();
        schedule.ShatterWorld(this);
        // Initialize Event Variables
        entitySpawn = new EntitySpawn();
        playerTeleport = new PlayerTeleport();
        playerInteract = new PlayerInteract();
        playerPortal = new PlayerPortal();
        worldChange = new WorldChange();
        // Register Event Listeners
        Bukkit.getServer().getPluginManager().registerEvents(entitySpawn, this);
        Bukkit.getServer().getPluginManager().registerEvents(playerPortal, this);
        Bukkit.getServer().getPluginManager().registerEvents(playerTeleport, this);
        Bukkit.getServer().getPluginManager().registerEvents(playerInteract, this);
        Bukkit.getServer().getPluginManager().registerEvents(worldChange, this);
        // Register Commands
        registerCommands();
    }

    public void registerCommands() {
        PaperCommandManager manager = new PaperCommandManager(this);
        manager.registerCommand(new CoreCommands(this));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
