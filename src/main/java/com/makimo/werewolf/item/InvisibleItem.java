package com.makimo.werewolf.item;

import io.redspace.ironsspellbooks.registries.MobEffectRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class InvisibleItem extends Item {
    public InvisibleItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("右クリックすると自身を透明化する。"));
        tooltip.add(Component.literal("手に持ったアイテムなども透明化するが"));
        tooltip.add(Component.literal("攻撃をすると解除される"));
        tooltip.add(Component.literal("価格:8").withStyle(ChatFormatting.GOLD));
        tooltip.add(Component.literal("効果時間:20s").withStyle(ChatFormatting.AQUA));

        super.appendHoverText(stack, level, tooltip, flag);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide) {
            player.addEffect(new MobEffectInstance(MobEffectRegistry.TRUE_INVISIBILITY.get(), 400, 0, false, false, true));
            level.playSound(
                    null,
                    player.blockPosition(),
                    SoundEvents.LIGHTNING_BOLT_IMPACT,
                    SoundSource.PLAYERS,
                    1.0f, // 音量
                    1.0f
            );
            stack.shrink(1);
            player.setItemInHand(hand, stack.isEmpty() ? ItemStack.EMPTY : stack);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
}
