package org.evlis.cardinal;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.List;

public class GlobalVars {
    static Plugin plugin = Cardinal.getInstance();
    public static final List<String> WORDLIST = slurpJson("wordlist.json", plugin);
    public static int seaSalt = 1;

    // Method to read JSON into a List<String>
    public static List<String> slurpJson(String fileName, Plugin plugin) {
        File wordlistFile = new File(plugin.getDataFolder(), fileName);
        try (FileReader reader = new FileReader(wordlistFile)) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<String>>() {}.getType();
            return gson.fromJson(reader, listType);
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to read JSON file: " + fileName);
            e.printStackTrace();
            return List.of("Abcdefg"); // Return a dummy list on failure
        }
    }
}