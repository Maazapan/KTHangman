package io.github.maazapan.kthangman.commands;

import io.github.maazapan.kthangman.KTHangman;
import io.github.maazapan.kthangman.game.Arena;
import io.github.maazapan.kthangman.game.manager.ArenaManager;
import io.github.maazapan.kthangman.game.player.GameArena;
import io.github.maazapan.kthangman.utils.KatsuUtils;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ArenaCommand implements CommandExecutor {

    private final KTHangman plugin;

    public ArenaCommand(KTHangman plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            FileConfiguration messages = plugin.getLoaderManager().getFileManager().getMessages();

            ArenaManager arenaManager = plugin.getArenaManager();
            Player player = (Player) sender;

            if (args.length > 0) {
                switch (args[0].toLowerCase()) {
                    /*
                     - Create Custom arena.
                     + Command: /hm create <name> <type>
                     */
                    case "create":
                        if (player.hasPermission("hangman.cmd.create")) {
                            if (!(args.length > 1)) {
                                player.sendMessage(KatsuUtils.coloredHex(plugin.getPrefix() + "&fPlease complete command &e/hm create <name>"));
                                return true;
                            }
                            String name = args[1];

                            if (arenaManager.existArena(name)) {
                                player.sendMessage(KatsuUtils.coloredHex(plugin.getPrefix() + messages.getString("arena-already-exist").replaceAll("%arena_name%", name)));
                                return true;
                            }
                            arenaManager.getCreatedArenas().add(new Arena(name));

                            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 10, 1);
                            player.sendMessage(KatsuUtils.coloredHex(plugin.getPrefix() + messages.getString("arena-created").replaceAll("%arena_name%", name)));
                        }
                        break;
                    /*
                     - Delete custom arena
                     + Command: /hm delete <name>
                     */
                    case "delete":
                        if (player.hasPermission("hangman.cmd.delete")) {
                            if (!(args.length > 1)) {
                                player.sendMessage(KatsuUtils.coloredHex(plugin.getPrefix() + "&fPlease complete command &e/hm delete <name>."));
                                return true;
                            }
                            String name = args[1];

                            if (!arenaManager.existArena(name)) {
                                player.sendMessage(KatsuUtils.coloredHex(plugin.getPrefix() + messages.getString("arena-non-exist").replaceAll("%arena_name%", name)));
                                return true;
                            }
                            Arena arena = arenaManager.getArena(name);

                            if (arena.isUsed()) {
                                player.sendMessage(KatsuUtils.coloredHex(plugin.getPrefix() + messages.getString("arena-in-use").replaceAll("%arena_name%", name)));
                                return true;
                            }
                            arenaManager.removeArena(arena);

                            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                            player.sendMessage(KatsuUtils.coloredHex(plugin.getPrefix() + messages.getString("arena-delete").replaceAll("%arena_name%", name)));
                        }
                        break;
                    /*
                     - Join custom arena
                     + Command: /hm arena <spawn/hangman/enable> <name>
                     */
                    case "arena":
                        if (player.hasPermission("hangman.cmd.arena")) {
                            if (!(args.length > 1)) {
                                player.sendMessage(KatsuUtils.coloredHex(plugin.getPrefix() + "&fPlease complete command &e/hm arena <setspawn/sethangman/enable/disable> <name>."));
                                return true;
                            }
                            String name = args[2];

                            if (!arenaManager.existArena(name)) {
                                player.sendMessage(KatsuUtils.coloredHex(plugin.getPrefix() + messages.getString("arena-non-exist").replaceAll("%arena_name%", name)));
                                return true;
                            }
                            Arena arena = arenaManager.getArena(name);

                            switch (args[1].toLowerCase()) {
                                // Set arena spawn at player location.
                                case "setspawn":
                                case "spawn":
                                    arena.setSpawn(player.getLocation());

                                    player.playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, 1, 2);
                                    player.sendMessage(KatsuUtils.coloredHex(plugin.getPrefix() + messages.getString("arena-set-spawn").replaceAll("%arena_name%", name)));
                                    break;

                                // Set arena hangman at player location.
                                case "sethangman":
                                case "hangman":
                                    arena.setHang(player.getLocation());

                                    player.playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, 1, 2);
                                    player.sendMessage(KatsuUtils.coloredHex(plugin.getPrefix() + messages.getString("arena-set-hangman").replaceAll("%arena_name%", name)));
                                    break;

                                // Enable arena.
                                case "enable":
                                    if (arena.isEnabled()) {
                                        player.sendMessage(KatsuUtils.coloredHex(plugin.getPrefix() + messages.getString("arena-already-enabled").replaceAll("%arena_name%", name)));
                                        return true;
                                    }
                                    if (arena.getHang() == null) {
                                        player.sendMessage(KatsuUtils.coloredHex(plugin.getPrefix() + messages.getString("arena-hangman-error").replaceAll("%arena_name%", name)));
                                        return true;
                                    }
                                    if (arena.getSpawn() == null) {
                                        player.sendMessage(KatsuUtils.coloredHex(plugin.getPrefix() + messages.getString("arena-spawn-error").replaceAll("%arena_name%", name)));
                                        return true;
                                    }

                                    if (arenaManager.getLobby() == null) {
                                        player.sendMessage(KatsuUtils.coloredHex(plugin.getPrefix() + messages.getString("lobby-not-set")));
                                        return true;
                                    }
                                    arena.setEnabled(true);

                                    player.playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, 1, 2);
                                    player.sendMessage(KatsuUtils.coloredHex(plugin.getPrefix() + messages.getString("arena-enabled").replaceAll("%arena_name%", name)));
                                    break;

                                // Disable arena.
                                case "disable":
                                    if (!arena.isEnabled()) {
                                        player.sendMessage(KatsuUtils.coloredHex(plugin.getPrefix() + messages.getString("arena-already-disabled").replaceAll("%arena_name%", name)));
                                        return true;
                                    }

                                    arena.setEnabled(false);
                                    player.playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, 1, 2);
                                    player.sendMessage(KatsuUtils.coloredHex(plugin.getPrefix() + messages.getString("arena-disabled").replaceAll("%arena_name%", name)));
                                    break;

                                default:
                                    player.sendMessage(KatsuUtils.coloredHex(plugin.getPrefix() + "&fPlease complete command &e/hm arena <setspawn/sethangman/enable/disable> <name>."));
                                    break;
                            }
                        }
                        break;
                    /*
                     - Show available arenas.
                     + Command: /hm list
                     */
                    case "list":
                        if (player.hasPermission("hangman.cmd.list")) {
                            if (arenaManager.getCreatedArenas().isEmpty()) {
                                player.sendMessage(KatsuUtils.coloredHex(plugin.getPrefix() + messages.getString("arena-not-available")));
                                return true;
                            }
                            List<String> availableArenas = arenaManager.getCreatedArenas().stream()
                                    .map(Arena::getName)
                                    .collect(Collectors.toList());

                            player.sendMessage(KatsuUtils.coloredHex(plugin.getPrefix() + "&fAvailable arenas: &7" + String.join("&8, &7", availableArenas)));
                        }
                        break;
                    /*
                     - Join a Custom arena
                     + Command: /hm join <name>
                     */
                    case "join":
                        if (player.hasPermission("hangman.cmd.join")) {
                            if (!(args.length > 1)) {
                                player.sendMessage(KatsuUtils.coloredHex(plugin.getPrefix() + "&fPlease complete command &b/hm join <name>."));
                                return true;
                            }
                            String name = args[1];

                            if (arenaManager.isPlayingPlayer(player.getUniqueId())) {
                                player.sendMessage(KatsuUtils.coloredHex(plugin.getPrefix() + messages.getString("ready-in-arena").replaceAll("%arena_name%", name)));
                                return true;
                            }
                            if (!arenaManager.existArena(name)) {
                                player.sendMessage(KatsuUtils.coloredHex(plugin.getPrefix() + messages.getString("arena-non-exist").replaceAll("%arena_name%", name)));
                                return true;
                            }
                            Arena arena = arenaManager.getArena(name);

                            if (arena.isUsed()) {
                                player.sendMessage(KatsuUtils.coloredHex(plugin.getPrefix() + messages.getString("arena-in-use").replaceAll("%arena_name%", name)));
                                return true;
                            }
                            arenaManager.joinArena(arena, player);
                        }
                        break;

                    /*
                     - Leave a Custom arena
                     + Command: /hm leave
                     */
                    case "leave":
                        if (player.hasPermission("hangman.cmd.leave")) {
                            if (!arenaManager.isPlayingPlayer(player.getUniqueId())) {
                                player.sendMessage(KatsuUtils.coloredHex(plugin.getPrefix() + messages.getString("no-playing-arena")));
                                return true;
                            }
                            GameArena arena = arenaManager.getArenaByPlayer(player.getUniqueId());
                            arenaManager.leaveArena(arena, player);
                        }
                        break;

                    case "setlobby":
                        if (player.hasPermission("hangman.cmd.lobby")) {

                            player.playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, 1, 2);
                            player.sendMessage(KatsuUtils.coloredHex(plugin.getPrefix() + messages.getString("lobby-set")));
                            arenaManager.setLobby(player.getLocation());
                        }
                        break;

                    case "test":

                        new BukkitRunnable() {
                            private float i = 0.7f;
                            private int time = 0;

                            public void run() {
                                if (time <= 2) {
                                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10, i);

                                } else {
                                    cancel();
                                }
                                i -= 0.1f;
                                time++;
                            }
                        }.runTaskTimer(plugin, 0, 7);
                        break;

                    default:
                        player.sendMessage(KatsuUtils.coloredHex(plugin.getPrefix() + "&cPlease use &7/hm help &cfor more information about plugin."));
                        break;
                }
            } else {
                player.sendMessage(KatsuUtils.coloredHex(plugin.getPrefix() + "&cPlease use &7/hm help &cfor more information about plugin."));
            }
        }
        return false;
    }
}
