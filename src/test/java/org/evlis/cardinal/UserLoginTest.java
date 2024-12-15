package org.evlis.cardinal;


import io.papermc.paper.threadedregions.scheduler.GlobalRegionScheduler;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockbukkit.mockbukkit.MockBukkit.mock;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

public class UserLoginTest {

    private ServerMock server;
    private Cardinal plugin;
    private World world;
    private GlobalRegionScheduler globalRegionScheduler;

    @BeforeEach
    public void setUp()
    {
        // Start test server
        System.out.println("Starting server...");
        server = MockBukkit.mock();
        // Load the plugin
        System.out.println("Loading plugin...");
        plugin = MockBukkit.load(Cardinal.class);
        // Create dummy world
        System.out.println("Generating world...");
        this.world = server.addSimpleWorld("test");
    }

    @AfterEach
    public void tearDown()
    {
        // Stop test server
        MockBukkit.unmock();
    }

    //@Test
    //void playerJoinsServer() {
        // Simulate a player joining
        //PlayerMock player = server.addPlayer("Shakira");

        // Verify your plugin's behavior
        //assertEquals(GameMode.SURVIVAL, player.getGameMode());
        //player.disconnect();
    //}
}