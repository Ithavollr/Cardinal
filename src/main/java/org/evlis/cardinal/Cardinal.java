package org.evlis.cardinal;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.evlis.cardinal.events.EntitySpawn;
import org.evlis.cardinal.events.PlayerPortal;
import org.evlis.cardinal.events.WorldChange;
import org.evlis.cardinal.triggers.Scheduler;

public final class Cardinal extends JavaPlugin {

    public EntitySpawn entitySpawn;
    public PlayerPortal playerPortal;
    public WorldChange worldChange;

    @Override
    public void onEnable() {
        Scheduler schedule = new Scheduler();
        schedule.ShatterWorld(this);

        entitySpawn = new EntitySpawn();
        playerPortal = new PlayerPortal();
        worldChange = new WorldChange();
        Bukkit.getServer().getPluginManager().registerEvents(entitySpawn, this);
        Bukkit.getServer().getPluginManager().registerEvents(playerPortal, this);
        Bukkit.getServer().getPluginManager().registerEvents(worldChange, this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
