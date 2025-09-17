package com.makimo.werewolf.network;

import com.makimo.werewolf.gui.CandleScreen;
import com.makimo.werewolf.gui.PlayerData;
import com.makimo.werewolf.gui.DisguiseScreen;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class ClientPacketHandler {
    public static void openDisguise(List<PlayerData> data) {
        Minecraft.getInstance().setScreen(new DisguiseScreen(data));
    }

    public static void openCandle(List<PlayerData> data) {
        Minecraft.getInstance().setScreen(new CandleScreen(data));
    }
}