package com.makimo.werewolf.gui;

import com.makimo.werewolf.network.C2SCandlePacket;
import com.makimo.werewolf.network.NetworkHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.List;

public class CandleScreen extends Screen {
    private final List<PlayerData> playerList;
    private final int buttonWidth = 120;
    private final int buttonHeight = 20;
    private final int padding = 5;
    private final int faceSize = 8;
    public CandleScreen(List<PlayerData> playerList) {
        super(Component.literal("Candle Menu"));
        this.playerList = playerList;
    }

    @Override
    protected void init() {
        int y = 20;
        for (PlayerData pd : playerList) {
            Button button = Button.builder(Component.literal(pd.getName()), btn -> {
                        Minecraft.getInstance().setScreen(null);
                        NetworkHandler.sendToServer(new C2SCandlePacket(pd.getUUID()));
                    })
                    .size(buttonWidth, buttonHeight) // サイズを指定
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
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
