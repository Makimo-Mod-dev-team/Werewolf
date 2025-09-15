package com.makimo.werewolf.item;

import com.makimo.werewolf.capability.Role;
import com.makimo.werewolf.registry.CapabilityRegister;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class WolvesAxeItem extends Item {
    public WolvesAxeItem(Properties properties) {
        super(properties);
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
                    AtomicBoolean kill = new AtomicBoolean(false);
                    targetPlayer.getCapability(CapabilityRegister.ROLE_CAP).ifPresent(cap -> {
                        if (cap.getRole() == Role.VILLAGE) {
                            targetPlayer.kill();
                            kill.set(true);
                        }
                    });
                    return kill.get(); // 最初の見つかったプレイヤーで終了
                }
            }
        }
        return false;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("村人陣営に右クリックすると即死させるが"));
        tooltip.add(Component.literal("1つにつき1回しか使えない"));
        tooltip.add(Component.literal("価格：1").withStyle(ChatFormatting.GOLD));
        tooltip.add(Component.literal("クールタイム：5s").withStyle(ChatFormatting.BLUE));

        super.appendHoverText(stack, level, tooltip, flag);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide) {
            return InteractionResultHolder.pass(player.getItemInHand(hand));
        }
        if (detect_player(level, player)) {
            ItemStack stack = player.getItemInHand(hand);
            player.level().playSound(
                    null,
                    player.getX(), // X座標
                    player.getY(), // Y座標
                    player.getZ(), // Z座標
                    SoundEvents.ITEM_BREAK,
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
