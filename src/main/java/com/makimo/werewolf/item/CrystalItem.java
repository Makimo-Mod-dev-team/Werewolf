package com.makimo.werewolf.item;

import com.makimo.werewolf.capability.Role;
import com.makimo.werewolf.registry.CapabilityRegister;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

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

    public Boolean detect_player(Level level, Player player) { // プレイヤーの検知
        Vec3 playerPos = player.position();
        AABB searchBox = new AABB(
            playerPos.x - 3.0D, playerPos.y - 3.0D, playerPos.z - 3.0D,
            playerPos.x + 3.0D, playerPos.y + 3.0D, playerPos.z + 3.0D
        );
        // プレイヤーの目の位置
        Vec3 eyePos = player.getEyePosition();
        // プレイヤーの視線ベクトル
        Vec3 lookVec = player.getLookAngle();

        List<Entity> nearbyEntities = level.getEntitiesOfClass(Entity.class, searchBox, (entity) -> entity != player);
        for (Entity targetEntity : nearbyEntities) {
            // ターゲットエンティティの目の位置
            Vec3 targetEyePos = targetEntity.getEyePosition();

            // プレイヤーからターゲットエンティティへのベクトル
            Vec3 playerToTargetVec = targetEyePos.subtract(eyePos).normalize();

            // 視線ベクトルとターゲットへのベクトルの内積を計算
            double dotProduct = lookVec.dot(playerToTargetVec);

            // 内積が1に近いほど視線上に近い。許容範囲を設定（例：0.9以上）
            if (dotProduct > 0.9) {
                // 視線の先にあるエンティティを検出！
                if (targetEntity instanceof Player) {
                    Player targetPlayer = (Player) targetEntity;
                    targetPlayer.getCapability(CapabilityRegister.ROLE_CAP).ifPresent(cap -> {
                        player.sendSystemMessage(Component.literal("占い結果：" + targetPlayer.getDisplayName().getString() + "は" + cap.getRole().name()));
                    });
                    return true; // 最初の見つかったプレイヤーで終了
                }
            }
        }
        return false;
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
