package com.makimo.werewolf.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import static com.makimo.werewolf.Werewolf.MOD_ID;

public class NetworkHandler {
    private static final String PROTOCOL_VERSION = "1";
    public static SimpleChannel INSTANCE; // static 初期化しない
    private static int id = 0;

    private static int id() {
        return id++;
    }

    public static void init() {
        INSTANCE = NetworkRegistry.newSimpleChannel(
                new ResourceLocation(MOD_ID, "main"),
                () -> PROTOCOL_VERSION,
                PROTOCOL_VERSION::equals,
                PROTOCOL_VERSION::equals
        );

        INSTANCE.registerMessage(id(), CandleCheckPacket.class,
                CandleCheckPacket::encode,
                CandleCheckPacket::decode,
                CandleCheckPacket::handle
        );

        INSTANCE.messageBuilder(OpenPlayerMenuPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(OpenPlayerMenuPacket::decode)
                .encoder(OpenPlayerMenuPacket::encode)
                .consumerMainThread(OpenPlayerMenuPacket::handle)
                .add();
    }

    public static <MSG> void sendToPlayer(MSG message, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }
}

