package com.makimo.werewolf.renderer;

import com.makimo.werewolf.entity.BombEntity;
import com.makimo.werewolf.model.BombModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class BombRenderer extends GeoEntityRenderer<BombEntity> {
    public BombRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new BombModel());
        this.shadowRadius = 0.1f;
    }
}
