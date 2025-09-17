package com.makimo.werewolf.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public record C2SDisguisePacket(UUID targetId) {
    public static void encode(C2SDisguisePacket msg, FriendlyByteBuf buf) {
        buf.writeUUID(msg.targetId());
    }

    public static C2SDisguisePacket decode(FriendlyByteBuf buf) {
        return new C2SDisguisePacket(buf.readUUID());
    }

    public static void handle(C2SDisguisePacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            NetworkHandler.sendToAllPlayers(new S2CDisguisePacket(ctx.get().getSender().getUUID(), msg.targetId));
        });
        ctx.get().setPacketHandled(true);
    }
}
