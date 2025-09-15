package com.makimo.werewolf.item;

import com.makimo.werewolf.gui.CandleMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class CandleItem extends Item {
    public CandleItem(Properties properties) {
        super(properties);
    }
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide) {
            // サーバー側でメニュー開く
            player.openMenu(new SimpleMenuProvider(
                    (id, inv, p) -> new CandleMenu(id, inv),
                    Component.translatable("gui.werewolf.candle")
            ));
        }

        return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide());
    }
}
