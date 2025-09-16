package com.makimo.werewolf.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

import static com.makimo.werewolf.capability.Role.*;

public record ResponseRolePacket(String name, String role) {
    public static void encode(ResponseRolePacket msg, FriendlyByteBuf buf) {
        buf.writeUtf(msg.name);
        buf.writeUtf(msg.role);
    }

    public static ResponseRolePacket decode(FriendlyByteBuf buf) {
        return new ResponseRolePacket(buf.readUtf(), buf.readUtf());
    }

    public static void handle(ResponseRolePacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Player player = Minecraft.getInstance().player;
            if (player != null) {
                String displayText = switch (msg.role()) {
                    case WEREWOLF -> "人狼";
                    case LUNATIC, VILLAGE -> "村人";
                    case FOX -> "妖狐";
                    default -> "プレイヤー";
                };
                player.sendSystemMessage(Component.literal(
                        msg.name() + " の役職は " + msg.role() + " です。"
                ));
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
