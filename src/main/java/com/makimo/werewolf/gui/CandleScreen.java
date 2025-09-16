package com.makimo.werewolf.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.client.gui.components.Button;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

import static com.makimo.werewolf.Werewolf.MOD_ID;

@OnlyIn(Dist.CLIENT)
public class CandleScreen extends AbstractContainerScreen<CandleMenu> {
    private static final ResourceLocation BG_TEXTURE = new ResourceLocation(MOD_ID, "textures/gui/candle_menu.png");

    public CandleScreen(CandleMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    protected void init() {
        super.init();

        // 死亡プレイヤーのリストを取得
        List<String> deadPlayers = this.getMenu().getDeadPlayerNames();

        int buttonWidth = 80;
        int buttonHeight = 20;
        int colMax = 2; // 2列表示にする
        int paddingX = 10;
        int paddingY = 5;

        for (int i = 0; i < deadPlayers.size(); i++) {
            String playerName = deadPlayers.get(i);
            int col = Math.floorMod(i, colMax); // 列のインデックス
            int row = Math.floorDiv(i, colMax); // 行のインデックス

            int buttonX = this.leftPos + (this.imageWidth / 2) - (buttonWidth * colMax + paddingX * (colMax - 1)) / 2 + col * (buttonWidth + paddingX);
            int buttonY = this.topPos + 20 + row * (buttonHeight + paddingY);

            this.addRenderableWidget(Button.builder(Component.literal(playerName), (button) -> {
                this.getMenu().player.sendSystemMessage(Component.literal(playerName + "の霊を調べます。"));
                // ここに霊媒処理
            }).pos(buttonX, buttonY).width(buttonWidth).build());
        }
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        guiGraphics.blit(BG_TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight, this.imageWidth, this.imageHeight);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }
}