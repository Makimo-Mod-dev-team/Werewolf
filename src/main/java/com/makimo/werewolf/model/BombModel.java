package com.makimo.werewolf.model;

import com.makimo.werewolf.entity.BombEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

import static com.makimo.werewolf.Werewolf.MOD_ID;

public class BombModel extends GeoModel<BombEntity> {
    @Override
    public ResourceLocation getModelResource(BombEntity animatable) {
        return new ResourceLocation(MOD_ID, "geo/bomb.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(BombEntity animatable) {
        return new ResourceLocation(MOD_ID, "textures/entity/bomb.png");
    }

    @Override
    public ResourceLocation getAnimationResource(BombEntity animatable) {
        return new ResourceLocation(MOD_ID, "animations/bomb.animation.json");
    }
}