package com.makimo.werewolf.item;

import com.makimo.werewolf.capability.Role;
import com.makimo.werewolf.registry.CapabilityRegister;
import com.makimo.werewolf.util.DetectPlayer;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
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

    public void divination(Player player) {
        player.getCapability(CapabilityRegister.ROLE_CAP).ifPresent(cap -> {
            String displayText = switch (cap.getRole()) {
                case WEREWOLF -> "人狼陣営";
                case LUNATIC, VILLAGE -> "村人陣営";
                case FOX -> "妖狐陣営";
                default -> "プレイヤー";
            };
            if (cap.getRole() == Role.FOX) {
                player.hurt(player.level().damageSources().playerAttack(player), Float.MAX_VALUE);
                player.sendSystemMessage(Component.literal(player.getDisplayName().getString() + "は妖狐だった"));
            } else {
                player.sendSystemMessage(Component.literal("占い結果：" + player.getDisplayName().getString() + "は" + displayText));
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
        if (level.isClientSide) {
            return InteractionResultHolder.pass(player.getItemInHand(hand));
        }
        Player targetPlayer = DetectPlayer.DetectPlayerFromLayCast(player, 3);
        if (targetPlayer != null) {
            ItemStack stack = player.getItemInHand(hand);
            divination(targetPlayer);
            stack.shrink(1);
            player.setItemInHand(hand, stack.isEmpty() ? ItemStack.EMPTY : stack);
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
        } else {
            player.sendSystemMessage(Component.literal("占えるプレイヤーがいませんでした。"));
            return InteractionResultHolder.pass(player.getItemInHand(hand));
        }
    }
}
