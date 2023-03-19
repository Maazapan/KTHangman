package io.github.maazapan.kthangman.game.player;

import io.github.maazapan.kthangman.KTHangman;
import io.github.maazapan.kthangman.game.Arena;
import io.github.maazapan.kthangman.game.manager.ArenaManager;
import io.github.maazapan.kthangman.game.state.ArenaState;
import io.github.maazapan.kthangman.game.type.ArenaType;
import io.github.maazapan.kthangman.utils.KatsuUtils;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class GameArena extends Arena {

    private final KTHangman plugin;
    private final FileConfiguration messages;

    public GameArena(Arena arena, KTHangman plugin) {
        super(arena);
        this.plugin = plugin;
        this.messages = plugin.getLoaderManager()
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

        for (Player player : arenaPlayers) {
            List<String> message = messages.getStringList("game-start");
            message.forEach(s -> player.sendMessage(KatsuUtils.coloredHex(s.replaceAll("%formatted_word%", getFormatWord()))));

            playStartSound(player);
            player.teleport(this.getSpawn());

            // Send title to player.
            String[] titles = KatsuUtils.coloredHex(messages.getString("titles.game-start")).split(";");
            player.sendTitle(KatsuUtils.coloredHex(titles[0].replaceAll("%formatted_word%", getFormatWord())),
                    KatsuUtils.coloredHex(titles[1].replaceAll("%formatted_word%", getFormatWord())), 15, 50, 15);
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

        this.setCurrentLives(getLives());
    }

    /**
     * Update the game every 20 ticks.
     */
    public void updateGame() {
        for (GamePlayer gamePlayer : getGamePlayers()) {
            Player player = Bukkit.getPlayer(gamePlayer.getUUID());

            if (getState() == ArenaState.PLAYING) {
                // Send actionbar with word at player.
                String message = KatsuUtils.coloredHex(messages.getString("action-bar.game-word").replaceAll("%formatted_word%", getFormatWord()));
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
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

        for (Player player : arenaPlayers) {

        }
    }

    /**
     *
     */
    public void gameWin() {
        List<Player> arenaPlayers = getGamePlayers().stream()
                .map(GamePlayer::getUUID).filter(uuid -> Bukkit.getPlayer(uuid) != null)
                .map(Bukkit::getPlayer).collect(Collectors.toList());

        for (Player player : arenaPlayers) {
            List<String> winMessages = messages.getStringList("arena-win");

            winMessages.forEach(s -> player.sendMessage(KatsuUtils.coloredHex(s)));
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


        // Check if the word is correct.
        if (writeWord.equalsIgnoreCase(this.getWord())) {
            this.gameWin();

        } else {
            int lives = this.getCurrentLives() - 1;


            // Check if the player has no lives.
            if (lives <= 0) {
                this.gameOver();
                return;
            }

            for (Player player : arenaPlayers) {
                player.playSound(player.getLocation(), Sound.ENTITY_IRON_GOLEM_DAMAGE, 1, 1);
                player.sendMessage(KatsuUtils.coloredHex(plugin.getPrefix() + messages.getString("arena-lives-left").replace("%lives%", String.valueOf(lives))));

                double damage = (player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() / this.getLives());
                Bukkit.getScheduler().runTask(plugin, () -> player.damage(damage));
            }
            this.setCurrentLives(lives);
        }
    }

    /**
     * Play sound game start.
     *
     * @param player Player to play the sound.
     */
    private void playStartSound(Player player) {
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
}
