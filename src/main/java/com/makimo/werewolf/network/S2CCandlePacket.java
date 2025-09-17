package com.makimo.werewolf.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public record S2CCandlePacket(String name, String role, UUID player) {
    public static void encode(S2CCandlePacket msg, FriendlyByteBuf buf) {
        buf.writeUtf(msg.name);
        buf.writeUtf(msg.role);
        buf.writeUUID(msg.player);
    }

    public static S2CCandlePacket decode(FriendlyByteBuf buf) {
        return new S2CCandlePacket(buf.readUtf(), buf.readUtf(), buf.readUUID());
    }

    public static void handle(S2CCandlePacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ClientPacketHandler.sendRole(msg.name, msg.role, msg.player);
        });
        ctx.get().setPacketHandled(true);
    }
}
