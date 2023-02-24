package io.github.maazapan.kthangman.game.arenas;

import io.github.maazapan.kthangman.game.Arena;
import io.github.maazapan.kthangman.game.player.GamePlayer;
import io.github.maazapan.kthangman.game.type.ArenaType;

public class SingleArena extends Arena {

    private GamePlayer gamePlayer;

    public SingleArena(String name) {
        super(ArenaType.SINGLE, name);
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

    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }

    public void setGamePlayer(GamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }
}
