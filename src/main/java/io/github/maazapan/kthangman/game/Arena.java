package io.github.maazapan.kthangman.game;

import io.github.maazapan.kthangman.game.player.GamePlayer;
import io.github.maazapan.kthangman.game.state.ArenaState;
import io.github.maazapan.kthangman.game.type.ArenaType;
import org.bukkit.Location;
import org.bukkit.event.player.AsyncPlayerChatEvent;

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

    private long time;

    private int lives;
    private int currentLives;

    private final List<GamePlayer> gamePlayers;
    private List<String> words;

    private String word;
    private String formatWord;

    public Arena(String name) {
        this.gamePlayers = new ArrayList<>();
        this.state = ArenaState.WAITING;
        this.lives = 5;
        this.currentLives = lives;
        this.name = name;
        this.enabled = false;
        this.used = false;
        this.words = Arrays.asList(
                "HOUSE", "CAT", "MINECRAFT", "DOG",
                "BANANA", "APPLE", "ORANGE",
                "TECHNOLOGY", "COMPUTER", "MUSIC");
    }

    public Arena(Arena arena) {
        this.spawn = arena.getSpawn();
        this.hang = arena.getHang();
        this.gamePlayers = arena.getGamePlayers();
        this.currentLives = arena.getCurrentLives();
        this.state = arena.getState();
        this.lives = arena.getLives();
        this.name = arena.getName();
        this.enabled = arena.isEnabled();
        this.used = arena.isUsed();
        this.words = arena.getWords();
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

    public long getTime() {
        return time;
    }

    public void setSpawn(Location spawn) {
        this.spawn = spawn;
    }

    public void setTime(long time) {
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

    public List<String> getWords() {
        return words;
    }

    public String getWord() {
        return word;
    }

    public String getFormatWord() {
        return formatWord;
    }

    public void setLives(int lives) {
        this.lives = lives;
    }

    public void setFormatWord(String formatWord) {
        this.formatWord = formatWord;
    }

    public void setWord(String word) {
        this.word = word;
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

    public void setWords(List<String> words) {
        this.words = words;
    }
}
