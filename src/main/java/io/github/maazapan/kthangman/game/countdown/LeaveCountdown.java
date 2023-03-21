package io.github.maazapan.kthangman.game.countdown;

import io.github.maazapan.kthangman.KTHangman;
import io.github.maazapan.kthangman.game.player.GameArena;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

public class LeaveCountdown extends BukkitRunnable {

    private FileConfiguration messages;
    private GameArena gameArena;

    private int time;

    public LeaveCountdown(GameArena gameArena, KTHangman plugin) {
        this.gameArena = gameArena;
        this.messages = plugin.getLoaderManager()
                .getFileManager()
                .getMessages();
    }

    @Override
    public void run() {

    }
}
