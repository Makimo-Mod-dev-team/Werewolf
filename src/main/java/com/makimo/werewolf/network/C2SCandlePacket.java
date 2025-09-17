package com.makimo.werewolf.network;

import com.makimo.werewolf.game.GameManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public record C2SCandlePacket(UUID target) {
    public static void encode(C2SCandlePacket msg, FriendlyByteBuf buf) {
        buf.writeUUID(msg.target);
    }

    public static C2SCandlePacket decode(FriendlyByteBuf buf) {
        return new C2SCandlePacket(buf.readUUID());
    }

    public static void handle(C2SCandlePacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            NetworkHandler.sendToPlayer(new S2CCandlePacket(
                    GameManager.allPlayers.get(msg.target).getName().getString(),
                    GameManager.roleMap.get(msg.target).toString(),
                    ctx.get().getSender().getUUID()),
                    ctx.get().getSender());
        });
        ctx.get().setPacketHandled(true);
    }
}
