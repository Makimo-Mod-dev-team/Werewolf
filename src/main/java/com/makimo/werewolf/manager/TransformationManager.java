package com.makimo.werewolf.manager;

import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TransformationManager {
    private static final Map<UUID, UUID> transformedPlayers = new HashMap<>();

    public static void transform(Player player, UUID targetUuid) {
        transformedPlayers.put(player.getUUID(), targetUuid);
        // クライアントに変身を通知する（例: Packetを送信）
    }

    public static UUID getTransformedUuid(UUID playerUuid) {
        return transformedPlayers.get(playerUuid);
    }

    public static void untransform(Player player) {
        transformedPlayers.remove(player.getUUID());
    }
}