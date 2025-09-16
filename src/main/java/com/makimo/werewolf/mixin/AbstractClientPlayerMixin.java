package com.makimo.werewolf.mixin;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.makimo.werewolf.manager.TransformationManager;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.makimo.werewolf.Werewolf.MOD_ID;

@Mixin(AbstractClientPlayer.class)
public class AbstractClientPlayerMixin {

    private final LoadingCache<UUID, ResourceLocation> customSkinCache = CacheBuilder.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .build(new CacheLoader<>() {
                @Override
                public ResourceLocation load(UUID uuid) {
                    try {
                        // GameProfileをUUIDから取得
                        GameProfile gameProfile = new GameProfile(uuid, "");



                        // セッションサービスを使ってテクスチャ情報を取得
                        MinecraftSessionService sessionService = Minecraft.getInstance().getMinecraftSessionService();
                        Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> textures = sessionService.getTextures(gameProfile, false);

                        if (textures.containsKey(MinecraftProfileTexture.Type.SKIN)) {
                            MinecraftProfileTexture skinTexture = textures.get(MinecraftProfileTexture.Type.SKIN);
                            String skinUrl = skinTexture.getUrl();

                            try (InputStream stream = new URL(skinUrl).openStream()) {
                                ResourceLocation location = Minecraft.getInstance().getSkinManager().getInsecureSkinLocation(gameProfile);
                                NativeImage image = NativeImage.read(stream);
                                Minecraft.getInstance().getTextureManager().register(location, new DynamicTexture(image));
                                return location;
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return new ResourceLocation(MOD_ID, "skin/reimu.png");
                }
            });

    @Inject(method = "getSkinTextureLocation", at = @At("HEAD"), cancellable = true)
    private void onGetSkinTextureLocation(CallbackInfoReturnable<ResourceLocation> cir) {
        AbstractClientPlayer player = (AbstractClientPlayer)(Object)this;
        UUID playerUuid = player.getGameProfile().getId();

        if (playerUuid != null) {
            try {
                // 変身先のUUIDを取得（TransformationManagerクラスを使用）
                UUID targetUuid = TransformationManager.getTransformedUuid(playerUuid);

                if (targetUuid != null) {
                    ResourceLocation customSkin = customSkinCache.get(targetUuid);
                    player.sendSystemMessage(Component.literal(customSkin.getNamespace() + ":" + customSkin.getPath()));
                    if (customSkin != null) {
                        cir.setReturnValue(customSkin);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
