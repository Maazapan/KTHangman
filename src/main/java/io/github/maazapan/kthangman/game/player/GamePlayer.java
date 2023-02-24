package io.github.maazapan.kthangman.game.player;

import org.bukkit.inventory.ItemStack;

public class GamePlayer {

    private final String name;

    private ItemStack[] contents;
    private ItemStack[] armorContents;

    public GamePlayer(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public ItemStack[] getContents() {
        return contents;
    }

    public ItemStack[] getArmorContents() {
        return armorContents;
    }

    public void setContents(ItemStack[] contents) {
        this.contents = contents;
    }

    public void setArmorContents(ItemStack[] armorContents) {
        this.armorContents = armorContents;
    }
}
