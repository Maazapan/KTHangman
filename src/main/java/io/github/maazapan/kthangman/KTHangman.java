package io.github.maazapan.kthangman;

import io.github.maazapan.kthangman.game.manager.ArenaManager;
import io.github.maazapan.kthangman.manager.LoaderManager;
import io.github.maazapan.kthangman.manager.files.FileCreator;
import io.github.maazapan.kthangman.manager.files.FileManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class KTHangman extends JavaPlugin {

    private ArenaManager arenaManager;
    private LoaderManager loaderManager;

    @Override
    public void onEnable() {
        // Plugin startup logic
        this.arenaManager = new ArenaManager(this);
        this.loaderManager = new LoaderManager(this);

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

    public String getPrefix() {
        return getConfig().getString("config.prefix");
    }
}
