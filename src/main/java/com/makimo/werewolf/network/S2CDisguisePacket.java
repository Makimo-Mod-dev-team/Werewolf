package com.makimo.werewolf.network;

import com.makimo.werewolf.manager.TransformationManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public record S2CDisguisePacket(UUID selfId, UUID targetId) {
    public static void encode(S2CDisguisePacket msg, FriendlyByteBuf buf) {
        buf.writeUUID(msg.selfId());
        buf.writeUUID(msg.targetId());
    }

    public static S2CDisguisePacket decode(FriendlyByteBuf buf) {
        return new S2CDisguisePacket(buf.readUUID(), buf.readUUID());
    }

    public static void handle(S2CDisguisePacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            TransformationManager.transform(msg.selfId, msg.targetId);
        });
    }
}
