package io.github.maazapan.kthangman.game.player;

import de.tr7zw.changeme.nbtapi.NBTItem;
import io.github.maazapan.kthangman.KTHangman;
import io.github.maazapan.kthangman.game.Arena;
import io.github.maazapan.kthangman.game.discover.DiscoverLetter;
import io.github.maazapan.kthangman.game.manager.ArenaManager;
import io.github.maazapan.kthangman.game.manager.hangman.HangAnimation;
import io.github.maazapan.kthangman.game.manager.hangman.Hangman;
import io.github.maazapan.kthangman.game.manager.scoreboard.FastManager;
import io.github.maazapan.kthangman.game.state.ArenaState;
import io.github.maazapan.kthangman.game.word.GameWord;
import io.github.maazapan.kthangman.utils.ItemBuilder;
import io.github.maazapan.kthangman.utils.KatsuUtils;
import io.github.maazapan.kthangman.utils.task.KatsuTask;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class GameArena extends Arena {

    private final KTHangman plugin;
    private final FileConfiguration messages;

    private final ArenaManager arenaManager;
    private final DiscoverLetter discoverLetter;

    private final GameWord gameWord;

    public GameArena(Arena arena, KTHangman plugin) {
        super(arena);
        this.plugin = plugin;
        this.discoverLetter = new DiscoverLetter();
        this.arenaManager = plugin.getArenaManager();
        this.gameWord = getGameWord();
        this.messages = plugin
                .getLoaderManager()
                .getFileManager()
                .getMessages();
    }

    /**
     * Start the game arena.
     */
    public void startGame() {
        List<Player> arenaPlayers = getGamePlayers().stream()
                .map(GamePlayer::getUUID).filter(uuid -> Bukkit.getPlayer(uuid) != null)
                .map(Bukkit::getPlayer).collect(Collectors.toList());

        // Select a random word from the list.
        gameWord.selectRandomWord();

        this.setUsed(true);
        this.setState(ArenaState.PLAYING);
        this.setCurrentTime(System.currentTimeMillis() + (getTime() * 1000L));

        System.out.println("Word: " + gameWord.getWord());

        for (Player player : arenaPlayers) {
            // Set player scoreboard.
            FastManager fastManager = new FastManager(plugin);
            fastManager.createPlayingScoreboard(this, player);

            this.messageStartArena(player);
            this.addArenaItems(player);
        }
    }


    /**
     * Terminate the game.
     */
    public void terminateGame() {
        this.setState(ArenaState.WAITING);
        this.setUsed(false);
        this.setTime(0);
        this.setCurrentLives(getLives());

        // Remove all players from the game.
    }

    /**
     * Update the game every 10 ticks.
     */
    public void updateGame() {
        FileConfiguration config = plugin.getConfig();

        for (GamePlayer gamePlayer : getGamePlayers()) {
            Player player = Bukkit.getPlayer(gamePlayer.getUUID());

            if (getState() == ArenaState.PLAYING) {
                // Check time of the game, if the time is over, the game is over.
                if (System.currentTimeMillis() > getCurrentTime()) {
                    this.gameOver();
                    return;
                }

                // Send actionbar with word at player.
                String message = KatsuUtils.coloredHex(messages.getString("action-bar.game-word")
                        .replaceAll("%formatted_word%", KatsuUtils.formatDisplayWord(gameWord.getFormattedWord())));
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));

                // Update player scoreboard
                if (plugin.getScoreboardMap().containsKey(player.getUniqueId())) {
                    FastManager fastManager = new FastManager(plugin);
                    fastManager.updatePlayingScoreboard(this, player);
                }

                // Display a random word
                if (config.getBoolean("config.display-random-words")) {
                    if (new Random().nextInt(100) <= 10) {
                        //     hangman.displayRandomWord("holograms.random-words");
                    }
                }
            }
        }
    }


    /**
     * GameOver que.
     */
    public void gameOver() {
        List<Player> arenaPlayers = getGamePlayers().stream()
                .map(GamePlayer::getUUID).filter(uuid -> Bukkit.getPlayer(uuid) != null)
                .map(Bukkit::getPlayer).collect(Collectors.toList());

        this.setState(ArenaState.ENDING);

        for (Player player : arenaPlayers) {
            this.playSoundGameOver(player);

            // Send game over message at player.
            List<String> message = messages.getStringList("game-over");
            message.replaceAll(s -> s.replaceAll("%word%", KatsuUtils.formatDisplayWord(gameWord.getWord()))
                    .replaceAll("%formatted_word%", KatsuUtils.formatDisplayWord(gameWord.getFormattedWord())));

            message.forEach(s -> player.sendMessage(KatsuUtils.coloredHex(s)));

            String[] titles = KatsuUtils.coloredHex(messages.getString("titles.game-over")).split(";");
            player.sendTitle(KatsuUtils.coloredHex(titles[0].replaceAll("%word%", KatsuUtils.formatDisplayWord(gameWord.getWord()))),
                    KatsuUtils.coloredHex(titles[1].replaceAll("%word%", KatsuUtils.formatDisplayWord(gameWord.getFormattedWord()))), 15, 50, 15);

            // Terminate the game past 10 seconds.
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> arenaManager.leaveArena(this, player), 200);
            player.sendMessage(KatsuUtils.coloredHex(plugin.getPrefix() + messages.getString("arena-game-over-leave")));
        }
    }

    /**
     *
     */
    public void gameWin() {
        FileConfiguration config = plugin.getConfig();
        HangAnimation animation = new HangAnimation(plugin);

        List<Player> arenaPlayers = getGamePlayers().stream()
                .map(GamePlayer::getUUID).filter(uuid -> Bukkit.getPlayer(uuid) != null)
                .map(Bukkit::getPlayer).collect(Collectors.toList());

        this.setState(ArenaState.ENDING);

        //  animation.winAnimation(hangman);

        for (Player player : arenaPlayers) {

            // Send win message at player.
            long time = getTime() - ((getCurrentTime() - System.currentTimeMillis()) / 1000L);

            List<String> winMessages = messages.getStringList("game-win");
            winMessages.replaceAll(s -> s.replaceAll("%word%", KatsuUtils.formatDisplayWord(gameWord.getWord()))
                    .replaceAll("%lives%", String.valueOf(getCurrentLives()))
                    .replaceAll("%time%", String.valueOf(time)));

            winMessages.forEach(s -> player.sendMessage(KatsuUtils.coloredHex(s)));
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);

            String[] titles = KatsuUtils.coloredHex(messages.getString("titles.game-win")).split(";");
            player.sendTitle(KatsuUtils.coloredHex(titles[0].replaceAll("%word%", KatsuUtils.formatDisplayWord(gameWord.getWord())).replaceAll("%time%", String.valueOf(time))),
                    KatsuUtils.coloredHex(titles[1].replaceAll("%word%", KatsuUtils.formatDisplayWord(gameWord.getWord())).replaceAll("%time%", String.valueOf(time))), 15, 60, 15);

            // Terminate the game past 10 seconds.
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> arenaManager.leaveArena(this, player), 200);
            player.sendMessage(KatsuUtils.coloredHex(plugin.getPrefix() + messages.getString("arena-game-win-leave")));
        }
    }


    /**
     * Check if the word is correct.
     *
     * @param writeWord String at PlayerChatEvent
     */
    public void checkWord(String writeWord) {
        List<Player> arenaPlayers = getGamePlayers().stream()
                .map(GamePlayer::getUUID).filter(uuid -> Bukkit.getPlayer(uuid) != null)
                .map(Bukkit::getPlayer).collect(Collectors.toList());

        for (Player player : arenaPlayers) {
            /*
             - Check if the word is correct.
             */
            if (writeWord.equalsIgnoreCase(gameWord.getWord())) {
                this.gameWin();
                return;
            }

            /*
             - Check if the word contains the letter.
             */
            if ((gameWord.getWord().contains(writeWord))) {
                if (writeWord.length() > 1) {
                    this.decreaseLives();
                    return;
                }
                char letter = writeWord.charAt(0);

                // Check if the letter is already discovered.
                if (discoverLetter.getCharDiscover().contains(letter)) {
                    player.sendMessage(KatsuUtils.coloredHex(plugin.getPrefix() + messages.getString("arena-letter-discovered").replaceAll("%letter%", String.valueOf(letter))));
                    return;
                }

                String discoverWord = discoverLetter.discover(gameWord.getWord(), gameWord.getFormattedWord(), letter);
                gameWord.setFormattedWord(discoverWord);

                // Check if the word is discovered.
                if (discoverLetter.getCharDiscover().size() == gameWord.getWord().length()) {
                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, this::gameWin, 2L);
                    return;
                }

                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 10, 2);
                player.sendTitle(KatsuUtils.coloredHex("&a" + letter + "".toUpperCase()), "", 10, 40, 10);
                return;
            }

            // Decrease the lives of the player.
            this.decreaseLives();
        }
    }

    /**
     * Decrease the lives of the player.
     */
    private void decreaseLives() {
        List<Player> arenaPlayers = getGamePlayers().stream()
                .map(GamePlayer::getUUID).filter(uuid -> Bukkit.getPlayer(uuid) != null)
                .map(Bukkit::getPlayer).collect(Collectors.toList());

        int lives = this.getCurrentLives() - 1;

        // Check if the player has no lives.
        if (lives <= 0) {
            this.gameOver();
            return;
        }

        for (Player player : arenaPlayers) {

            // discover a letter if the player has only one life.
            if (lives == 1) {
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 10, 2);
                gameWord.setFormattedWord(discoverLetter.discoverRandom(gameWord.getWord(), gameWord.getFormattedWord()));
            }

            player.playSound(player.getLocation(), Sound.ENTITY_IRON_GOLEM_DAMAGE, 1, 1);
            player.sendMessage(KatsuUtils.coloredHex(plugin.getPrefix() + messages.getString("arena-lives-left").replace("%lives%", String.valueOf(lives))));

            double damage = (player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() / this.getLives());
            Bukkit.getScheduler().runTask(plugin, () -> player.damage(damage));
        }
        this.setCurrentLives(lives);
    }


    /**
     * Play sound game over.
     *
     * @param player Player to play the sound.
     */
    private void playSoundGameOver(Player player) {
        new KatsuTask(plugin, 7) {
            private float i = 0.7f;

            @Override
            public void run() {
                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10, i);
                i -= 0.1f;
            }
        }.countDownTask(2);
    }

    /**
     * Play sound game start.
     *
     * @param player Player to play the sound.
     */
    private void playSoundStart(Player player) {
        new KatsuTask(plugin, 10) {
            private float i = 0.7f;

            @Override
            public void run() {
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 10, i);
                i += 0.1f;
            }
        }.countDownTask(2);
    }

    /**
     * Create all items for the arena.
     * and add that items at player inventory.
     *
     * @param player GamePlayer
     */
    private void addArenaItems(Player player) {
        // Create the hint item-stack.
        ItemStack hintItem = new ItemBuilder().fromConfig(messages, "arena-items.hint-item").build();
        int slot = messages.getInt("arena-items.hint-item.slot");

        NBTItem nbtHint = new NBTItem(hintItem);
        nbtHint.setBoolean("kthangman-item-tips", false);
        nbtHint.applyNBT(hintItem);

        player.getInventory().setItem(slot, hintItem);
    }

    public void messageStartArena(Player player) {
        List<String> message = messages.getStringList("game-start");
        message.forEach(s -> player.sendMessage(KatsuUtils.coloredHex(s.replaceAll("%formatted_word%", gameWord.getFormattedWord()))));

        playSoundStart(player);
        player.teleport(getSpawn());

        // Send title to player.
        String[] titles = KatsuUtils.coloredHex(messages.getString("titles.game-start")).split(";");
        player.sendTitle(KatsuUtils.coloredHex(titles[0].replaceAll("%formatted_word%", gameWord.getFormattedWord())),
                KatsuUtils.coloredHex(titles[1].replaceAll("%formatted_word%", gameWord.getFormattedWord())), 15, 50, 15);
    }
}
