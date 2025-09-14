package com.makimo.werewolf.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public class CrystalItem extends Item {// 占いアイテム
    public CrystalItem(Properties properties) {
        super(properties);
    }

    public bool detect_player(Level level, Player player) { // プレイヤーの検知
        AABB serchBox = new AABB()
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide) {
            return InteractionResultHolder.pass(player.getItemInHand(interactionHand));
        }

        return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide());
    }
}
