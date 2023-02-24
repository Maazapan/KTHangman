package io.github.maazapan.kthangman.game;

import io.github.maazapan.kthangman.game.type.ArenaType;
import org.bukkit.Location;

public abstract class Arena {

    private final String name;
    private final ArenaType type;

    private Location spawn;
    private Location hang;

    private boolean enabled;
    private boolean used;

    public Arena(ArenaType type, String name) {
        this.name = name;
        this.type = type;
        this.enabled = false;
        this.used = false;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isUsed() {
        return used;
    }

    public String getName() {
        return name;
    }

    public Location getHang() {
        return hang;
    }

    public Location getSpawn() {
        return spawn;
    }

    public ArenaType getType() {
        return type;
    }

    public void setSpawn(Location spawn) {
        this.spawn = spawn;
    }

    public void setHang(Location hang) {
        this.hang = hang;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    public abstract void update();
    public abstract void start();
    public abstract void stop();
    public abstract void finish();
}
