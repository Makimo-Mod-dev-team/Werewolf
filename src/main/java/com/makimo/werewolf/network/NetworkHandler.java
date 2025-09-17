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

    public static void init(FMLCommonSetupEvent event) {
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

        INSTANCE.messageBuilder(OpenCandleMenuPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(OpenCandleMenuPacket::decode)
                .encoder(OpenCandleMenuPacket::encode)
                .consumerMainThread(OpenCandleMenuPacket::handle)
                .add();
        INSTANCE.registerMessage(id(), RequestRolePacket.class,
                        RequestRolePacket::encode,
                        RequestRolePacket::decode,
                        RequestRolePacket::handle
        );
        INSTANCE.registerMessage(id(), ResponseRolePacket.class,
                ResponseRolePacket::encode,
                ResponseRolePacket::decode,
                ResponseRolePacket::handle
        );
        INSTANCE.registerMessage(id(), PlayerButtonClickPacket.class,
                PlayerButtonClickPacket::encode,
                PlayerButtonClickPacket::decode,
                PlayerButtonClickPacket::handle
        );

        INSTANCE.registerMessage(id(), C2SDisguisePacket.class,
                C2SDisguisePacket::encode,
                C2SDisguisePacket::decode,
                C2SDisguisePacket::handle
        );

        INSTANCE.registerMessage(id(), S2CDisguisePacket.class,
                S2CDisguisePacket::encode,
                S2CDisguisePacket::decode,
                S2CDisguisePacket::handle
        );

        INSTANCE.registerMessage(id++, OpenDisguiseMenuPacket.class,
                OpenDisguiseMenuPacket::encode,
                OpenDisguiseMenuPacket::decode,
                OpenDisguiseMenuPacket::handle);
    }

    public static <MSG> void sendToPlayer(MSG message, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }

    public static <MSG> void sendToServer(MSG message) {
        INSTANCE.sendToServer(message);
    }

    public static <MSG> void sendToAllPlayers(MSG message) {
        INSTANCE.send(PacketDistributor.ALL.noArg(), message);
    }
}

