package com.makimo.werewolf.network;

import com.makimo.werewolf.gui.CandleScreen;
import com.makimo.werewolf.gui.PlayerData;
import com.makimo.werewolf.gui.DisguiseScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;
import java.util.UUID;

@OnlyIn(Dist.CLIENT)
public class ClientPacketHandler {
    public static void openDisguise(List<PlayerData> data) {
        Minecraft.getInstance().setScreen(new DisguiseScreen(data));
    }

    public static void openCandle(List<PlayerData> data) {
        Minecraft.getInstance().setScreen(new CandleScreen(data));
    }

    public static void sendRole(String target, String role, UUID user) {
        Player player = Minecraft.getInstance().level.getPlayerByUUID(user);
        player.sendSystemMessage(Component.literal(target + "は" + role));
    }

    public static void killPlayer(UUID targetID, UUID userID) {
        Player target = Minecraft.getInstance().level.getPlayerByUUID(targetID);
        Player user = Minecraft.getInstance().level.getPlayerByUUID(userID);
        target.hurt(user.level().damageSources().playerAttack(user), Float.MAX_VALUE);
    }
}