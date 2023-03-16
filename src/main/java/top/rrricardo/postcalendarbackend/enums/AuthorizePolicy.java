package top.rrricardo.postcalendarbackend.enums;
public enum AuthorizePolicy {
    /**
     * 只要登录就可以访问
     */
    ONLY_LOGIN,
    /**
     * 当前用户可以访问（URL终结点包含用户ID）
     */
    CURRENT_USER,
    /**
     * 用户权限超过普通管理员
     */
    ABOVE_ADMINISTRATOR,
    /**
     * 用户权限超过超级管理员
     */
    ABOVE_SUPERMAN
}
