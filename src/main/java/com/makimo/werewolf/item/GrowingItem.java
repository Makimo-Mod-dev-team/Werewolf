package com.makimo.werewolf.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.players.PlayerList;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class GrowingItem extends Item {
    public GrowingItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("右クリックすると"));
        tooltip.add(Component.literal("全てのプレイヤーが発光する"));
        tooltip.add(Component.literal("価格:1").withStyle(ChatFormatting.GOLD));
        tooltip.add(Component.literal("効果時間:20s").withStyle(ChatFormatting.AQUA));

        super.appendHoverText(stack, level, tooltip, flag);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide) {
            PlayerList players = level.getServer().getPlayerList();
            for (Player target : players.getPlayers()) {
                if (target != player) {
                    target.addEffect(new MobEffectInstance(MobEffects.GLOWING, 400, 0, false, false));
                }
            }
            level.playSound(
                    null,
                    player.blockPosition(),
                    SoundEvents.ANVIL_DESTROY,
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
