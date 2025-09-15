package com.makimo.werewolf.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import static com.makimo.werewolf.Werewolf.MOD_ID;

@OnlyIn(Dist.CLIENT)
public class ShopScreen extends AbstractContainerScreen<ShopMenu> {
    private static final ResourceLocation BG_TEXTURE = new ResourceLocation(MOD_ID, "textures/gui/shop_menu.png");

    public ShopScreen(ShopMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        guiGraphics.blit(BG_TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight, this.imageWidth, this.imageHeight);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }
}
