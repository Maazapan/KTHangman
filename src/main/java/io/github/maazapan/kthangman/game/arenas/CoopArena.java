package io.github.maazapan.kthangman.game.arenas;

import io.github.maazapan.kthangman.game.Arena;
import io.github.maazapan.kthangman.game.player.GamePlayer;
import io.github.maazapan.kthangman.game.type.ArenaType;

import java.util.ArrayList;
import java.util.List;

public class CoopArena extends Arena {

    private final List<GamePlayer> gamePlayers;

    public CoopArena(String name) {
        super(ArenaType.COOP, name);
        this.gamePlayers = new ArrayList<>();
    }

    @Override
    public void update() {

    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void finish() {

    }

    public List<GamePlayer> getGamePlayers() {
        return gamePlayers;
    }
}
