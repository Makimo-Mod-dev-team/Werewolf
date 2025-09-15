package com.makimo.werewolf.item;

import com.makimo.werewolf.entity.BombEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class BombItem extends Item {
    public BombItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide) {
            BombEntity bomb = new BombEntity(level, player);
            bomb.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 0.9F, 1.0F);
            level.addFreshEntity(bomb);
        }

        stack.shrink(1); // 消費
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
}
