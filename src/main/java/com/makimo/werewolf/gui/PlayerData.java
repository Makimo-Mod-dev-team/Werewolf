package com.makimo.werewolf.gui;

import java.util.UUID;

public class PlayerData {
    private final String name;
    private final UUID uuid;

    public PlayerData(String name, UUID uuid) {
        this.name = name;
        this.uuid = uuid;
    }

    public String getName() { return name; }
    public UUID getUUID() { return uuid; }
}