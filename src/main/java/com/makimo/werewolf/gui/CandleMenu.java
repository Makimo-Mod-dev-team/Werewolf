package com.makimo.werewolf.gui;

import com.makimo.werewolf.registry.MenuRegistry;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.item.ItemStack;

public class CandleMenu extends AbstractContainerMenu {
    public CandleMenu(int id, Inventory inv) {
        super(MenuRegistry.CANDLE_MENU.get(), id);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int i) {
        return null;
    }

    @Override
    public boolean stillValid(Player player) {
        return true; // 常に開ける
    }

    // GUIを開くためのProvider
    public static class Provider implements MenuProvider {
        @Override
        public Component getDisplayName() {
            return Component.literal("Candle Menu");
        }

        @Override
        public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
            return new CandleMenu(id, inv);
        }
    }
}
