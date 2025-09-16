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

public class WolvesAxeItem extends Item {
    public WolvesAxeItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("村人陣営に右クリックすると即死させるが"));
        tooltip.add(Component.literal("1つにつき1回しか使えない"));
        tooltip.add(Component.literal("価格:1").withStyle(ChatFormatting.GOLD));
        tooltip.add(Component.literal("クールタイム:5s").withStyle(ChatFormatting.BLUE));

        super.appendHoverText(stack, level, tooltip, flag);
    }

    public void attack(Player player) {
        player.getCapability(CapabilityRegistry.ROLE_CAP).ifPresent(cap -> {
            if (cap.getRole() != Role.FOX) {
                player.kill();
            }
        });
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide) {
            return InteractionResultHolder.pass(player.getItemInHand(hand));
        }
        Player targetPlayer = DetectPlayer.DetectPlayerFromLayCast(player, 3);
        if (targetPlayer != null) {
            attack(targetPlayer);
            ItemStack stack = player.getItemInHand(hand);
            player.level().playSound(
                    null,
                    player.getX(), // X座標
                    player.getY(), // Y座標
                    player.getZ(), // Z座標
                    SoundEvents.TOTEM_USE,
                    SoundSource.PLAYERS,
                    1.0f, // 音量
                    1.0f
            );
            stack.shrink(1);
            player.setItemInHand(hand, stack.isEmpty() ? ItemStack.EMPTY : stack);
        }
        player.getCooldowns().addCooldown(player.getItemInHand(hand).getItem(), 20 * 5);
        return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide());
    }
}
