package io.github.maazapan.kthangman.game.countdown;

import io.github.maazapan.kthangman.KTHangman;
import io.github.maazapan.kthangman.game.player.GameArena;
import io.github.maazapan.kthangman.game.player.GamePlayer;
import io.github.maazapan.kthangman.utils.KatsuUtils;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.stream.Collectors;

public class StartCountdown extends BukkitRunnable {

    private final GameArena gameArena;

    private final FileConfiguration messages;
    private int countdownTime = 5;

    public StartCountdown(KTHangman plugin, GameArena gameArena) {
        this.gameArena = gameArena;
        this.messages = plugin.getLoaderManager()
                .getFileManager()
                .getMessages();
    }

    /**
     * Run the countdown.
     */
    @Override
    public void run() {
        if (countdownTime > 0) {
            List<Player> arenaPlayers = gameArena.getGamePlayers().stream()
                    .map(GamePlayer::getUUID).filter(uuid -> Bukkit.getPlayer(uuid) != null)
                    .map(Bukkit::getPlayer).collect(Collectors.toList());

            for (Player player : arenaPlayers) {
                player.sendMessage(KatsuUtils.coloredHex(messages.getString("start-countdown").replace("%time%", String.valueOf(countdownTime))));
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 10, 1);

                String[] titles = KatsuUtils.coloredHex(messages.getString("titles.start-countdown")).split(";");
                player.sendTitle(titles[0].replaceAll("%time%", String.valueOf(countdownTime)), titles[1].replaceAll("%time%", String.valueOf(countdownTime)), 0, 40, 0);
            }
        }

        // Start the game.
        if (countdownTime <= 0) {
            gameArena.startGame();
            cancel();
        }
        countdownTime--;
    }
}
