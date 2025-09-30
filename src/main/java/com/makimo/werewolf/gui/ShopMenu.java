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

        this.shopContainer = new SimpleContainer(27) {
            @Override
            public boolean stillValid(Player player) {
                return true;
            }
        };

        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                this.addSlot(new Slot(this.shopContainer, x + y * 9, 8 + x * 18,  16 + y * 18));
            }
        }

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

    public void foxSlot() {
        this.shopContainer.setItem(0, new ItemStack(ItemRegistry.DISGUISE_ITEM.get()));
        this.shopContainer.setItem(1, new ItemStack(ItemRegistry.INVISIBLE_ITEM.get()));
        this.shopContainer.setItem(2, new ItemStack(ItemRegistry.GROWING_ITEM.get()));
        this.shopContainer.setItem(3, new ItemStack(ItemRegistry.BOMB_ITEM.get()));
        this.shopContainer.setItem(4, new ItemStack(ItemRegistry.CANDLE_ITEM.get()));
        this.shopContainer.setItem(5, new ItemStack(ItemRegistry.DEATH_NOTE_ITEM.get()));
    }

    public void villagerSlot() {
        this.shopContainer.setItem(0, new ItemStack(ItemRegistry.CRYSTAL.get()));
        this.shopContainer.setItem(1, new ItemStack(ItemRegistry.INVISIBLE_ITEM.get()));
        this.shopContainer.setItem(2, new ItemStack(ItemRegistry.GROWING_ITEM.get()));
        this.shopContainer.setItem(3, new ItemStack(ItemRegistry.BOMB_ITEM.get()));
        this.shopContainer.setItem(4, new ItemStack(ItemRegistry.CANDLE_ITEM.get()));
        this.shopContainer.setItem(5, new ItemStack(ItemRegistry.DEATH_NOTE_ITEM.get()));
    }

    public void werewolfSlot() {
        this.shopContainer.setItem(0, new ItemStack(ItemRegistry.WOLVES_AXE.get()));
        this.shopContainer.setItem(1, new ItemStack(ItemRegistry.INVISIBLE_ITEM.get()));
        this.shopContainer.setItem(2, new ItemStack(ItemRegistry.GROWING_ITEM.get()));
        this.shopContainer.setItem(3, new ItemStack(ItemRegistry.BOMB_ITEM.get()));
        this.shopContainer.setItem(4, new ItemStack(ItemRegistry.CANDLE_ITEM.get()));
        this.shopContainer.setItem(5, new ItemStack(ItemRegistry.DEATH_NOTE_ITEM.get()));
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();

        // 商品がスロットにないならセットする
        if (this.player instanceof ServerPlayer serverPlayer) {
            serverPlayer.getCapability(CapabilityRegistry.ROLE_CAP).ifPresent(cap -> {
                switch (cap.getRole()) {
                    case FOX -> foxSlot();
                    case VILLAGE, LUNATIC -> villagerSlot();
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
        Slot slot = this.slots.get(slotId);
        ItemStack stack = slot.getItem();
        if (stack.isEmpty()) {
            return;
        }
        switch (slotId) {
            case 0 : {
                if (player instanceof ServerPlayer serverPlayer) {
                    serverPlayer.getCapability(CapabilityRegistry.ROLE_CAP).ifPresent(cap -> {
                        switch (cap.getRole()) {
                            case VILLAGE, LUNATIC : {
                                ItemStack cost = new ItemStack(ItemRegistry.COIN.get(), 8);
                                if (player.getInventory().contains(cost)) {
                                    Shopping(player, cost);
                                    player.getInventory().add(new ItemStack(ItemRegistry.CRYSTAL.get(), 1));
                                }
                                break;
                            }
                            case WEREWOLF : {
                                ItemStack cost = new ItemStack(ItemRegistry.COIN.get(), 8);
                                if (player.getInventory().contains(cost)) {
                                    Shopping(player, cost);
                                    player.getInventory().add(new ItemStack(ItemRegistry.WOLVES_AXE.get(), 1));
                                }
                                break;
                            }
                            case FOX : {
                                ItemStack cost = new ItemStack(ItemRegistry.COIN.get(), 6);
                                if (player.getInventory().contains(cost)) {
                                    Shopping(player, cost);
                                    player.getInventory().add(new ItemStack(ItemRegistry.DISGUISE_ITEM.get(), 1));
                                }
                                break;
                            }
                        }
                    });
                }
                break;
            }
            case 1 : {
                ItemStack cost = new ItemStack(ItemRegistry.COIN.get(), 8);
                if (player.getInventory().contains(cost)) {
                    Shopping(player, cost);
                    player.getInventory().add(new ItemStack(ItemRegistry.INVISIBLE_ITEM.get(), 1));
                }
                break;
            }
            case 2 : {
                ItemStack cost = new ItemStack(ItemRegistry.COIN.get(), 3);
                if (player.getInventory().contains(cost)) {
                    Shopping(player, cost);
                    player.getInventory().add(new ItemStack(ItemRegistry.GROWING_ITEM.get(), 1));
                }
                break;
            }
            case 3 : {
                ItemStack cost = new ItemStack(ItemRegistry.COIN.get(), 10);
                if (player.getInventory().contains(cost)) {
                    Shopping(player, cost);
                    player.getInventory().add(new ItemStack(ItemRegistry.BOMB_ITEM.get(), 1));
                }
                break;
            }
            case 4 : {
                ItemStack cost = new ItemStack(ItemRegistry.COIN.get(), 4);
                if (player.getInventory().contains(cost)) {
                    Shopping(player, cost);
                    player.getInventory().add(new ItemStack(ItemRegistry.CANDLE_ITEM.get(), 1));
                }
                break;
            }
            case 5 : {
                ItemStack cost = new ItemStack(ItemRegistry.COIN.get(), 30);
                if (player.getInventory().contains(cost)) {
                    Shopping(player, cost);
                    player.getInventory().add(new ItemStack(ItemRegistry.DEATH_NOTE_ITEM.get(), 1));
                }
                break;
            }
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}
