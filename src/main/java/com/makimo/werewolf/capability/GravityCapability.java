package com.makimo.werewolf.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

public class GravityCapability implements IGravityCapability, INBTSerializable<CompoundTag> {
    private Gravity gravity = Gravity.NOMAL;

    @Override
    public Gravity getGravity() {
        return gravity;
    }

    @Override
    public void setGravity(Gravity gravity) {
        this.gravity = gravity;
    }

    @Override
    public CompoundTag serializeNBT() { // NBTに保存する関数
        CompoundTag tag = new CompoundTag();
        tag.putString("Gravity", gravity.name());
        return tag;
    }
    @Override
    public void deserializeNBT(CompoundTag nbt) { // NBTを取得する関数
        if (nbt.contains("Gravity")) {
            try {
                this.gravity = Gravity.valueOf(nbt.getString("Gravity")); // 文字列→enum
            } catch (IllegalArgumentException e) {
                this.gravity = Gravity.NOMAL; // 不正値ならデフォルトに戻す
            }
        }
    }
}
