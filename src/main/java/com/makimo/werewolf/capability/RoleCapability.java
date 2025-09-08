package com.makimo.werewolf.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

public class RoleCapability implements IRoleCapability, INBTSerializable<CompoundTag> {
    private Role role = Role.VILLAGE;

    @Override
    public Role getRole() {
        return role;
    }

    @Override
    public void setRole(Role role) {
        this.role = role;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putString("Role", role.name());
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        if (nbt.contains("Role")) {
            try {
                this.role = Role.valueOf(nbt.getString("Role")); // 文字列→enum
            } catch (IllegalArgumentException e) {
                this.role = Role.VILLAGE; // 不正値ならデフォルトに戻す
            }
        }
    }
}
