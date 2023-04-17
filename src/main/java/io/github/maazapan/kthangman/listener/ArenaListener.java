package io.github.maazapan.kthangman.listener;

import de.tr7zw.changeme.nbtapi.NBTItem;
import io.github.maazapan.kthangman.KTHangman;
import io.github.maazapan.kthangman.game.manager.ArenaManager;
import io.github.maazapan.kthangman.game.player.GameArena;
import io.github.maazapan.kthangman.game.state.ArenaState;
import io.github.maazapan.kthangman.game.word.GameWord;
import io.github.maazapan.kthangman.utils.KatsuUtils;
import io.github.maazapan.kthangman.utils.task.KatsuTask;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;

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
        String message = event.getMessage().toUpperCase();
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
     * Check player is drop item in arena.
     * cancel it.
     *
     * @param event PlayerDropItemEvent
     */
    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();

        if (arenaManager.isPlayingPlayer(player.getUniqueId())) {
            event.setCancelled(true);
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

    /**
     * Disable player damage in arena.
     *
     * @param event EntityDamageByEntityEvent
     */
    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();

            if (arenaManager.isPlayingPlayer(player.getUniqueId())) {
                event.setCancelled(true);
            }
        }
    }

    /**
     * Disable player sneak at spectating.
     *
     * @param event PlayerToggleSneakEvent
     */
    @EventHandler
    public void onPlayerSneak(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (arenaManager.isPlayingPlayer(player.getUniqueId())) {
            GameArena gameArena = arenaManager.getArenaByPlayer(player.getUniqueId());

            if (gameArena.getState() == ArenaState.PLAYING) {
                event.setCancelled(true);
            }
        }
    }


    /**
     * Check player is click at the item.
     *
     * @param event InventoryClickEvent
     */
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        if (arenaManager.isPlayingPlayer(player.getUniqueId())) {
            event.setCancelled(true);
        }
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

    /**
     * Check player is click at the item.
     * if the item is leave item, then leave player from the arena.
     *
     * @param event PlayerInteractEvent
     */
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (event.getItem() != null && event.getAction() == Action.RIGHT_CLICK_AIR) {
            NBTItem nbtItem = new NBTItem(event.getItem());

            if (arenaManager.isPlayingPlayer(player.getUniqueId())) {
                GameArena gameArena = arenaManager.getArenaByPlayer(player.getUniqueId());

                /*
                 - Check player is using item leave.
                 */
                if (nbtItem.hasTag("kthangman-item-leave")) {
                    boolean cancelLeave = nbtItem.getBoolean("kthangman-item-leave");

                    // Cancel leave task.
                    if (cancelLeave) {
                        int taskID = nbtItem.getInteger("kthangman-item-leave-task");
                        Bukkit.getScheduler().cancelTask(taskID);

                        nbtItem.setBoolean("kthangman-item-leave", false);
                        nbtItem.applyNBT(event.getItem());

                        player.sendMessage(KatsuUtils.coloredHex(plugin.getPrefix() + messages.getString("arena-game-leave-cancel")));
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 10, 1);
                        return;
                    }
                    player.sendMessage(KatsuUtils.coloredHex(plugin.getPrefix() + messages.getString("arena-game-leave-item")));
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 10, 1);

                    int taskID = Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> arenaManager.leaveArena(gameArena, player), 100L);

                    nbtItem.setBoolean("kthangman-item-leave", true);
                    nbtItem.setInteger("kthangman-item-leave-task", taskID);

                    nbtItem.applyNBT(event.getItem());
                    return;
                }

                /*
                 - Check player is using item hint.
                 */
                if (nbtItem.hasTag("kthangman-item-tips")) {
                    GameWord gameWord = gameArena.getGameWord();

                    if (!(gameWord.getTips() > 0)) {
                        player.sendMessage(KatsuUtils.coloredHex(plugin.getPrefix() + messages.getString("arena-game-no-tips")));
                        return;
                    }

                    player.sendMessage(KatsuUtils.coloredHex(plugin.getPrefix() + messages.getString("arena-game-tip").replaceAll("%tip%", gameWord.getRandomTip())));
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 10, 1);
                }
            }
        }
    }
}
