package io.github.maazapan.kthangman.game.task;

import io.github.maazapan.kthangman.KTHangman;
import io.github.maazapan.kthangman.game.player.GameArena;
import org.bukkit.scheduler.BukkitRunnable;

public class ArenaTask extends BukkitRunnable {

    private final KTHangman plugin;

    public ArenaTask(KTHangman plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        for (GameArena gameArena : plugin.getArenaManager().getPlayingArenas()) {
            gameArena.updateGame();
        }
    }
}
