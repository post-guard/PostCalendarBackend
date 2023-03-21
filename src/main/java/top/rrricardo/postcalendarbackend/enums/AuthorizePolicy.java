package top.rrricardo.postcalendarbackend.enums;
public enum AuthorizePolicy {
    /**
     * 只要登录就可以访问
     */
    ONLY_LOGIN("onlyLogin"),
    /**
     * 用户在当前请求的组织中
     */
    CURRENT_GROUP_USER("currentGroupUser"),
    /**
     * 用户在当前请求的组织中 且权限在管理员之上
     */
    CURRENT_GROUP_ADMINISTRATOR("currentGroupAdministrator"),
    /**
     * 用户在当前请求的组织中 且权限在超级管理员之上
     */
    CURRENT_GROUP_SUPERMAN("currentGroupSuperman"),
    /**
     * 当前用户可以访问（URL终结点包含用户ID）
     */
    CURRENT_USER("currentUser"),
    /**
     * 用户权限超过普通管理员
     */
    ABOVE_ADMINISTRATOR("aboveAdministrator"),
    /**
     * 用户权限超过超级管理员
     */
    ABOVE_SUPERMAN("aboveSuperman");

    private final String implementName;

    AuthorizePolicy(String implementName) {
        this.implementName = implementName;
    }

    public String getImplementName() {
        return this.implementName;
    }
}
