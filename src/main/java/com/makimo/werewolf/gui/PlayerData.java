package com.makimo.werewolf.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public class PlayerData {
    private final String name;
    private final UUID uuid;
    private final Player player;

    public PlayerData(String name, UUID uuid) {
        this.name = name;
        this.uuid = uuid;
        this.player = Minecraft.getInstance().level.getPlayerByUUID(uuid);
    }

    public String getName() { return name; }
    public UUID getUUID() { return uuid; }
    public Player getPlayer() { return player; }
}