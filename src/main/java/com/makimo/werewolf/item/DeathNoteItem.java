package com.makimo.werewolf.item;

import com.makimo.werewolf.util.DetectPlayer;
import com.makimo.werewolf.util.ServerTaskScheduler;
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

public class DeathNoteItem extends Item {
    public DeathNoteItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("ノートに名前を書かれた"));
        tooltip.add(Component.literal("者は必ず死ぬ"));
        tooltip.add(Component.literal("価格:1").withStyle(ChatFormatting.GOLD));
        tooltip.add(Component.literal("死亡時間:30s後").withStyle(ChatFormatting.DARK_RED));

        super.appendHoverText(stack, level, tooltip, flag);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide) {
            Player target = DetectPlayer.DetectPlayerFromLayCast(player, 10);
            if (target != null) {
                player.sendSystemMessage(Component.literal(target.getDisplayName().getString() + ""));
                ServerTaskScheduler.schedule(20 * 3, () -> {
                    target.hurt(player.level().damageSources().playerAttack(player), Float.MAX_VALUE);
                });
            }
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
}
