package com.makimo.werewolf.network;

import com.makimo.werewolf.registry.CapabilityRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.UUID;
import java.util.function.Supplier;

public record RequestRolePacket(UUID target) {
    public static void encode(RequestRolePacket msg, FriendlyByteBuf buf) {
        buf.writeUUID(msg.target);
    }

    public static RequestRolePacket decode(FriendlyByteBuf buf) {
        return new RequestRolePacket(buf.readUUID());
    }

    public static void handle(RequestRolePacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer sender = ctx.get().getSender();
            if (sender == null) return;

            MinecraftServer server = sender.server;
            ServerPlayer target = server.getPlayerList().getPlayer(msg.target);

            if (target != null) {
                target.getCapability(CapabilityRegistry.ROLE_CAP).ifPresent(cap -> {
                    String role = cap.getRole().toString();
                    // クライアントに返信
                    NetworkHandler.sendToPlayer(new ResponseRolePacket(target.getName().getString(), role), sender);
                });
            }
        });
        ctx.get().setPacketHandled(true);
    }
}

