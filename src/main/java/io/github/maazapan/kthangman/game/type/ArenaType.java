package io.github.maazapan.kthangman.game.type;

import io.github.maazapan.kthangman.game.Arena;
import io.github.maazapan.kthangman.game.arenas.CoopArena;
import io.github.maazapan.kthangman.game.arenas.SingleArena;

public enum ArenaType {

    SINGLE("single_arena", SingleArena.class),
    COOP("coop_arena", CoopArena.class);

    private final Class<? extends Arena> arenaClass;
    private final String name;

    ArenaType(String name, Class<? extends Arena> arenaClass) {
        this.arenaClass = arenaClass;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Class<? extends Arena> getArenaClass() {
        return arenaClass;
    }
}
