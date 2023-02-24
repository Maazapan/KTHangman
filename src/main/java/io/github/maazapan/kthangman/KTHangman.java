package io.github.maazapan.kthangman;

import io.github.maazapan.kthangman.commands.ArenaCommand;
import io.github.maazapan.kthangman.game.manager.ArenaManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class KTHangman extends JavaPlugin {

    private ArenaManager arenaManager;

    @Override
    public void onEnable() {
        // Plugin startup logic
        this.arenaManager = new ArenaManager(this);

        this.registerCommands();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private void registerCommands() {
        getCommand("hm").setExecutor(new ArenaCommand(this));
    }

    public ArenaManager getArenaManager() {
        return arenaManager;
    }

    public String getPrefix() {
        return getConfig().getString("config.prefix");
    }
}
