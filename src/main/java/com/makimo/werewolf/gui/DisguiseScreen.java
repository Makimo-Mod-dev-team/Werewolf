package com.makimo.werewolf.gui;

import com.makimo.werewolf.network.C2SDisguisePacket;
import com.makimo.werewolf.network.NetworkHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class DisguiseScreen extends Screen {
    private final List<PlayerData> playerList;
    private final int buttonWidth = 120;
    private final int buttonHeight = 20;
    private final int padding = 5;
    private final int faceSize = 8;
    public DisguiseScreen(List<PlayerData> playerList) {
        super(Component.literal("Player Menu"));
        this.playerList = playerList;
    }

    @Override
    protected void init() {
        int y = 20;
        for (PlayerData pd : playerList) {
            Button button = Button.builder(Component.literal(pd.getName()), btn -> {
                        Minecraft.getInstance().setScreen(null);
                        Player self = Minecraft.getInstance().player;
                        self.sendSystemMessage(Component.literal(pd.getName() + "に変身中"));
                        NetworkHandler.sendToServer(new C2SDisguisePacket(pd.getUUID()));
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

