package io.github.maazapan.kthangman.listener;

import io.github.maazapan.kthangman.KTHangman;
import io.github.maazapan.kthangman.game.Arena;
import io.github.maazapan.kthangman.game.manager.ArenaManager;
import io.github.maazapan.kthangman.game.player.GameArena;
import io.github.maazapan.kthangman.game.state.ArenaState;
import io.github.maazapan.kthangman.manager.files.FileCreator;
import io.github.maazapan.kthangman.utils.KatsuUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.List;
import java.util.stream.Collectors;

public class ArenaListener implements Listener {

    private final ArenaManager arenaManager;
    private final FileConfiguration messages;

    private final KTHangman plugin;

    public ArenaListener(KTHangman plugin) {
        this.plugin = plugin;
        this.arenaManager = plugin.getArenaManager();
        this.messages = plugin.getLoaderManager()
                .getFileManager()
                .getMessages();
    }

    /**
     * Check player is use chat in arena, and
     * if the arena is in game, then check is the correct word.
     *
     * @param event AsyncPlayerChatEvent
     */
    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        String message = event.getMessage();
        Player player = event.getPlayer();

        if (arenaManager.isPlayingPlayer(player.getUniqueId())) {
            GameArena gameArena = arenaManager.getArenaByPlayer(player.getUniqueId());

            if (gameArena.getState() == ArenaState.PLAYING) {
                event.setCancelled(true);
                gameArena.checkWord(message);
            }
        }
    }

    /**
     * Check player is regaining health in arena.
     *
     * @param event EntityRegainHealthEvent
     */
    @EventHandler
    public void onHealthChange(EntityRegainHealthEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();

        if (arenaManager.isPlayingPlayer(player.getUniqueId())) {
            GameArena gameArena = arenaManager.getArenaByPlayer(player.getUniqueId());

            if (gameArena.getState() == ArenaState.PLAYING) {
                event.setCancelled(true);
            }
        }
    }


    /**
     * Check player is using command in arena.
     * disable command if the arena is in game.
     *
     * @param event PlayerCommandPreprocessEvent
     */
    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String command = event.getMessage();

        if (arenaManager.isPlayingPlayer(player.getUniqueId())) {
            FileConfiguration config = plugin.getConfig();
            List<String> allowedCommands = config.getStringList("config.allowed-commands")
                    .stream().map(String::toLowerCase)
                    .collect(Collectors.toList());

            if (!allowedCommands.contains(command.toLowerCase())) {
                event.setCancelled(true);
                player.sendMessage(KatsuUtils.coloredHex(plugin.getPrefix() + messages.getString("arena-commands-disabled")));
            }
        }
    }
}
