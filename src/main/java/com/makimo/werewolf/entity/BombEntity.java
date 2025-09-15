package com.makimo.werewolf.entity;

import com.makimo.werewolf.registry.EntityRegistry;
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

import java.util.List;

public class BombEntity extends ThrowableProjectile implements GeoEntity {
    private boolean stuck = false;
    public BombEntity(EntityType<? extends ThrowableProjectile> type, Level level) {
        super(type, level);
    }

    public BombEntity(Level level, LivingEntity thrower) {
        super(EntityRegistry.get(), thrower, level);
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        // 壁や床に当たったら固着
        this.setDeltaMovement(Vec3.ZERO);
        this.noPhysics = true;
        this.stuck = true;
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        if (!this.level().isClientSide) {
            explode();
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide && stuck) {
            double radius = 3.0D;
            List<Player> nearby = this.level().getEntitiesOfClass(Player.class,
                    this.getBoundingBox().inflate(radius),
                    p -> !p.isSpectator() && p.isAlive());

            if (!nearby.isEmpty()) {
                explode();
            }
        }
    }

    private void explode() {
        this.level().explode(this, this.getX(), this.getY(), this.getZ(),
                3.0F, Level.ExplosionInteraction.TNT);
        this.discard(); // 自分を消す
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return null;
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
