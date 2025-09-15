package com.makimo.werewolf.item;

import com.makimo.werewolf.util.DetectPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.api.ScaleTypes;

public class SmallLightItem extends Item {
    public SmallLightItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide) {
            return InteractionResultHolder.pass(player.getItemInHand(hand));
        }
        Player targetPlayer = DetectPlayer.DetectPlayerFromLayCast(player, 3);
        if (targetPlayer != null) {
            ScaleData data = ScaleTypes.BASE.getScaleData(targetPlayer);
            data.setTargetScale(0.5f);
        }
        return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide());
    }
}
