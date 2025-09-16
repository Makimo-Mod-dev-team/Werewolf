package com.makimo.werewolf.gui;

import com.makimo.werewolf.registry.MenuRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class CandleMenu extends AbstractContainerMenu {
    public Player player;// 使用者
    private List<PlayerData> data;

    public CandleMenu(int id, Inventory playerInv, FriendlyByteBuf buf) {
        super(MenuRegistry.CANDLE_MENU.get(), id);
        this.player = playerInv.player;
    }

    public CandleMenu(int id, Inventory playerInv, List<PlayerData> data) {
        this(id, playerInv, (FriendlyByteBuf) null);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {return ItemStack.EMPTY;}

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    public List<PlayerData> getDeadPlayers() { return this.data; }
}