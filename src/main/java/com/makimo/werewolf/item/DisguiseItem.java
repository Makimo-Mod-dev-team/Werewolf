package com.makimo.werewolf.item;

import com.makimo.werewolf.manager.TransformationManager;
import com.makimo.werewolf.util.DetectPlayer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class DisguiseItem extends Item {
    public DisguiseItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide) {
            if (player instanceof ServerPlayer serverPlayer) {
                Player target = DetectPlayer.DetectPlayerFromLayCast(player, 3);
                if (target == null) {
                    return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
                }
                TransformationManager.transform(player, target.getUUID());
            }
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
}
