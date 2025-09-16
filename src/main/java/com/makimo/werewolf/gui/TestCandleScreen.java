package com.makimo.werewolf.gui;
import com.makimo.werewolf.network.NetworkHandler;
import com.makimo.werewolf.network.RequestRolePacket;
import com.makimo.werewolf.registry.CapabilityRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;

import java.util.List;

import static com.makimo.werewolf.Werewolf.MOD_ID;

public class TestCandleScreen extends Screen {
    private static final ResourceLocation BG_TEXTURE = new ResourceLocation(MOD_ID, "textures/gui/candle_menu.png");
    private final List<PlayerData> playerList;
    private final int buttonWidth = 120;
    private final int buttonHeight = 20;
    private final int padding = 5;

    public TestCandleScreen(List<PlayerData> playerList) {
        super(Component.literal("変身する対象を選択"));
        this.playerList = playerList;
    }

    @Override
    protected void init() {
        super.init();
        int y = 20;

        for (PlayerData pd : playerList) {
            int finalY = y;
            Button button = Button.builder(Component.literal(pd.getName()), btn -> {
                Minecraft.getInstance().setScreen(null);
                Minecraft.getInstance().player.sendSystemMessage(Component.literal(pd.getName() + "の霊を調べます。"));
                // 霊媒処理
                //NetworkHandler.INSTANCE.sendToServer(new CandleCheckPacket(playerName));
                NetworkHandler.sendToServer(new RequestRolePacket(pd.getUUID()));
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
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
