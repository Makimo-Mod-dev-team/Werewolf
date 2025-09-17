package com.makimo.werewolf.gui;

import com.makimo.werewolf.event.ServerEvents;
import com.makimo.werewolf.manager.TransformationManager;
import com.makimo.werewolf.network.NetworkHandler;
import com.makimo.werewolf.network.PlayerButtonClickPacket;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public class PlayerMenuScreen extends Screen {
    private final List<PlayerData> playerList;
    private final int buttonWidth = 120;
    private final int buttonHeight = 20;
    private final int padding = 5;
    private final int faceSize = 8;

    public PlayerMenuScreen(List<PlayerData> playerList) {
        super(Component.literal("変身する対象を選択"));
        this.playerList = playerList;
    }

    @Override
    protected void init() {
        int y = 20;
        for (PlayerData pd : playerList) {
            int finalY = y;
            Button button = Button.builder(Component.literal(pd.getName()), btn -> {
                Minecraft.getInstance().setScreen(null);
                Player self = Minecraft.getInstance().player;
                // ボタンクリック時の処理
                TransformationManager.transform(self, pd.getUUID());
                self.sendSystemMessage(Component.literal(pd.getName() + "に変身中"));
                NetworkHandler.sendToServer(new PlayerButtonClickPacket(pd.getUUID()));
                }).size(buttonWidth, buttonHeight) // サイズを指定
                .pos(30, y)                     // 位置を指定
                .build();

            this.addRenderableWidget(button);
            y += buttonHeight + padding;
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTicks);

        int y = 20;
        for (PlayerData pd : playerList) {
            // スキンを描画（Minecraft標準のPlayerRendererを利用）
            AbstractClientPlayer player = (AbstractClientPlayer) Minecraft.getInstance().level.getPlayerByUUID(pd.getUUID());
            if (player != null) {
                ResourceLocation skin = player.getSkinTextureLocation();
                RenderSystem.setShaderTexture(0, skin);
                guiGraphics.blit(skin, 5, y, 8, 8, faceSize, faceSize, 64, 64); // 簡易的に頭だけ
            }
            y += buttonHeight + padding;
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
