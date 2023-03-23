package io.github.maazapan.kthangman.game.player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import io.github.maazapan.kthangman.KTHangman;
import io.github.maazapan.kthangman.game.Arena;
import io.github.maazapan.kthangman.game.discover.DiscoverLetter;
import io.github.maazapan.kthangman.game.manager.ArenaManager;
import io.github.maazapan.kthangman.game.manager.scoreboard.FastManager;
import io.github.maazapan.kthangman.game.state.ArenaState;
import io.github.maazapan.kthangman.utils.KatsuUtils;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.WorldBorder;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class GameArena extends Arena {

    private final KTHangman plugin;
    private final FileConfiguration messages;

    private ArenaManager arenaManager;
    private DiscoverLetter discoverLetter;

    public GameArena(Arena arena, KTHangman plugin) {
        super(arena);
        this.plugin = plugin;
        this.discoverLetter = new DiscoverLetter();
        this.arenaManager = plugin.getArenaManager();
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
        String word = this.getWords().get(new Random().nextInt(getWords().size()));
        String formatWord = KatsuUtils.formatWord(word);

        this.setUsed(true);
        this.setWord(word);
        this.setState(ArenaState.PLAYING);
        this.setFormatWord(formatWord);
        this.setCurrentTime(System.currentTimeMillis() + (getTime() * 1000L));

        System.out.println(getWord());

        for (Player player : arenaPlayers) {
            List<String> message = messages.getStringList("game-start");
            message.forEach(s -> player.sendMessage(KatsuUtils.coloredHex(s.replaceAll("%formatted_word%", getFormatWord()))));

            playSoundStart(player);
            player.teleport(this.getSpawn());

            // Send title to player.
            String[] titles = KatsuUtils.coloredHex(messages.getString("titles.game-start")).split(";");
            player.sendTitle(KatsuUtils.coloredHex(titles[0].replaceAll("%formatted_word%", getFormatWord())),
                    KatsuUtils.coloredHex(titles[1].replaceAll("%formatted_word%", getFormatWord())), 15, 50, 15);

            // Set player scoreboard.
            FastManager fastManager = new FastManager(plugin);
            fastManager.createPlayingScoreboard(this, player);
        }
    }

    /**
     * Terminate the game.
     */
    public void terminateGame() {
        this.setState(ArenaState.WAITING);
        this.setUsed(false);
        this.setWord(null);
        this.setFormatWord(null);
        this.setTime(0);

        this.setCurrentLives(getLives());
    }

    /**
     * Update the game every 10 ticks.
     */
    public void updateGame() {
        for (GamePlayer gamePlayer : getGamePlayers()) {
            Player player = Bukkit.getPlayer(gamePlayer.getUUID());

            if (getState() == ArenaState.PLAYING) {

                // Check time of the game, if the time is over, the game is over.
                if (System.currentTimeMillis() > getCurrentTime()) {
                    this.gameOver();
                    return;
                }

                // Send actionbar with word at player.
                String message = KatsuUtils.coloredHex(messages.getString("action-bar.game-word").replaceAll("%formatted_word%", getFormatWord()));
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));

                // Update player scoreboard
                if (plugin.getScoreboardMap().containsKey(player.getUniqueId())) {
                    FastManager fastManager = new FastManager(plugin);
                    fastManager.updatePlayingScoreboard(this, player);
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
            message.replaceAll(s -> s.replaceAll("%word%", getWord()));
            message.forEach(s -> player.sendMessage(KatsuUtils.coloredHex(s.replaceAll("%formatted_word%", getWord()))));

            String[] titles = KatsuUtils.coloredHex(messages.getString("titles.game-over")).split(";");
            player.sendTitle(KatsuUtils.coloredHex(titles[0].replaceAll("%word%", getWord())),
                    KatsuUtils.coloredHex(titles[1].replaceAll("%word%", getWord())), 15, 50, 15);

            // Terminate the game past 10 seconds.
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> arenaManager.leaveArena(this, player), 200);
            player.sendMessage(KatsuUtils.coloredHex(plugin.getPrefix() + messages.getString("arena-game-over-leave")));
        }
    }

    /**
     *
     */
    public void gameWin() {
        List<Player> arenaPlayers = getGamePlayers().stream()
                .map(GamePlayer::getUUID).filter(uuid -> Bukkit.getPlayer(uuid) != null)
                .map(Bukkit::getPlayer).collect(Collectors.toList());

        this.setState(ArenaState.ENDING);

        for (Player player : arenaPlayers) {

            // Send win message at player.
            long time = getTime() - ((getCurrentTime() - System.currentTimeMillis()) / 1000L);

            List<String> winMessages = messages.getStringList("game-win");
            winMessages.replaceAll(s -> s.replaceAll("%word%", getWord())
                    .replaceAll("%lives%", String.valueOf(getCurrentLives()))
                    .replaceAll("%time%", String.valueOf(time)));

            winMessages.forEach(s -> player.sendMessage(KatsuUtils.coloredHex(s)));
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 10, 1);

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
        FileConfiguration config = plugin.getConfig();

        List<Player> arenaPlayers = getGamePlayers().stream()
                .map(GamePlayer::getUUID).filter(uuid -> Bukkit.getPlayer(uuid) != null)
                .map(Bukkit::getPlayer).collect(Collectors.toList());

        for (Player player : arenaPlayers) {

            // Check if the word is correct.
            if (writeWord.equalsIgnoreCase(this.getWord())) {
                this.gameWin();

                // Check if the word contains a letter.
            } else if (getWord().contains(writeWord)) {
                if (writeWord.length() > 1) {
                    this.decreaseLives();
                    return;
                }
                char letter = writeWord.charAt(0);

                // Check if the letter is already discovered.
                if (discoverLetter.getCharDiscover().contains(letter)) {
                    player.sendMessage(KatsuUtils.coloredHex(plugin.getPrefix() + messages.getString("arena-letter-discovered")));
                    return;
                }

                // Discover the letter.
                discoverLetter.getCharDiscover().add(letter);
                this.setFormatWord(discoverLetter.discover(getWord(), getFormatWord(), letter));
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 10, 2);

            } else {
                this.decreaseLives();
            }
        }
    }

    /**
     * Decrease the lives of the player.
     */
    private void decreaseLives() {
        FileConfiguration config = plugin.getConfig();

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
                this.setFormatWord(discoverLetter.discoverRandomLetter(getWord(), getFormatWord()));
            }

            player.playSound(player.getLocation(), Sound.ENTITY_IRON_GOLEM_DAMAGE, 1, 1);
            player.sendMessage(KatsuUtils.coloredHex(plugin.getPrefix() + messages.getString("arena-lives-left").replace("%lives%", String.valueOf(lives))));

            double damage = (player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() / this.getLives());
            Bukkit.getScheduler().runTask(plugin, () -> player.damage(damage));

            // Send blood effect to player.
            if (config.getBoolean("config.blood-effect")) {
                this.sendBloodEffect(player);
            }
        }
        this.setCurrentLives(lives);
    }

    /**
     * Play sound game start.
     *
     * @param player Player to play the sound.
     */
    private void playSoundStart(Player player) {
        new BukkitRunnable() {
            private float i = 0.7f;
            private int time = 0;

            public void run() {
                if (time <= 2) {
                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 10, i);

                } else {
                    cancel();
                }
                i += 0.1f;
                time++;
            }
        }.runTaskTimer(plugin, 0, 10);
    }

    /**
     * Send blood effect to player.
     *
     * @param player Player to send the effect.
     */
    private void sendBloodEffect(Player player) {
        WorldBorder worldBorder = player.getWorld().getWorldBorder();

        ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
        PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.SET_BORDER_SIZE);
        packet.getDoubles().write(0, 0.0);

        protocolManager.sendServerPacket(player, packet);

        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () ->
        {
            PacketContainer packet2 = protocolManager.createPacket(PacketType.Play.Server.SET_BORDER_SIZE);
            packet.getDoubles().write(0, worldBorder.getSize());

            protocolManager.sendServerPacket(player, packet2);
        }, 10);
    }


    /**
     * Play sound game over.
     *
     * @param player Player to play the sound.
     */
    private void playSoundGameOver(Player player) {
        new BukkitRunnable() {
            private float i = 0.7f;
            private int time = 0;

            public void run() {
                if (time <= 2) {
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 10, i);

                } else {
                    cancel();
                }
                i -= 0.1f;
                time++;
            }
        }.runTaskTimer(plugin, 0, 7);
    }
}
