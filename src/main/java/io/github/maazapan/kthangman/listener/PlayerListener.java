package io.github.maazapan.kthangman.listener;

import io.github.maazapan.kthangman.KTHangman;
import io.github.maazapan.kthangman.game.manager.ArenaManager;
import io.github.maazapan.kthangman.game.player.GameArena;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

    private final ArenaManager arenaManager;
    private KTHangman plugin;

    public PlayerListener(KTHangman plugin) {
        this.plugin = plugin;
        this.arenaManager = plugin.getArenaManager();
    }

    /**
     * Check player is quit at the server
     * and remove from the game.
     *
     * @param event PlayerQuitEvent
     */
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        // Check player is playing at the arena.
        if (arenaManager.isPlayingPlayer(player.getUniqueId())) {
            GameArena arena = arenaManager.getArenaByPlayer(player.getUniqueId());
            arenaManager.leaveArena(arena, player);
        }
    }
}
