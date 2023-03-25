package top.rrricardo.postcalendarbackend.models;

import top.rrricardo.postcalendarbackend.enums.UserPermission;

public class GroupLink {
    private int id;
    private int userId;
    private int groupId;
    private int permission;

    public GroupLink(int userId, int groupId, UserPermission permission) {
        this.userId = userId;
        this.groupId = groupId;
        this.permission = permission.ordinal();
    }

    public GroupLink(int id, int userId, int groupId, int permission) {
        this.id = id;
        this.userId = userId;
        this.groupId = groupId;
        this.permission = permission;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
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

    public int getId() {
        return id;
    }
}
