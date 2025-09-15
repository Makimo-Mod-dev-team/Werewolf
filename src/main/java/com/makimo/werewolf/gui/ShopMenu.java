package com.makimo.werewolf.gui;

import com.makimo.werewolf.registry.CapabilityRegistry;
import com.makimo.werewolf.registry.ItemRegistry;
import com.makimo.werewolf.registry.MenuRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class ShopMenu extends AbstractContainerMenu {
    private final Container shopContainer;
    private Player player;
    public ShopMenu(int id, Inventory playerInv) {
        super(MenuRegistry.SHOP_MENU.get(), id);
        this.player = playerInv.player;

        this.shopContainer = new SimpleContainer(1) {
            @Override
            public boolean stillValid(Player player) {
                return true;
            }
        };

        this.addSlot(new Slot(this.shopContainer, 0, 80, 35));

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

    public void villagerSlot() {
        this.shopContainer.setItem(0, new ItemStack(ItemRegistry.CRYSTAL.get()));
    }

    public void werewolfSlot() {
        this.shopContainer.setItem(0, new ItemStack(ItemRegistry.CRYSTAL.get()));
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();

        // 商品がスロットにないならセットする
        if (this.player instanceof ServerPlayer serverPlayer) {
            serverPlayer.getCapability(CapabilityRegistry.ROLE_CAP).ifPresent(cap -> {
                switch (cap.getRole()) {
                    case VILLAGE -> villagerSlot();
                    case WEREWOLF -> werewolfSlot();
                }
            });
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {return ItemStack.EMPTY;}

    public void Shopping(Player player, ItemStack cost) {
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack slotStack = player.getInventory().getItem(i);
            if (slotStack.getItem() == cost.getItem() && slotStack.getCount() >= cost.getCount()) {
                slotStack.shrink(cost.getCount());
                break; // 最初のスロットで見つかったらループを終了
            }
        }
    }

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
                    Shopping(player, cost);
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
