package com.makimo.werewolf.registry;

import com.makimo.werewolf.capability.IRoleCapability;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public class CapabilityRegister {
    public static final Capability<IRoleCapability> ROLE_CAP = CapabilityManager.get(new CapabilityToken<>() {});
}
