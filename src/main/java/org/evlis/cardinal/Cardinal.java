package org.evlis.cardinal;

import co.aikar.commands.PaperCommandManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.evlis.cardinal.commands.*;
import org.evlis.cardinal.events.*;
import org.evlis.cardinal.helpers.LogHandler;

import java.util.logging.Logger;

public class Cardinal extends JavaPlugin {

    public static Cardinal instance;

    public PlayerPortal playerPortal;
    public PlayerInteract playerInteract;
    public WorldChange worldChange;

    private final Logger logger = getLogger();

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        logger.setUseParentHandlers(false); // Disable parent handlers to avoid duplicate logging
        for (java.util.logging.Handler handler : logger.getHandlers()) {
            logger.removeHandler(handler);
        }
        LogHandler handler = new LogHandler();
        logger.addHandler(handler);
        logger.info("Starting Cardinal on Minecraft version: " + Bukkit.getVersion());
        logger.info("And Bukkit version: " + Bukkit.getBukkitVersion());
        // Initialize Event Variables
        playerInteract = new PlayerInteract();
        playerPortal = new PlayerPortal();
        worldChange = new WorldChange();
        // Register Event Listeners
        Bukkit.getServer().getPluginManager().registerEvents(playerPortal, this);
        Bukkit.getServer().getPluginManager().registerEvents(playerInteract, this);
        Bukkit.getServer().getPluginManager().registerEvents(worldChange, this);
        // Register Commands
        registerCommands();
        // Copy resources
        saveResource("wordlist.json", false);
        // Config Initialization
        saveDefaultConfig();
        loadGlobalConfig();
        // Assign instance variable
        instance = this;
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

    public static Cardinal getInstance() {
        return instance;
    }

    public void loadGlobalConfig() {
        try {
            // Set default values for missing keys
            getConfig().addDefault("seaSalt", 1);

            // Apply defaults if missing
            getConfig().options().copyDefaults(true);
            saveConfig();

            // Load values into GlobalVars
            GlobalVars.seaSalt = getConfig().getString("seaSalt");
        } catch (Exception e) {
            logger.info("Failed to load configuration! Disabling plugin. Error: " + e.getMessage());
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }
}
