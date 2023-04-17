package io.github.maazapan.kthangman.game.manager.hangman;

import io.github.maazapan.kthangman.KTHangman;
import io.github.maazapan.kthangman.utils.task.KatsuTask;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.util.EulerAngle;

public class HangAnimation {

    private final KTHangman plugin;
    private boolean isRunning = false;

    public HangAnimation(KTHangman plugin) {
        this.plugin = plugin;
    }


    /**
     * Play win animation.
     *
     * @param hangman Hangman to animate
     */
    public void winAnimation(Hangman hangman) {
        ArmorStand armorStand = hangman.getHangman();

        armorStand.setHeadPose(new EulerAngle(Math.toRadians(355.03f), Math.toRadians(0.0f), Math.toRadians(359.46f)));

        armorStand.getWorld().spawnParticle(Particle.BLOCK_DUST, armorStand.getLocation().add(0, 0.5, 0), 100, 0.4, 0.5, 0.4, 0.5, Material.CHAIN.createBlockData());
        armorStand.getWorld().playSound(armorStand.getLocation(), Sound.BLOCK_CHAIN_BREAK, 10, 0.2f);

        armorStand.setGravity(true);
        //  hangman.displayRandomWord("holograms.win-words");

        new KatsuTask(plugin, 1) {
            private boolean test = true;

            @Override
            public void run() {
                EulerAngle oldLeftArmPose = armorStand.getLeftArmPose();
                EulerAngle oldRightArmPose = armorStand.getRightArmPose();

                double x = Math.toDegrees(armorStand.getLeftArmPose().getX());

                System.out.println(x);

                if (x <= 213.0 && test) {
                    test = false;
                    return;
                }

                if(x >= 213.0 && !test) {
                    test = true;
                    return;
                }

                if (x >= 213.0 && test) {
                    armorStand.setLeftArmPose(oldLeftArmPose.add(Math.toRadians(-5.2f), 0, 0));
                    armorStand.setRightArmPose(oldRightArmPose.add(Math.toRadians(-5.2f), 0, 0));
                    return;
                }

                armorStand.setLeftArmPose(oldLeftArmPose.add(Math.toRadians(5.2f), 0, 0));
                armorStand.setRightArmPose(oldRightArmPose.add(Math.toRadians(5.2f), 0, 0));
            }
        }.start();
    }

    public void damageAnimation(Hangman hangman, int damage) {

    }

    public void loseAnimation(Hangman hangman) {

    }

    public void resetAnimation(Hangman hangman) {
        ArmorStand hologram = hangman.getHologram();
    }

    public void startAnimation(Hangman hangman) {
        ArmorStand armorStand = hangman.getHangman();


    }

    public void idleAnimation(Hangman hangman) {
        ArmorStand armorStand = hangman.getHangman();
    }
}
