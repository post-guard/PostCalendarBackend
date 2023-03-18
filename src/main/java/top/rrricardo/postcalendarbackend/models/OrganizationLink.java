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

    public OrganizationLink(int id, int userId, int organizationId, int permission) {
        this.id = id;
        this.userId = userId;
        this.organizationId = organizationId;
        this.permission = permission;
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

    public int getPermission() {
        return permission;
    }

    public void setPermission(int permission) {
        this.permission = permission;
    }

    public UserPermission getPermissionEnum() {
        return UserPermission.values()[permission];
    }

    public void setPermissionEnum(UserPermission permission) {
        this.permission = permission.ordinal();
    }
}
