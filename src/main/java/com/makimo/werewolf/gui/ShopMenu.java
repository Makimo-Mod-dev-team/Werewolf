package com.makimo.werewolf.gui;

import com.makimo.werewolf.registry.ItemRegistry;
import com.makimo.werewolf.registry.MenuRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

public class ShopMenu extends AbstractContainerMenu {
    private final Container shopContainer;
    public ShopMenu(int id, Inventory playerInv) {
        super(MenuRegistry.SHOP_MENU.get(), id);

        this.shopContainer = new SimpleContainer(1) {
            @Override
            public boolean stillValid(Player player) {
                return true;
            }
        };

        this.addSlot(new ShopSlot(this.shopContainer, 0, 80, 35));

        // プレイヤーインベントリスロットとか追加
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(playerInv, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }
        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(playerInv, col, 8 + col * 18, 142));
        }
    }

    public ShopMenu(int id, Inventory playerInv, FriendlyByteBuf extraData) {
        this(id, playerInv); // 使わなければこれでOK
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();

        // 商品がスロットにないならセットする
        Slot shopSlot = this.slots.get(0);
        if (shopSlot.getItem().isEmpty()) {
            shopSlot.set(new ItemStack(ItemRegistry.CRYSTAL.get(), 1));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {return ItemStack.EMPTY;}



    @Override
    public void clicked(int slotId, int dragType, ClickType clickType, Player player) {
        // 通常の処理をしないようにするなら super を呼ばない
        if (slotId == 0) {
            Slot slot = this.slots.get(slotId);
            ItemStack stack = slot.getItem();

            if (!stack.isEmpty()) {
                // 例えば「エメラルド1個でダイヤを買う」みたいな処理
                ItemStack cost = new ItemStack(ItemRegistry.COIN.get(), 1);
                if (player.getInventory().contains(cost)) {
                    player.getInventory().removeItem(cost);
                    player.getInventory().add(new ItemStack(ItemRegistry.CRYSTAL.get(), 1));
                }
            }
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}
