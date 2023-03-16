package top.rrricardo.postcalendarbackend.models;

import top.rrricardo.postcalendarbackend.enums.UserPermission;

public class OrganizationLink {
    private int id;
    private int userId;
    private int organizationId;
    private int permission;

    public OrganizationLink(int userId, int organizationId, UserPermission permission) {
        this.userId = userId;
        this.organizationId = organizationId;
        this.permission = permission.ordinal();
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(int organizationId) {
        this.organizationId = organizationId;
    }

    public UserPermission getPermission() {
        return UserPermission.values()[permission];
    }

    public void setPermission(UserPermission permission) {
        this.permission = permission.ordinal();
    }
}
