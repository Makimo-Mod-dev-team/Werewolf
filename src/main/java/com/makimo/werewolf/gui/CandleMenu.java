package com.makimo.werewolf.gui;

import com.makimo.werewolf.registry.MenuRegistry;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

public class CandleMenu extends AbstractContainerMenu {
    public CandleMenu(int id, Inventory playerInv) {
        super(MenuRegistry.CANDLE_MENU.get(), id);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int i) {
        return null;
    }

    @Override
    public boolean stillValid(Player player) {
        return false;
    }
}
