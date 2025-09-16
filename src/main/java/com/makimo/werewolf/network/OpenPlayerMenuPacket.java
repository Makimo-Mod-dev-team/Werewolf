package com.makimo.werewolf.network;

import com.makimo.werewolf.gui.PlayerData;
import com.makimo.werewolf.gui.PlayerMenuScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

public record OpenPlayerMenuPacket(List<PlayerData> players) {
    public static void encode(OpenPlayerMenuPacket msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.players().size());
        for (PlayerData pd : msg.players()) {
            buf.writeUUID(pd.getUUID());
            buf.writeUtf(pd.getName());
        }
    }

    public static OpenPlayerMenuPacket decode(FriendlyByteBuf buf) {
        int size = buf.readInt();
        List<PlayerData> players = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            UUID uuid = buf.readUUID();
            String name = buf.readUtf(32767);
            players.add(new PlayerData(name, uuid));
        }
        return new OpenPlayerMenuPacket(players);
    }

    public static void handle(OpenPlayerMenuPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Minecraft.getInstance().setScreen(new PlayerMenuScreen(msg.players()));
        });
        ctx.get().setPacketHandled(true);
    }
}
