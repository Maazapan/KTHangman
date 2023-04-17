package io.github.maazapan.kthangman.listener;

import io.github.maazapan.kthangman.KTHangman;
import io.github.maazapan.kthangman.game.manager.ArenaManager;
import org.bukkit.event.Listener;

public class PlayerListener implements Listener {

    private final ArenaManager arenaManager;
    private KTHangman plugin;

    public PlayerListener(KTHangman plugin) {
        this.plugin = plugin;
        this.arenaManager = plugin.getArenaManager();
    }
}
