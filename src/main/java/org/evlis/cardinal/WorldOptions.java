package org.evlis.cardinal;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bukkit.Difficulty;
import org.bukkit.World;
import org.bukkit.WorldType;

// Generates setters for all fields
@Setter
// Generates getters for all fields
@Getter
// Generates the default no-argument constructor
@NoArgsConstructor
public class WorldOptions {
    // Fields with Default Values
    private String worldName = "world";
    private int seed = 0;
    private WorldType worldType = WorldType.NORMAL;
    private Difficulty difficulty = Difficulty.EASY;
    private World.Environment environment = World.Environment.NORMAL;
}
