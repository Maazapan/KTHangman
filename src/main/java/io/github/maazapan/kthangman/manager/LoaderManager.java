package io.github.maazapan.kthangman.manager;

import io.github.maazapan.kthangman.KTHangman;
import io.github.maazapan.kthangman.commands.ArenaCommand;
import io.github.maazapan.kthangman.game.manager.ArenaLoader;
import io.github.maazapan.kthangman.game.manager.ArenaManager;
import io.github.maazapan.kthangman.game.player.GameArena;
import io.github.maazapan.kthangman.game.player.GamePlayer;
import io.github.maazapan.kthangman.listener.ArenaListener;
import io.github.maazapan.kthangman.listener.PlayerListener;
import io.github.maazapan.kthangman.manager.files.FileManager;
import io.github.maazapan.kthangman.manager.task.TaskManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class LoaderManager {

    private final KTHangman plugin;

    private FileManager fileManager;
    private final ArenaLoader arenaLoader;

    public LoaderManager(KTHangman plugin) {
        this.plugin = plugin;
        this.arenaLoader = new ArenaLoader(plugin);
    }

    public void load() {
        this.registerFiles();
        this.registerListener();
        this.registerCommands();
        this.registerTask();
        arenaLoader.loadArenas();
    }

    public void disable() {
        this.leavePlayersArena();
        arenaLoader.saveArenas();
    }

    private void registerCommands() {
        plugin.getCommand("hm").setExecutor(new ArenaCommand(plugin));
    }

    private void registerListener() {
        plugin.getServer().getPluginManager().registerEvents(new PlayerListener(plugin), plugin);
        plugin.getServer().getPluginManager().registerEvents(new ArenaListener(plugin), plugin);
    }

    /**
     * Load all configuration files.
     */
    private void registerFiles() {
        fileManager = new FileManager(plugin);
        fileManager.loadFiles();
    }

    /**
     * Register and run the all plugin task.
     */
    private void registerTask() {
        TaskManager taskManager = new TaskManager(plugin);
        taskManager.runTask();
    }

    public FileManager getFileManager() {
        return fileManager;
    }

    /**
     * Leave all players from the arena, these
     * method is used when the server is stopping.
     */
    private void leavePlayersArena() {
        ArenaManager arenaManager = plugin.getArenaManager();

        for (GameArena arena : arenaManager.getPlayingArenas()) {
            List<Player> arenaPlayers = arena.getGamePlayers().stream()
                    .map(GamePlayer::getUUID).filter(uuid -> Bukkit.getPlayer(uuid) != null)
                    .map(Bukkit::getPlayer).collect(Collectors.toList());

            arenaPlayers.forEach(player -> arenaManager.leaveArena(arena, player));
        }
    }
}
