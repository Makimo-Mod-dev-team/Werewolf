package com.makimo.werewolf.entity;

import com.makimo.werewolf.registry.EntityRegistry;
import com.min01.gravityapi.api.GravityChangerAPI;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Comparator;
import java.util.List;

public class BombEntity extends ThrowableProjectile implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private boolean stuck = false;
    private Player owner;
    public BombEntity(EntityType<? extends ThrowableProjectile> type, Level level) {
        super(type, level);
    }

    public BombEntity(Level level, LivingEntity thrower) {
        super(EntityRegistry.BOMB_ENTITY.get(), thrower, level);
        this.owner = (Player) thrower;
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        if (this.stuck) {
            return;
        }
        this.setDeltaMovement(Vec3.ZERO);
        this.noPhysics = true;
        this.setNoGravity(true);
        this.setPos(result.getLocation());
        this.stuck = true;
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if (!this.level().isClientSide) {
            this.stuck = true;
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide && stuck) {
            double radius = 0.25D;
            List<Player> nearby = this.level().getEntitiesOfClass(Player.class,
                    this.getBoundingBox().inflate(radius),
                    p -> !p.isSpectator() && p.isAlive() && !p.getUUID().equals(this.owner.getUUID()));

            if (!nearby.isEmpty()) {
                explode();
                Player nearest = nearby.stream()
                        .min(Comparator.comparingDouble(p -> p.distanceToSqr(this)))
                        .orElse(null);

                if (nearest != null) {
                    // 即死（演出付きなら hurt でも可）
                    nearest.kill();
                }
            }
            this.setDeltaMovement(Vec3.ZERO);
            this.setPos(this.position());
        }
    }

    private void explode() {
        this.level().explode(this, this.getX(), this.getY(), this.getZ(),
                2.0F, Level.ExplosionInteraction.NONE);
        this.discard(); // 自分を消す
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    protected void defineSynchedData() {

    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {

    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {

    }
}
