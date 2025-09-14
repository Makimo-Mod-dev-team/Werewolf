package com.makimo.werewolf.game;

import com.makimo.werewolf.capability.Role;
import com.makimo.werewolf.registry.CapabilityRegister;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;

public class GameManager {
    private static final Set<UUID> wolves = new HashSet<>();
    private static final Set<UUID> villagers = new HashSet<>();
    private static final Set<UUID> fox = new HashSet<>();
    public static int number_wolves = 1;
    public static int number_foxes = 1;

    public static void assignRoles(MinecraftServer server) {
        List<ServerPlayer> players = new ArrayList<>(server.getPlayerList().getPlayers());
        if (players.isEmpty()) return;
        Collections.shuffle(players);

        for (int i = 0; i < players.size(); i++) {
            ServerPlayer player = players.get(i);
            Role choose;
            if (i < number_wolves) {
                choose = Role.WEREWOLF;
                wolves.add(player.getUUID());
            } else if (i <= number_wolves && i < number_wolves + number_foxes) {
                choose = Role.FOX;
                fox.add(player.getUUID());
            } else {
                choose = Role.VILLAGE;
                villagers.add(player.getUUID());
            }
            Role role = choose;
            player.getCapability(CapabilityRegister.ROLE_CAP).ifPresent(cap -> {
                cap.setRole(role);
            });
            player.sendSystemMessage(Component.literal("あなたの役職は: " + role.name()));
            player.sendSystemMessage(Component.literal(players.toString())); //デバッグ用
        }
    }
}
