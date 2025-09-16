package com.makimo.werewolf.network;

import com.makimo.werewolf.registry.CapabilityRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class CandleCheckPacket {
    private final String targetName;

    public CandleCheckPacket(String targetName) {
        this.targetName = targetName;
    }

    public static void encode(CandleCheckPacket msg, FriendlyByteBuf buf) {
        buf.writeUtf(msg.targetName);
    }

    public static CandleCheckPacket decode(FriendlyByteBuf buf) {
        return new CandleCheckPacket(buf.readUtf());
    }

    public static void handle(CandleCheckPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            var sender = ctx.get().getSender(); // このパケットを送ったプレイヤー（＝霊媒師）
            if (sender == null) return;

            var server = sender.server;
            var target = server.getPlayerList().getPlayerByName(msg.targetName);
            if (target != null) {
                target.getCapability(CapabilityRegistry.ROLE_CAP).ifPresent(role -> {
                    sender.sendSystemMessage(
                            Component.literal(msg.targetName + " の役職は " + role.getRole() + " です。")
                    );
                });
            } else {
                sender.sendSystemMessage(Component.literal(msg.targetName + " は見つかりません。"));
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
