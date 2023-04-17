package io.github.maazapan.kthangman.utils;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;

public class StandBuilder {

    private final ArmorStand armorStand;

    public StandBuilder(Location location) {
        this.armorStand = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
    }

    public StandBuilder(ArmorStand armorStand){
        this.armorStand = armorStand;
    }
    public StandBuilder setBasePlate(boolean basePlate) {
        this.armorStand.setBasePlate(basePlate);
        return this;
    }

    public StandBuilder setHelmet(ItemStack itemStack) {
        this.armorStand.getEquipment().setHelmet(itemStack);
        return this;
    }

    public StandBuilder setChestPlate(ItemStack itemStack) {
        this.armorStand.getEquipment().setChestplate(itemStack);
        return this;
    }

    public StandBuilder setLeggings(ItemStack itemStack) {
        this.armorStand.getEquipment().setLeggings(itemStack);
        return this;
    }

    public StandBuilder setDisplayName(String text) {
        this.armorStand.setCustomNameVisible(true);
        this.armorStand.setCustomName(text);
        return this;
    }

    public StandBuilder setBoots(ItemStack itemStack) {
        this.armorStand.getEquipment().setBoots(itemStack);
        return this;
    }

    public StandBuilder setInvulnerable(boolean invulnerable) {
        this.armorStand.setInvulnerable(invulnerable);
        return this;
    }

    public StandBuilder setVisible(boolean visible) {
        this.armorStand.setVisible(visible);
        return this;
    }

    public StandBuilder setSmall(boolean small) {
        this.armorStand.setSmall(small);
        return this;
    }

    public StandBuilder setGravity(boolean gravity) {
        this.armorStand.setGravity(gravity);
        return this;
    }

    public StandBuilder setArms(boolean arms) {
        this.armorStand.setArms(arms);
        return this;
    }

    public StandBuilder setBodyPose(float x, float y, float z) {
        this.armorStand.setBodyPose(new EulerAngle(Math.toRadians(x), Math.toRadians(y), Math.toRadians(z)));
        return this;
    }

    public StandBuilder setHeadPose(float x, float y, float z) {
        this.armorStand.setHeadPose(new EulerAngle(Math.toRadians(x), Math.toRadians(y), Math.toRadians(z)));
        return this;
    }

    public StandBuilder setLeftLegPose(float x, float y, float z) {
        this.armorStand.setLeftLegPose(new EulerAngle(Math.toRadians(x), Math.toRadians(y), Math.toRadians(z)));
        return this;
    }

    public StandBuilder setRightLegPose(float x, float y, float z) {
        this.armorStand.setRightLegPose(new EulerAngle(Math.toRadians(x), Math.toRadians(y), Math.toRadians(z)));
        return this;
    }

    public StandBuilder setLeftArmPose(float x, float y, float z) {
        this.armorStand.setLeftArmPose(new EulerAngle(Math.toRadians(x), Math.toRadians(y), Math.toRadians(z)));
        return this;
    }

    public StandBuilder setRightArmPose(float x, float y, float z) {
        this.armorStand.setRightArmPose(new EulerAngle(Math.toRadians(x), Math.toRadians(y), Math.toRadians(z)));
        return this;
    }

    public ArmorStand build() {
        return this.armorStand;
    }
}
