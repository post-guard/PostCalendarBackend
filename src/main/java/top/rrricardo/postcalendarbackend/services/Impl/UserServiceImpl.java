package top.rrricardo.postcalendarbackend.services.Impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import top.rrricardo.postcalendarbackend.mappers.UserMapper;
import top.rrricardo.postcalendarbackend.models.User;
import top.rrricardo.postcalendarbackend.services.UserService;
import top.rrricardo.postcalendarbackend.utils.EncryptSha256Util;


@Service
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;

    @Value("${user.password-salt}")
    private String passwordSalt;

    public UserServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public User userLogin(String emailAddress, String password) {
        var users = userMapper.getUsers();

        User result = null;
        for(var user: users) {
            if (user.getEmailAddress().equals(emailAddress)) {
                result = user;
                break;
            }
        }

        if (result == null) {
            return null;
        }

        if (result.getPassword().equals(sha256Password(password))) {
            return result;
        } else {
            return null;
        }
    }

    @Override
    public boolean userRegister(User user) {
        var users = userMapper.getUsers();

        User result = null;
        for(var item: users) {
            if (item.getEmailAddress().equals(user.getEmailAddress())) {
                result = item;
                break;
            }
        }

        if (result != null) {
            // 电子邮件已经被使用
            return false;
        }

        // 密码加盐哈希之后入库
        user.setPassword(sha256Password(user.getPassword()));

        userMapper.createUser(user);

        return true;
    }

    /**
     * 获得密码加盐哈希之后的值
     */
    private String sha256Password(String password) {
        for(var i = 0 ; i < 1000; i++) {
            password = EncryptSha256Util.sha256String(password + passwordSalt);
        }

        return password;
    }
}
