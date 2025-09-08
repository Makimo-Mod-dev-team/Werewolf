package com.makimo.werewolf.capability;

import com.makimo.werewolf.registry.CapabilityRegister;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CapabilityProvider implements ICapabilityProvider {
    private final IRoleCapability instance = new RoleCapability();
    private final LazyOptional<IRoleCapability> optional = LazyOptional.of(() -> instance);

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return cap == CapabilityRegister.ROLE_CAP ? optional.cast() : LazyOptional.empty();
    }
}
