package com.makimo.werewolf.gui;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import static com.makimo.werewolf.Werewolf.MOD_ID;

public class ShopScreen extends Screen {
    private static final ResourceLocation TEXTURE = new ResourceLocation(MOD_ID, "textures/gui/shop_gui.png");

    public ShopScreen() {
        super(Component.translatable("gui.", MOD_ID, ".shop"));
    }
}
