package io.github.maazapan.kthangman.game.player;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class GamePlayer {

    private final UUID uuid;

    private final ItemStack[] contents;
    private final ItemStack[] armorContents;

    private final float xp;
    private final int food;

    private final double health;
    private final GameMode gameMode;

    public GamePlayer(Player player) {
        this.armorContents = player.getInventory().getArmorContents();
        this.contents = player.getInventory().getContents();
        this.food = player.getFoodLevel();
        this.uuid = player.getUniqueId();
        this.xp = player.getExp();
        this.health = player.getHealth();
        this.gameMode = player.getGameMode();
    }

    public UUID getUUID() {
        return uuid;
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public ItemStack[] getContents() {
        return contents;
    }

    public ItemStack[] getArmorContents() {
        return armorContents;
    }

    public float getXp() {
        return xp;
    }

    public int getFood() {
        return food;
    }

    public double getHealth() {
        return health;
    }
}
