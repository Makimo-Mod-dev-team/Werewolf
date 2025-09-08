package com.makimo.werewolf.capability;

import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ICapabilityProvider {
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side);
}
