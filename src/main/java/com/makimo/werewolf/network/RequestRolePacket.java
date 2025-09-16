package com.makimo.werewolf.network;

import com.makimo.werewolf.capability.Role;
import com.makimo.werewolf.game.GameManager;
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
            Role role = GameManager.roleMap.get(msg.target);
            NetworkHandler.sendToPlayer(new ResponseRolePacket(GameManager.allPlayers.get(msg.target).getGameProfile().getName(), role.toString()), sender);
        });
        ctx.get().setPacketHandled(true);
    }
}

