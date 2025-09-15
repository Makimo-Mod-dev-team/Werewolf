package com.makimo.werewolf.item;

import com.makimo.werewolf.capability.Gravity;
import com.makimo.werewolf.registry.CapabilityRegister;
import com.min01.gravityapi.api.GravityChangerAPI;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class AntiGravityItem extends Item {
    public AntiGravityItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide) {
            return InteractionResultHolder.pass(player.getItemInHand(hand));
        }
        player.getCapability(CapabilityRegister.GRAVITY_CAP).ifPresent(cap -> {
            if (cap.getGravity() == Gravity.NOMAL) {
                cap.setGravity(Gravity.FLIP);
                GravityChangerAPI.setBaseGravityDirection(player, Direction.UP);
            } else {
                cap.setGravity(Gravity.NOMAL);
                GravityChangerAPI.setBaseGravityDirection(player, Direction.DOWN);
            }
        });
        return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide());
    }
}
