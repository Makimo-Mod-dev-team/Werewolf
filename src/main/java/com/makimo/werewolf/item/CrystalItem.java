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
            if (dotProduct > 0.98) {
                // 視線の先にあるエンティティを検出！
                if (targetEntity instanceof Player) {
                    Player targetPlayer = (Player) targetEntity;
                    targetPlayer.getCapability(CapabilityRegister.ROLE_CAP).ifPresent(cap -> {
                        String roleName = cap.getRole().name();
                        // Roleごとに表示文字を変更
                        String displayText = switch (cap.getRole()) {
                            case WEREWOLF -> "人狼陣営";
                            case VILLAGE -> "村人陣営";
                            case FOX -> "妖狐陣営";
                            default -> "プレイヤー";
                        };

                        // 使用者にのみ結果をチャット表示
                        player.sendSystemMessage(Component.literal("占い結果：" + targetPlayer.getDisplayName().getString() + "は" + displayText));

                        // 妖狐だった場合は占われたプレイヤーをkill
                        if (cap.getRole() == Role.FOX) {
                            targetPlayer.hurt(player.level().damageSources().playerAttack(player), Float.MAX_VALUE);
                            player.sendSystemMessage(Component.literal(targetPlayer.getDisplayName().getString() + "は妖狐だった"));
                        }
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
            // 占い成功時にアイテムを1つ消費
            ItemStack stack = player.getItemInHand(hand);
            stack.shrink(1);
            player.setItemInHand(hand, stack.isEmpty() ? ItemStack.EMPTY : stack);
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
        } else {
            player.sendSystemMessage(Component.literal("占えるプレイヤーがいませんでした。"));
            return InteractionResultHolder.pass(player.getItemInHand(hand));
        }
    }
}
