package com.makimo.werewolf.util;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class DetectPlayer {
    public static Player DetectPlayerFromLayCast(Player player, int dis) {
        // プレイヤーの視線の位置
        Vec3 eyePos = player.getEyePosition();

        // 視線の向き (単位ベクトル)
        Vec3 lookVec = player.getLookAngle();

        // レイを飛ばす長さ（例えば 10 ブロック）
        double reach = (double) dis;

        // レイの終点
        Vec3 end = eyePos.add(lookVec.scale(reach));

        AABB searchBox = player.getBoundingBox().expandTowards(lookVec.scale(reach)).inflate(1.0D);

        EntityHitResult hitResult = ProjectileUtil.getEntityHitResult(
                player,
                eyePos,
                end,
                searchBox,
                (target) -> target instanceof Player && target != player, // プレイヤーのみ対象、自分は除外
                reach * reach
        );

        if (hitResult != null && hitResult.getEntity() instanceof Player hitPlayer) {
            // ここで他プレイヤーにヒット！
            return hitPlayer;
        }
        return null;
    }
}
