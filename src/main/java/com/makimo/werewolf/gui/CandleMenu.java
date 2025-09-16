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
    public Player player;
    private List<String> deadPlayerNames; // 死亡プレイヤー名のリスト

    public CandleMenu(int id, Inventory playerInv, FriendlyByteBuf extraData) {
        super(MenuRegistry.CANDLE_MENU.get(), id);
        this.player = playerInv.player;

        // FriendlyByteBufからプレイヤー名のリストを読み込む
        // データがない場合は空のリストを初期化
        if (extraData != null) {
            this.deadPlayerNames = extraData.readList(FriendlyByteBuf::readUtf);
        } else {
            this.deadPlayerNames = new ArrayList<>();
        }

        // プレイヤーインベントリスロット追加
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(playerInv, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }
        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(playerInv, col, 8 + col * 18, 142));
        }
    }

    public CandleMenu(int id, Inventory playerInv) {
        this(id, playerInv, null);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {return ItemStack.EMPTY;}

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    public List<String> getDeadPlayerNames() {
        return this.deadPlayerNames;
    }
}