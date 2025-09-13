package com.makimo.werewolf.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

public class RoleCapability implements IRoleCapability, INBTSerializable<CompoundTag> {
    private Role role = Role.VILLAGE; // 初期設定

    @Override
    public Role getRole() { // 役割を返す関数
        return role;
    }

    @Override
    public void setRole(Role role) { // 役割を設定する関数
        this.role = role;
    }

    @Override
    public CompoundTag serializeNBT() { // NBTに保存する関数
        CompoundTag tag = new CompoundTag();
        tag.putString("Role", role.name());
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) { // NBTを取得する関数
        if (nbt.contains("Role")) {
            try {
                this.role = Role.valueOf(nbt.getString("Role")); // 文字列→enum
            } catch (IllegalArgumentException e) {
                this.role = Role.VILLAGE; // 不正値ならデフォルトに戻す
            }
        }
    }
}
