package io.github.maazapan.kthangman.game.manager;

import io.github.maazapan.kthangman.KTHangman;
import io.github.maazapan.kthangman.game.Arena;
import io.github.maazapan.kthangman.game.arenas.CoopArena;
import io.github.maazapan.kthangman.game.arenas.SingleArena;
import io.github.maazapan.kthangman.game.player.GamePlayer;
import io.github.maazapan.kthangman.game.type.ArenaType;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ArenaManager {

    private final List<Arena> arenas;

    private KTHangman plugin;

    public ArenaManager(KTHangman plugin) {
        this.arenas = new ArrayList<>();
        this.plugin = plugin;
    }

    public void joinArena(Arena arena, Player player) {

    }

    public void leaveArena() {

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

    public Arena getArenaByPlayer(String name) {
        for (Arena arena : arenas) {
            if (arena.getType() == ArenaType.SINGLE) {
                SingleArena singleArena = (SingleArena) arena;

                if (singleArena.getGamePlayer().getName().equalsIgnoreCase(name)) {
                    return singleArena;
                }
            }


        }
        return null;
    }

    /**
     * Check player is playing in any arena.
     *
     * @param name Player name
     * @return Boolean
     */
    public boolean isPlayingPlayer(String name) {
        for (Arena arena : arenas) {
            if (arena.getType() == ArenaType.SINGLE) {
                return ((SingleArena) arena).getGamePlayer().getName().equalsIgnoreCase(name);
            }

            if (arena.getType() == ArenaType.COOP) {
                return ((CoopArena) arena).getGamePlayers().stream().map(GamePlayer::getName).anyMatch(name::equalsIgnoreCase);
            }
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
}
