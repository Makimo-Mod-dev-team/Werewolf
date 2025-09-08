package com.makimo.werewolf.capability;

public class RoleCapability implements IRoleCapability {
    private Role role = Role.FOOL;

    @Override
    public Role getRole() {
        return role;
    }

    @Override
    public void setRole(Role role) {
        this.role = role;
    }
}
