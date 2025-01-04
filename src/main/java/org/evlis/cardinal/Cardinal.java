package org.evlis.cardinal;

import java.util.Set;
import co.aikar.commands.PaperCommandManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;
import org.evlis.cardinal.commands.*;
import org.evlis.cardinal.events.*;
import org.evlis.cardinal.tooltrims.SmithingTemplate;
import org.evlis.cardinal.tooltrims.TrimMaterials;
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
        // ToolTrims Setup
        Set<Material> tools = Set.of(
                Material.NETHERITE_SWORD,       Material.DIAMOND_SWORD,     Material.GOLDEN_SWORD,      Material.IRON_SWORD,    Material.STONE_SWORD,   Material.WOODEN_SWORD,
                Material.NETHERITE_PICKAXE,     Material.DIAMOND_PICKAXE,   Material.GOLDEN_PICKAXE,    Material.IRON_PICKAXE,  Material.STONE_PICKAXE, Material.WOODEN_PICKAXE,
                Material.NETHERITE_AXE,         Material.DIAMOND_AXE,       Material.GOLDEN_AXE,        Material.IRON_AXE,      Material.STONE_AXE,     Material.WOODEN_AXE,
                Material.NETHERITE_SHOVEL,      Material.DIAMOND_SHOVEL,    Material.GOLDEN_SHOVEL,     Material.IRON_SHOVEL,   Material.STONE_SHOVEL,  Material.WOODEN_SHOVEL,
                Material.NETHERITE_HOE,         Material.DIAMOND_HOE,       Material.GOLDEN_HOE,        Material.IRON_HOE,      Material.STONE_HOE,     Material.WOODEN_HOE,
                Material.BOW, Material.CROSSBOW, Material.MACE);
        int model_data = 311000;
        for (SmithingTemplate trim: SmithingTemplate.values()) {
            for (TrimMaterials trim_material: TrimMaterials.values()) {

                model_data++;
                String trimName = (trim.name() + "_" + trim_material.name()).toLowerCase();
                TrimEvents.ItemCompatibility.modalDataToTrimName.put(model_data, trimName);

                for (Material tool: tools) {
                    String key = (tool.name() + "_" + trimName).toLowerCase();
                    new ToolTrim(
                            key, tool, trim_material, trim, model_data
                    );
                }
            }
        }
        // Register Event Listeners
        Bukkit.getServer().getPluginManager().registerEvents(entitySpawn, this);
        Bukkit.getServer().getPluginManager().registerEvents(playerPortal, this);
        Bukkit.getServer().getPluginManager().registerEvents(playerInteract, this);
        Bukkit.getServer().getPluginManager().registerEvents(worldChange, this);
        Bukkit.getServer().getPluginManager().registerEvents(new TrimEvents(), this);
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
