package io.github.maazapan.kthangman.game.manager;

import io.github.maazapan.kthangman.KTHangman;
import io.github.maazapan.kthangman.game.Arena;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class ArenaLoader {

    private final KTHangman plugin;

    public ArenaLoader(KTHangman plugin) {
        this.plugin = plugin;
    }

    /**
     * Save arenas to file.
     */
    public void saveArenas() {
        ArenaManager arenaManager = plugin.getArenaManager();

        try {
            // Create data folder if it doesn't exist.
            if (!Files.exists(Paths.get(plugin.getDataFolder() + "/data"))) {
                Files.createDirectory(Paths.get(plugin.getDataFolder() + "/data"));
            }

            // Delete all files in data folder.
            File[] listFiles = new File(plugin.getDataFolder() + "/data").listFiles();

            if (listFiles != null) {
                Arrays.stream(listFiles).forEach(File::delete);
            }

            // Create a new file and save arena data to it.
            for (Arena arena : arenaManager.getCreatedArenas()) {
                File file = new File(plugin.getDataFolder() + "/data/" + arena.getName() + ".yml");
                file.createNewFile();

                YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

                if (arena.getHang() != null) {
                    String hangmanLocation = arena.getHang().getWorld().getName() + "," +
                            arena.getHang().getX() + "," + arena.getHang().getY() + "," +
                            arena.getHang().getZ() + "," + arena.getHang().getYaw() + "," + arena.getHang().getPitch();
                    config.set("hangman-location", hangmanLocation);
                }

                if (arena.getSpawn() != null) {
                    String spawnLocation = arena.getSpawn().getWorld().getName() + "," +
                            arena.getSpawn().getX() + "," + arena.getSpawn().getY() + "," +
                            arena.getSpawn().getZ() + "," + arena.getSpawn().getYaw() + "," + arena.getSpawn().getPitch();
                    config.set("spawn-location", spawnLocation);
                }

                // Save game words and tips.
                for (String word : arena.getGameWord().getWords().keySet()) {
                    config.set("list-of-words." + word, arena.getGameWord().getWords().get(word));
                }

                config.set("enabled", arena.isEnabled());
                config.set("time", arena.getTime());
                config.set("word-tips", arena.getGameWord().getTips());

                config.save(file);
            }
            plugin.getLogger().info("Successfully saved arenas.");

        } catch (Exception e) {
            plugin.getLogger().warning("Failed to save arenas, make sure the arena is created correctly.");
            e.printStackTrace();
        }
    }

    /**
     * Load arenas from file and put in the list.
     */
    public void loadArenas() {
        ArenaManager arenaManager = plugin.getArenaManager();

        try {
            if (!Files.exists(Paths.get(plugin.getDataFolder() + "/data"))) return;
            File[] listFiles = new File(plugin.getDataFolder() + "/data").listFiles();

            if (listFiles == null) return;
            for (File file : listFiles) {
                FileConfiguration config = YamlConfiguration.loadConfiguration(file);
                String name = file.getName().replace(".yml", "");

                boolean enabled = config.getBoolean("enabled");
                int time = config.getInt("time");
                int tips = config.getInt("word-tips");

                // Create a new arena.
                Arena arena = new Arena(name);

                // Get locations of spawn and hangman.
                if (config.contains("hangman-location")) {
                    String[] hangSplit = config.getString("hangman-location").split(",");
                    Location hangLocation = new Location(plugin.getServer().getWorld(hangSplit[0]),
                            Double.parseDouble(hangSplit[1]), Double.parseDouble(hangSplit[2]),
                            Double.parseDouble(hangSplit[3]), Float.parseFloat(hangSplit[4]),
                            Float.parseFloat(hangSplit[5]));

                    arena.setHang(hangLocation);
                }

                if (config.contains("spawn-location")) {
                    String[] spawnSplit = config.getString("spawn-location").split(",");
                    Location spawnLocation = new Location(plugin.getServer().getWorld(spawnSplit[0]),
                            Double.parseDouble(spawnSplit[1]), Double.parseDouble(spawnSplit[2]),
                            Double.parseDouble(spawnSplit[3]), Float.parseFloat(spawnSplit[4]),
                            Float.parseFloat(spawnSplit[5]));

                    arena.setSpawn(spawnLocation);
                }

                // Get list of words and tips.
                if (config.contains("list-of-words")) {
                    for (String word : config.getConfigurationSection("list-of-words").getKeys(false)) {
                        arena.getGameWord().getWords().put(word, config.getStringList("list-of-words." + word));
                    }
                }

                arena.setEnabled(enabled);
                arena.setTime(time);
                arena.getGameWord().setTips(tips);

                arenaManager.getCreatedArenas().add(arena);
            }
            plugin.getLogger().info("Successfully loaded arenas.");

        } catch (Exception e) {
            plugin.getLogger().warning("Failed to load arenas, make sure the arena is created correctly.");
            e.printStackTrace();
        }
    }
}
