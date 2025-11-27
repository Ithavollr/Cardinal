package org.evlis.cardinal.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.evlis.cardinal.Cardinal;
import org.evlis.cardinal.GlobalVars;
import org.evlis.cardinal.helpers.Cryptography;
import org.evlis.cardinal.helpers.Database;

import java.util.Objects;

public class PlayerJoin implements Listener {
    Plugin plugin = Cardinal.getInstance();
    private final Database database = Cardinal.getDatabase();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String uuid = player.getUniqueId().toString();
        String playerName = player.getName();

        //String uuid = GlobalVars.seaSalt + event.getPlayer().getName();
        String playerKey = Cryptography.generatePlayerKey(uuid);
        // Check and initialize db entries if needed:
        database.ensurePlayerExists(uuid, playerName)
                .thenCompose(v -> {
                    // 2. Load global vars (Task B, which depends on Task A)
                    // .thenCompose is used here to get the result from the next async call
                    return database.getGlobalVars(uuid);
                })
                .thenAccept(optionalJson -> {
                    // 3. Process the result (Task C)
                    String globalVarsJson = optionalJson.orElse("{}");
                    // Run the final update on the main thread
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        plugin.getLogger().info("Successfully initialized and loaded data for " + playerName + ": " + globalVarsJson);
                        // Now we can safely use the loaded data on the main thread.
                    });
                });

        // Send a welcome message to the player when they join
        event.getPlayer().sendMessage("Welcome to " + plugin.getConfig().getString("server-name") +
                "§r, " + event.getPlayer().getName() + "!");
        event.getPlayer().sendMessage(Objects.requireNonNull(plugin.getConfig().getString("server-greeting")));
    }
}
