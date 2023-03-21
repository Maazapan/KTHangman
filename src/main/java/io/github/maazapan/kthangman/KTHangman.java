package io.github.maazapan.kthangman;

import io.github.maazapan.kthangman.game.manager.ArenaManager;
import io.github.maazapan.kthangman.game.manager.scoreboard.FastBoard;
import io.github.maazapan.kthangman.manager.LoaderManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class KTHangman extends JavaPlugin {

    private ArenaManager arenaManager;
    private LoaderManager loaderManager;

    private Map<UUID, FastBoard> scoreboardMap;

    @Override
    public void onEnable() {
        // Plugin startup logic
        this.arenaManager = new ArenaManager(this);
        this.loaderManager = new LoaderManager(this);
        this.scoreboardMap = new HashMap<>();

        loaderManager.load();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        loaderManager.disable();
    }

    public LoaderManager getLoaderManager() {
        return loaderManager;
    }

    public ArenaManager getArenaManager() {
        return arenaManager;
    }

    public Map<UUID, FastBoard> getScoreboardMap() {
        return scoreboardMap;
    }

    public String getPrefix() {
        return getConfig().getString("config.prefix");
    }
}
