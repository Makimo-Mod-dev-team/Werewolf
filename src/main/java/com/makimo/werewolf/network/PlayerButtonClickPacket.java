package com.makimo.werewolf.network;

import com.makimo.werewolf.event.ServerEvents;
import com.makimo.werewolf.game.GameManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public record PlayerButtonClickPacket(UUID targetId) {

    public static void encode(PlayerButtonClickPacket msg, FriendlyByteBuf buf) {
        buf.writeUUID(msg.targetId());
    }

    public static PlayerButtonClickPacket decode(FriendlyByteBuf buf) {
        return new PlayerButtonClickPacket(buf.readUUID());
    }

    public static void handle(PlayerButtonClickPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer sender = ctx.get().getSender(); // ボタン押した本人
            if (sender != null) {
                ServerPlayer target = (ServerPlayer) GameManager.allPlayers.get(msg.targetId);
                if (target != null) {
                    // ★ サーバー側処理：変身解除など
                    ServerEvents.scheduleRevert(sender, 600);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}

