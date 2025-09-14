package com.makimo.werewolf.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class CrystalItem extends Item {// 占いアイテム
    public CrystalItem(Properties properties) {
        super(properties);
    }

    public Boolean detect_player(Level level, Player player) { // プレイヤーの検知
        Vec3 playerPos = player.position();
        AABB serchBox = new AABB(
                playerPos.x - 3.0D, playerPos.y - 3.0D, playerPos.z - 3.0D,
                playerPos.x + 3.0D, playerPos.y + 3.0D, playerPos.z + 3.0D
        );

        List<Player> nearByPlayers = level.getEntitiesOfClass(
                Player.class,
                serchBox,
                (targetPlayer) -> targetPlayer != player
        );

        if (!nearByPlayers.isEmpty()) {
            player.sendSystemMessage(Component.literal("OK"));
            return true;
        } else {
            return false;
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide) {
            return InteractionResultHolder.pass(player.getItemInHand(hand));
        }
        if (detect_player(level, player)) {
            return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide());
        } else {
            return InteractionResultHolder.pass(player.getItemInHand(hand));
        }
    }
}
