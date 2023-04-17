package io.github.maazapan.kthangman.game.manager.hangman;

import io.github.maazapan.kthangman.KTHangman;
import io.github.maazapan.kthangman.utils.ItemBuilder;
import io.github.maazapan.kthangman.utils.KatsuUtils;
import io.github.maazapan.kthangman.utils.StandBuilder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Hangman {

    private KTHangman plugin;

    private ArmorStand hangmanStand;
    private ArmorStand hologramStand;

    private final List<ArmorStand> armorStandList;

    public Hangman(KTHangman plugin) {
        this.plugin = plugin;
        this.armorStandList = new ArrayList<>();
    }

    /**
     * Spawn Hangman stand at the location.
     *
     * @param player   Player to Create skull owner
     * @param location Location to spawn the hangman
     */
    public void spawnHangman(Player player, Location location)  {
        ItemStack helmet = new ItemBuilder(Material.PLAYER_HEAD)
                .setSkullOwner(player.getName()).build();

        // Create the hangman armor stand.
        hangmanStand = new StandBuilder(location.add(0, 0.5, 0))
                .setBasePlate(false).setGravity(false).setArms(true)
                .setBodyPose(0, 358.16f, 0).setHeadPose(55.94f, 0, 350.4f).setLeftLegPose(0, 359.9f, 350.16f) // 327.14f, 299.83f, 0
                .setRightLegPose(180.74f, 177.87f, 186.88f).setLeftArmPose(327.14f, 299.83f, 0).setRightArmPose(349.27f, 53.98f, 10)
                .setBoots(new ItemStack(Material.LEATHER_BOOTS)).setLeggings(new ItemStack(Material.LEATHER_LEGGINGS))
                .setChestPlate(new ItemStack(Material.LEATHER_CHESTPLATE)).setHelmet(helmet).build();

        armorStandList.add(hangmanStand);

        // Create a chain armor stand.
        for (double i = -0.5; i < 1.5; i += 0.5) {
            ArmorStand chainsStand = new StandBuilder(hangmanStand.getLocation().clone().add(0, i, 0))
                    .setVisible(false).setGravity(false)
                    .setHelmet(new ItemStack(Material.CHAIN)).build();
            armorStandList.add(chainsStand);
        }

        // Create a hologram armor stand.
        hologramStand = new StandBuilder(hangmanStand.getLocation().clone().add(0, 0.0, 0))
                .setBasePlate(false).setGravity(false).setArms(false).setVisible(false).build();

        // Display a random word from the config.
        if (new Random().nextInt(2) == 0) {
            this.displayRandomWord("holograms.random-words");
        }
    }

    /**
     * Display a random word from the config
     * at the hologram stand.
     */
    public void displayRandomWord(String path) {
        FileConfiguration messages = plugin.getLoaderManager()
                .getFileManager().getMessages();

        List<String> words = messages.getStringList(path);
        String word = words.get(new Random().nextInt(words.size()));

        hologramStand.setCustomNameVisible(true);
        hologramStand.setCustomName(KatsuUtils.coloredHex(word));

        // Set the hologram stand to invisible after 5 seconds.
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> hologramStand.setCustomNameVisible(false), 100L);
    }

    /**
     * Display a word at the hologram stand.
     * @param word Word to display
     */
    public void displayWord(String word) {
        hologramStand.setCustomNameVisible(true);
        hologramStand.setCustomName(KatsuUtils.coloredHex(word));

        // Set the hologram stand to invisible after 5 seconds.
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> hologramStand.setCustomNameVisible(false), 100L);
    }

    /**
     * Remove all armor stands in the list.
     */
    public void removeStands() {
        armorStandList.forEach(ArmorStand::remove);
    }

    public ArmorStand getHangman() {
        return hangmanStand;
    }

    public ArmorStand getHologram() {
        return hologramStand;
    }

    public List<ArmorStand> getArmorStandList() {
        return armorStandList;
    }
}
