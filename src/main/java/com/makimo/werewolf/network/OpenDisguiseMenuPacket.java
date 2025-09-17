package com.makimo.werewolf.network;

import com.makimo.werewolf.gui.PlayerData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

public record OpenDisguiseMenuPacket(List<PlayerData> players) {
    public static void encode(OpenDisguiseMenuPacket msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.players().size());
        for (PlayerData pd : msg.players()) {
            buf.writeUUID(pd.getUUID());
            buf.writeUtf(pd.getName());
        }
    }

    public static OpenDisguiseMenuPacket decode(FriendlyByteBuf buf) {
        int size = buf.readInt();
        List<PlayerData> list = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            UUID uuid = buf.readUUID();
            String name = buf.readUtf();
            list.add(new PlayerData(name, uuid));
        }
        return new OpenDisguiseMenuPacket(list);
    }

    public static void handle(OpenDisguiseMenuPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (FMLEnvironment.dist == Dist.CLIENT) {
                ClientPacketHandler.openDisguise(msg.players());
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
