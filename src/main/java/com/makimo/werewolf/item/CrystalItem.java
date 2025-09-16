package com.makimo.werewolf.item;

import com.makimo.werewolf.capability.Role;
import com.makimo.werewolf.registry.CapabilityRegistry;
import com.makimo.werewolf.util.DetectPlayer;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class CrystalItem extends Item {// 占いアイテム
    public CrystalItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        // このメソッドがtrueを返すことで、エンチャントのオーラが表示される
        return true;
    }

    public void divination(Player player, Player target) {
        target.getCapability(CapabilityRegistry.ROLE_CAP).ifPresent(cap -> {
            String displayText = switch (cap.getRole()) {
                case WEREWOLF -> "人狼陣営";
                case LUNATIC, VILLAGE -> "村人陣営";
                case FOX -> "妖狐陣営";
                default -> "プレイヤー";
            };
            if (cap.getRole() == Role.FOX) {
                target.hurt(player.level().damageSources().playerAttack(player), Float.MAX_VALUE);
                player.sendSystemMessage(Component.literal(player.getDisplayName().getString() + "は妖狐だった"));
            } else {
                player.sendSystemMessage(Component.literal("占い結果：" + target.getDisplayName().getString() + "は" + displayText));
            }
        });
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("プレイヤーに右クリックすると"));
        tooltip.add(Component.literal("役職を確認できる"));
        tooltip.add(Component.literal("価格：1").withStyle(ChatFormatting.GOLD));

        super.appendHoverText(stack, level, tooltip, flag);
    }


    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide) {
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
        }
        Player targetPlayer = DetectPlayer.DetectPlayerFromLayCast(player, 3);
        if (targetPlayer != null) {
            divination(player, targetPlayer);
            stack.shrink(1);
            player.setItemInHand(hand, stack.isEmpty() ? ItemStack.EMPTY : stack);
            player.level().playSound(
                    null,
                    player.getX(), // X座標
                    player.getY(), // Y座標
                    player.getZ(), // Z座標
                    SoundEvents.ENCHANTMENT_TABLE_USE,
                    SoundSource.PLAYERS,
                    1.0f, // 音量
                    1.0f
            );
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
        } else {
            player.sendSystemMessage(Component.literal("占えるプレイヤーがいませんでした。"));
            return InteractionResultHolder.pass(player.getItemInHand(hand));
        }
    }
}
