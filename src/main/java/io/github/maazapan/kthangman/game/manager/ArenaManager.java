package io.github.maazapan.kthangman.game.manager;

import io.github.maazapan.kthangman.KTHangman;
import io.github.maazapan.kthangman.game.Arena;
import io.github.maazapan.kthangman.game.countdown.StartCountdown;
import io.github.maazapan.kthangman.game.manager.scoreboard.FastBoard;
import io.github.maazapan.kthangman.game.player.GameArena;
import io.github.maazapan.kthangman.game.player.GamePlayer;
import io.github.maazapan.kthangman.game.state.ArenaState;
import io.github.maazapan.kthangman.utils.KatsuUtils;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ArenaManager {

    private final List<Arena> arenas;
    private final List<GameArena> playingArenas;

    private final KTHangman plugin;

    public ArenaManager(KTHangman plugin) {
        this.playingArenas = new ArrayList<>();
        this.arenas = new ArrayList<>();
        this.plugin = plugin;
    }

    /**
     * Join player at arena.
     *
     * @param arena  Arena to play in
     * @param player Player to add to the arena
     */
    public void joinArena(Arena arena, Player player) {
        FileConfiguration messages = plugin.getLoaderManager().getFileManager().getMessages();
        GamePlayer gamePlayer = new GamePlayer(player);

        player.getInventory().clear();
        player.getInventory().setArmorContents(null);

        player.setFoodLevel(20);
        player.setExp(0.0f);
        player.setGameMode(GameMode.ADVENTURE);

        player.teleport(arena.getSpawn());
        player.sendMessage(KatsuUtils.coloredHex(messages.getString("join-arena").replaceAll("%arena_name%", arena.getName())));

        arena.setState(ArenaState.STARTING);
        arena.getGamePlayers().add(gamePlayer);
        GameArena gameArena = new GameArena(arena, plugin);

        playingArenas.add(gameArena);
        player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());

        // Start arena countdown.
        new StartCountdown(plugin, gameArena).runTaskTimer(plugin, 0, 20);
    }


    /**
     * Remove player at arena.
     *
     * @param arena  Arena to leave
     * @param player Player to remove from the arena
     */
    public void leaveArena(GameArena arena, Player player) {
        FileConfiguration messages = plugin.getLoaderManager().getFileManager().getMessages();
        GamePlayer gamePlayer = getGamePlayer(arena, player.getUniqueId());

        // Restore player inventory and stats.
        if (gamePlayer != null) {
            player.getInventory().setContents(gamePlayer.getContents());
            player.getInventory().setArmorContents(gamePlayer.getArmorContents());

            player.setFoodLevel(gamePlayer.getFood());
            player.setExp(gamePlayer.getXp());
            player.setHealth(gamePlayer.getHealth());
            player.setGameMode(gamePlayer.getGameMode());
        }

        // Teleport player at lobby
        if (getLobby() != null) {
            player.teleport(getLobby());
        }

        // If the player is has scoreboard, remove it.
        if (plugin.getScoreboardMap().containsKey(player.getUniqueId())) {
            FastBoard fastBoard = plugin.getScoreboardMap().get(player.getUniqueId());
            fastBoard.delete();
            plugin.getScoreboardMap().remove(player.getUniqueId());
        }

        arena.getGamePlayers().remove(gamePlayer);

        // If the arena is empty, terminate the game.
        if (arena.getGamePlayers().isEmpty()) {
            playingArenas.remove(arena);
            arena.terminateGame();
        }

        player.sendMessage(KatsuUtils.coloredHex(plugin.getPrefix() + messages.getString("arena-leave").replaceAll("%arena_name%", arena.getName())));
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(" "));
    }

    /**
     * Get main lobby location maybe null.
     *
     * @return Location of lobby
     */
    @Nullable
    public Location getLobby() {
        FileConfiguration config = plugin.getLoaderManager().getFileManager().getLobby();
        if (!config.contains("lobby.world")) return null;
        World world = plugin.getServer().getWorld(config.getString("lobby.world"));

        double x = config.getDouble("lobby.x");
        double y = config.getDouble("lobby.y");
        double z = config.getDouble("lobby.z");

        float yaw = (float) config.getDouble("lobby.yaw");
        float pitch = (float) config.getDouble("lobby.pitch");

        return new Location(world, x, y, z, yaw, pitch);
    }

    /**
     * Set main lobby at lobby config.
     *
     * @param location Current Location
     */
    public void setLobby(Location location) {
        FileConfiguration config = plugin.getLoaderManager().getFileManager().getLobby();

        config.set("lobby.world", location.getWorld().getName());
        config.set("lobby.x", location.getX());
        config.set("lobby.y", location.getY());
        config.set("lobby.z", location.getZ());

        config.set("lobby.yaw", location.getYaw());
        config.set("lobby.pitch", location.getPitch());

        plugin.getLoaderManager().getFileManager().getLobby().save();
    }

    /**
     * Get arena by player, need check if the player is in arena.
     *
     * @param uuid Player UUID
     * @return Arena where the player is
     */
    public GameArena getArenaByPlayer(UUID uuid) {
        for (GameArena arena : playingArenas) {
            if (arena.getGamePlayers().stream().map(GamePlayer::getUUID).anyMatch(uuid::equals)) {
                return arena;
            }
        }
        return null;
    }

    /**
     * Check player is playing in any arena.
     *
     * @param uuid Player UUID
     * @return Boolean
     */
    public boolean isPlayingPlayer(UUID uuid) {
        for (Arena arena : playingArenas) {
            return arena.getGamePlayers().stream().map(GamePlayer::getUUID).anyMatch(uuid::equals);
        }
        return false;
    }

    /**
     * Make sure to check if the arena is not playing.
     *
     * @return List of all arenas.
     */
    public List<Arena> getCreatedArenas() {
        return arenas;
    }

    /**
     * Get all arenas that are playing.
     *
     * @return List of playing arenas.
     */
    public List<GameArena> getPlayingArenas() {
        return playingArenas;
    }

    public GamePlayer getGamePlayer(Arena arena, UUID uuid) {
        return arena.getGamePlayers().stream().filter(gamePlayer -> gamePlayer.getUUID().equals(uuid)).findFirst().orElse(null);
    }

    public Arena getArena(String name) {
        return arenas.stream().filter(arena -> arena.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public void removeArena(String name) {
        arenas.removeIf(arena -> arena.getName().equalsIgnoreCase(name));
    }

    public void removeArena(Arena arena) {
        arenas.remove(arena);
    }

    public boolean existArena(String name) {
        return arenas.stream().anyMatch(arena -> arena.getName().equalsIgnoreCase(name));
    }
}
