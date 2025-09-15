package com.makimo.werewolf.renderer;

import com.makimo.werewolf.entity.BombEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class BombRenderer extends GeoEntityRenderer<BombEntity> {
    public BombRenderer(EntityRendererProvider.Context renderManager, GeoModel<BombEntity> model) {
        super(renderManager, model);
        this.shadowRadius = 0.3f;
    }
}
