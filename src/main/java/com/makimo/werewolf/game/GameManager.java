package com.makimo.werewolf.game;

import com.makimo.werewolf.capability.Role;
import com.makimo.werewolf.registry.CapabilityRegister;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.*;

public class GameManager {
    private static final Set<UUID> wolves = new HashSet<>();
    private static final Set<UUID> villagers = new HashSet<>();
    private static final Set<UUID> fox = new HashSet<>();

    public static void assignRoles(MinecraftServer server) {
        List<ServerPlayer> players = server.getPlayerList().getPlayers();
        Collections.shuffle(players);

        for (int i = 0; i < players.size(); i++) {
            ServerPlayer player = players.get(i);
            switch (i) {
                case 0:
                    player.getCapability(CapabilityRegister.ROLE_CAP);
            }
        }
    }
}
