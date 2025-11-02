package org.evlis.cardinal.helpers;

import org.bukkit.Difficulty;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.evlis.cardinal.Cardinal;
import org.evlis.cardinal.WorldOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.evlis.cardinal.GlobalVars.REQUIRED_KEYS;
import static org.evlis.cardinal.GlobalVars.VALID_KEYS;

public class BookValidator {
    static Plugin plugin = Cardinal.getInstance();
    public static WorldOptions parse(String bookYAML, Player player){
        // Initialize the SnakeYAML parser. This is safely included with Bukkit/Spigot.
        Yaml yaml = new Yaml();
        // Initialize a new World Options object:
        WorldOptions options = new WorldOptions();
        // Generic object to contain book while we're checking it:
        Object parsedBook;
        try {
            // Attempt to load the book's content as YAML.
            // If it succeeds, the YAML is valid.
            parsedBook = yaml.load(bookYAML);
            player.sendMessage("§c§lSYSTEM:§r§o§a " + "This is valid YAML.");
        } catch (YAMLException e) {
            // If a YAMLException is thrown, the content is not valid YAML.
            player.sendMessage("§c§lSYSTEM:§r§o§c " + "your book is not formatted correctly...");
            plugin.getLogger().severe("Book is not formatted correctly: " + e);
            return null;
        }
        // Validate that it's a Map
        Map<String, Object> yamlMap;
        if (parsedBook instanceof Map) {
            @SuppressWarnings("unchecked") // Use unchecked cast, but it's now safe bc in expects ANY Object as vals
            Map<String, Object> rawMap = (Map<String, Object>) parsedBook;
            yamlMap = rawMap;
        } else {
            player.sendMessage("§c§lSYSTEM:§r§o§c " + "your book must be formatted as (key: value) pairs.");
            return null;
        }
        Set<String> bookKeys = yamlMap.keySet();

        // Check for minimal required keys
        if (!bookKeys.containsAll(REQUIRED_KEYS)) {
            Set<String> missingKeys = new HashSet<>(REQUIRED_KEYS);
            missingKeys.removeAll(bookKeys);
            player.sendMessage("§c§lSYSTEM:§r§o§c " + "Missing required key(s): " + String.join(", ", missingKeys) + ".");
            return null;
        }

        // Check for invalid/extra keys
        Set<String> invalidKeys = new HashSet<>(bookKeys);
        invalidKeys.removeAll(VALID_KEYS);
        if (!invalidKeys.isEmpty()) {
            player.sendMessage("§c§lSYSTEM:§r§o§c " + "Found invalid key(s): " + String.join(", ", invalidKeys) + ". Only " + VALID_KEYS + " are allowed.");
            return null;
        }

        //========[ CAST YAML DATA ]==========//
        options.setWorldName(yamlMap.get("name").toString());
        Object seedObj = yamlMap.get("seed");
        options.setSeed((Integer) seedObj);
        String difficulty;
        if (yamlMap.containsKey("diff")) {
            String diffStr = yamlMap.get("diff").toString();
            difficulty = diffStr.toUpperCase();
        } else {
            difficulty = Difficulty.EASY.toString();
        }
        options.setDifficulty(Difficulty.valueOf(difficulty));
        // return the finalized options:
        return options;
    }
}
