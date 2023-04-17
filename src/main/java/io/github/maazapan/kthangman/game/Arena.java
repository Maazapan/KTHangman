package io.github.maazapan.kthangman.game;

import io.github.maazapan.kthangman.game.player.GamePlayer;
import io.github.maazapan.kthangman.game.state.ArenaState;
import io.github.maazapan.kthangman.game.word.GameWord;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Arena {

    private final String name;
    private ArenaState state;

    private Location spawn;
    private Location hang;

    private boolean enabled;
    private boolean used;

    private int time;
    private long currentTime;

    private int lives;
    private int currentLives;

    private final List<GamePlayer> gamePlayers;
    private final GameWord gameWord;

    public Arena(String name) {
        this.gamePlayers = new ArrayList<>();
        this.gameWord = new GameWord();
        this.state = ArenaState.WAITING;
        this.lives = 5;
        this.currentLives = lives;
        this.name = name;
        this.time = 120;
        this.enabled = false;
        this.used = false;
    }

    public Arena(Arena arena) {
        this.gameWord = arena.getGameWord();
        this.time = arena.getTime();
        this.spawn = arena.getSpawn();
        this.hang = arena.getHang();
        this.gamePlayers = arena.getGamePlayers();
        this.currentLives = arena.getCurrentLives();
        this.state = arena.getState();
        this.lives = arena.getLives();
        this.name = arena.getName();
        this.enabled = arena.isEnabled();
        this.used = arena.isUsed();
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

    public int getLives() {
        return lives;
    }

    public Location getHang() {
        return hang;
    }

    public Location getSpawn() {
        return spawn;
    }

    public int getTime() {
        return time;
    }

    public void setSpawn(Location spawn) {
        this.spawn = spawn;
    }

    public void setTime(int time) {
        this.time = time;
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

    public List<GamePlayer> getGamePlayers() {
        return gamePlayers;
    }

    public void setLives(int lives) {
        this.lives = lives;
    }

    public ArenaState getState() {
        return state;
    }

    public int getCurrentLives() {
        return currentLives;
    }

    public void setCurrentLives(int currentLives) {
        this.currentLives = currentLives;
    }

    public void setState(ArenaState state) {
        this.state = state;
    }

    public long getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(long currentTime) {
        this.currentTime = currentTime;
    }

    public GameWord getGameWord() {
        return gameWord;
    }
}
