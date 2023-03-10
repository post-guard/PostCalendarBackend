package top.rrricardo.postcalendarbackend.services;

import top.rrricardo.postcalendarbackend.models.User;

/**
 * 提供用户相关操作的接口
 */
public interface UserService {
    /**
     * 用户登录
     *
     * @param emailAddress 电子邮件地址
     * @param password     用户密码
     * @return 为空则登录失败 否则登录成功
     */
    User userLogin(String emailAddress, String password);

    /**
     * 用户注册
     * @param user 注册用户的信息
     *             参数不为空应该由调用者保证
     * @return 是否注册成功
     */
    boolean userRegister(User user);
}
