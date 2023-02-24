package io.github.maazapan.kthangman.commands;

import io.github.maazapan.kthangman.KTHangman;
import io.github.maazapan.kthangman.game.Arena;
import io.github.maazapan.kthangman.game.arenas.SingleArena;
import io.github.maazapan.kthangman.game.manager.ArenaManager;
import io.github.maazapan.kthangman.game.type.ArenaType;
import io.github.maazapan.kthangman.utils.KatsuUtils;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
                            if (!(args.length > 2)) {
                                player.hasPermission(KatsuUtils.coloredHex(plugin.getPrefix() + "&fPlease complete command &7/hm create <name> <type>."));
                                return true;
                            }

                            if (Arrays.stream(ArenaType.values()).anyMatch(s -> !s.toString().equalsIgnoreCase(args[2]))) {
                                player.sendMessage(KatsuUtils.coloredHex(plugin.getPrefix() + "&fArena type is invalid please use &7SINGLE"));
                                return true;
                            }
                            ArenaType type = ArenaType.valueOf(args[2].toUpperCase());
                            String name = args[1];

                            if (arenaManager.existArena(name)) {
                                player.sendMessage(KatsuUtils.coloredHex(plugin.getPrefix() + "&fArena with name &7" + name + " &falready exist."));
                                return true;
                            }
                            arenaManager.getCreatedArenas().add(new SingleArena(name));

                            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 1);
                            player.sendMessage(KatsuUtils.coloredHex(plugin.getPrefix() + "&fArena &7" + name + " &fhas been created."));
                        }
                        break;

                    /*
                     - Delete custom arena
                     + Command: /hm delete <name>
                     */
                    case "delete":
                        if (player.hasPermission("hangman.cmd.delete")) {
                            if (!(args.length > 1)) {
                                player.sendMessage(KatsuUtils.coloredHex(plugin.getPrefix() + "&fPlease complete command &7/hm delete <name>."));
                                return true;
                            }
                            String name = args[1];

                            if (!arenaManager.existArena(name)) {
                                player.sendMessage(KatsuUtils.coloredHex(plugin.getPrefix() + "&fArena with name &7" + name + " &fdoes not exist."));
                                return true;
                            }
                            Arena arena = arenaManager.getArena(name);

                            if (arena.isUsed()) {
                                player.sendMessage(KatsuUtils.coloredHex(plugin.getPrefix() + "&fArena with name &7" + name + " &fis currently in use."));
                                return true;
                            }
                            arenaManager.removeArena(arena);

                            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 1);
                            player.sendMessage(KatsuUtils.coloredHex(plugin.getPrefix() + "&fArena &7" + name + " &fhas been deleted."));
                        }
                        break;
                    /*
                     - Join custom arena
                     + Command: /hm arena <name> <spawn/hangman/enable>
                     */
                    case "arena":
                        if (player.hasPermission("hangman.cmd.arena")) {
                            if (!(args.length > 2)) {
                                player.sendMessage(KatsuUtils.coloredHex(plugin.getPrefix() + "&fPlease complete command &7/hm arena <name> <spawn/hangman/enable>."));
                                return true;
                            }
                            String name = args[1];

                            if (!arenaManager.existArena(name)) {
                                player.sendMessage(KatsuUtils.coloredHex(plugin.getPrefix() + "&fArena with name &7" + name + " &fdoes not exist."));
                                return true;
                            }
                            Arena arena = arenaManager.getArena(name);

                            switch (args[1].toLowerCase()) {
                                // Set arena spawn at player location.
                                case "spawn":
                                    arena.setSpawn(player.getLocation());

                                    player.playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, 1, 2);
                                    player.sendMessage(KatsuUtils.coloredHex(plugin.getPrefix() + "&fSpawn has been set to your location."));
                                    break;

                                // Set arena hangman at player location.
                                case "hangman":
                                    arena.setHang(player.getLocation());

                                    player.playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, 1, 2);
                                    player.sendMessage(KatsuUtils.coloredHex(plugin.getPrefix() + "&fHangman has been set to your location."));
                                    break;

                                // Enable arena.
                                case "enable":
                                    if (arena.getHang() == null) {
                                        player.sendMessage(KatsuUtils.coloredHex(plugin.getPrefix() + "&fArena with name &7" + name + " &fdoes not have a hangman location use &7/hm arena hangman."));
                                        return true;
                                    }

                                    if (arena.getSpawn() == null) {
                                        player.sendMessage(KatsuUtils.coloredHex(plugin.getPrefix() + "&fArena with name &7" + name + " &fdoes not have a spawn location use &7/hm arena spawn."));
                                        return true;
                                    }
                                    arena.setEnabled(true);

                                    player.playSound(player.getLocation(), Sound.BLOCK_LEVER_CLICK, 1, 2);
                                    player.sendMessage(KatsuUtils.coloredHex(plugin.getPrefix() + "&fArena has been enabled."));
                                    break;

                                default:
                                    player.sendMessage(KatsuUtils.coloredHex(plugin.getPrefix() + "&fPlease use correct command &7/hm arena <name> <spawn/hangman/enable>."));
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
                                player.sendMessage(KatsuUtils.coloredHex(plugin.getPrefix() + "&cThere are no arenas available."));
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
                                player.sendMessage(KatsuUtils.coloredHex(plugin.getPrefix() + "&fPlease complete command &7/hm join <name>."));
                                return true;
                            }
                            String name = args[1];

                            if (!arenaManager.existArena(name)) {
                                player.sendMessage(KatsuUtils.coloredHex(plugin.getPrefix() + "&fArena with name &7" + name + " &fdoes not exist."));
                                return true;
                            }
                            Arena arena = arenaManager.getArena(name);
                            arenaManager.joinArena(arena, player);
                        }
                        break;


                    /*
                     - Leave a Custom arena
                     + Command: /hm leave
                     */
                    case "leave":
                        if (player.hasPermission("hangman.cmd.leave")) {
                            if (!arenaManager.isPlayingPlayer(player.getName())) {
                                player.sendMessage(KatsuUtils.coloredHex(plugin.getPrefix() + "&fYou are not in any arena."));
                                return true;
                            }


                        }
                        break;


                    default:
                        break;
                }
            } else {
                player.sendMessage(KatsuUtils.coloredHex(plugin.getPrefix() + "&fPlease use &7/hm help &ffor more information about plugin."));
            }
        }
        return false;
    }
}
