package io.github.maazapan.kthangman.game.manager.scoreboard;

import io.github.maazapan.kthangman.KTHangman;
import io.github.maazapan.kthangman.game.player.GameArena;
import io.github.maazapan.kthangman.utils.KatsuUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;

public class FastManager {

    private final KTHangman plugin;

    public FastManager(KTHangman plugin) {
        this.plugin = plugin;
    }

    public void createPlayingScoreboard(GameArena gameArena, Player player) {
        FileConfiguration messages = plugin.getLoaderManager().getFileManager().getMessages();
        FastBoard fastBoard = new FastBoard(player);

        List<String> lines = messages.getStringList("scoreboard.playing-scoreboard")
                .stream().map(KatsuUtils::coloredHex).collect(Collectors.toList());

        String time  = KatsuUtils.formatTime(gameArena.getCurrentTime() - System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");

        lines.replaceAll(s -> s.replaceAll("%formatted_word%", KatsuUtils.formatDisplayWord(gameArena.getGameWord().getFormattedWord()))
                .replaceAll("%arena_name%", gameArena.getName())
                .replaceAll("%lives%", String.valueOf(gameArena.getCurrentLives()))
                .replaceAll("%time%", time)
                .replaceAll("%date%", dateFormat.format(System.currentTimeMillis()))
                .replaceAll("%tips%", String.valueOf(gameArena.getGameWord().getTips())));

        fastBoard.updateTitle(lines.get(0));
        fastBoard.updateLines(lines.subList(1, lines.size()));

        plugin.getScoreboardMap().put(player.getUniqueId(), fastBoard);
    }

    /**
     * Update scoreboard for player.
     *
     * @param gameArena GameArena at player is playing
     * @param player    Player to update score
     */
    public void updatePlayingScoreboard(GameArena gameArena, Player player) {
        FileConfiguration messages = plugin.getLoaderManager().getFileManager().getMessages();
        FastBoard fastBoard = plugin.getScoreboardMap().get(player.getUniqueId());

        List<String> lines = messages.getStringList("scoreboard.playing-scoreboard")
                .stream().map(KatsuUtils::coloredHex).collect(Collectors.toList());

        String time  = KatsuUtils.formatTime(gameArena.getCurrentTime() - System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");

        lines.replaceAll(s -> s.replaceAll("%formatted_word%", KatsuUtils.formatDisplayWord(gameArena.getGameWord().getFormattedWord()))
                .replaceAll("%arena_name%", gameArena.getName())
                .replaceAll("%lives%", String.valueOf(gameArena.getCurrentLives()))
                .replaceAll("%time%", time)
                .replaceAll("%date%", dateFormat.format(System.currentTimeMillis()))
                .replaceAll("%tips%", String.valueOf(gameArena.getGameWord().getTips())));

        fastBoard.updateLines(lines.subList(1, lines.size()));
    }
}
