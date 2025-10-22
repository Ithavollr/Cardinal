package org.evlis.cardinal;

import co.aikar.commands.PaperCommandManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.evlis.cardinal.commands.*;
import org.evlis.cardinal.events.*;
import org.evlis.cardinal.helpers.LogHandler;
import org.flywaydb.core.Flyway;
import org.mvplugins.multiverse.core.MultiverseCore;
import org.mvplugins.multiverse.core.MultiverseCoreApi;
import org.mvplugins.multiverse.portals.MultiversePortalsApi;

import java.util.logging.Logger;

public class Cardinal extends JavaPlugin {

    private static Cardinal instance;

    private static MultiverseCoreApi mv;
    private static MultiversePortalsApi mvp;

    public PlayerPortal playerPortal;
    public PlayerInteract playerInteract;
    public PlayerJoin playerJoin;
    public WorldChange worldChange;

    private final Logger logger = getLogger();

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        // Assign Multiverse variables
        mv = MultiverseCoreApi.get();
        mvp = MultiversePortalsApi.get();
        // Initialize custom logger
        logger.setUseParentHandlers(false); // Disable parent handlers to avoid duplicate logging
        for (java.util.logging.Handler handler : logger.getHandlers()) {
            logger.removeHandler(handler);
        }
        LogHandler handler = new LogHandler();
        logger.addHandler(handler);
        logger.info("Starting Cardinal on Minecraft version: " + Bukkit.getVersion());
        logger.info("And Bukkit version: " + Bukkit.getBukkitVersion());
        // Run db migrations
        Flyway flyway = Flyway.configure()
                .dataSource("jdbc:sqlite:users.db", null, null)
                .locations("classpath:db/migration")
                .load();
        flyway.migrate();
        // Initialize Event Variables
        playerPortal = new PlayerPortal();
        playerInteract = new PlayerInteract();
        playerJoin = new PlayerJoin();
        worldChange = new WorldChange();
        // Register Event Listeners
        Bukkit.getServer().getPluginManager().registerEvents(playerPortal, this);
        Bukkit.getServer().getPluginManager().registerEvents(playerInteract, this);
        Bukkit.getServer().getPluginManager().registerEvents(playerJoin, this);
        Bukkit.getServer().getPluginManager().registerEvents(worldChange, this);
        // Register Commands
        registerCommands();
        // Copy resources
        saveResource("wordlist.json", false);
        // Config Initialization
        saveDefaultConfig();
        loadGlobalConfig();
        // Assign instance variable
        // instance = this;
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

    public void loadGlobalConfig() {
        try {
            // Set default values for missing keys
            getConfig().addDefault("seaSalt", "one");

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
    // getters
    public static Cardinal getInstance() { return instance; }
    public static MultiverseCoreApi getMVCore() {return mv;}
    public static MultiversePortalsApi getMVPortal() {return mvp;}
}
