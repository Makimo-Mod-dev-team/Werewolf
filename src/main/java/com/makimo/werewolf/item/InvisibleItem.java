package com.makimo.werewolf.item;

import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class InvisibleItem extends Item {
    public InvisibleItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        player.addEffect(new MobEffectInstance(MobEffectRegistry.TRUE_INVISIBILITY.get(), 400, 0, false, false, true));
        return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide());
    }
}
