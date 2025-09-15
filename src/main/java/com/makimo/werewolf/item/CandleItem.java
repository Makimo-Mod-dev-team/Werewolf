package com.makimo.werewolf.item;

import com.makimo.werewolf.gui.CandleMenu;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;

public class CandleItem extends Item {
    public CandleItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide) {
            NetworkHooks.openScreen(
                    (net.minecraft.server.level.ServerPlayer) player,
                    new CandleMenu.Provider(), // 後で定義するProvider
                    buf -> {}
            );
        }
        return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide());
    }
}
