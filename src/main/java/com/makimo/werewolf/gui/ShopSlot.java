package com.makimo.werewolf.gui;

import com.makimo.werewolf.registry.ItemRegistry;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ShopSlot extends Slot {
    public ShopSlot(Container container, int index, int x, int y) {
        super(container, index, x, y);
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        // プレイヤーがこのスロットにアイテムを置けないようにする
        return false;
    }

    @Override
    public boolean mayPickup(Player player) {
        // クリックしたら「購入」扱いにしたいので true にしておく
        return true;
    }


    @Override
    public void onTake(Player player, ItemStack stack) {
        // ここが「スロットからアイテムを取ったとき」の処理

        // コスト（例：エメラルド1個）
        ItemStack cost = new ItemStack(ItemRegistry.COIN.get(), 1);

        if (player.getInventory().contains(cost)) {
            // 支払い処理
            player.getInventory().clearOrCountMatchingItems(
                    (itemStack) -> itemStack.getItem() == ItemRegistry.COIN.get(),
                    10,
                    player.inventoryMenu.getCraftSlots()
            );

            // 報酬アイテムを付与
            player.getInventory().add(stack.copy());

            // 商品スロットは減らさないように戻す
            this.set(stack);
        } else {
            // お金がない場合 → キャンセル（商品を戻す）
            this.set(stack);
        }

        super.onTake(player, stack);
    }
}
