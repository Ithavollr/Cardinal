package org.evlis.cardinal;

import org.bukkit.plugin.java.JavaPlugin;
import org.evlis.cardinal.triggers.Scheduler;

public final class Cardinal extends JavaPlugin {

    @Override
    public void onEnable() {
        Scheduler schedule = new Scheduler();
        schedule.ShatterWorld(this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
