package top.rrricardo.postcalendarbackend.services.Impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import top.rrricardo.postcalendarbackend.enums.UserPermission;
import top.rrricardo.postcalendarbackend.mappers.GroupLinkMapper;
import top.rrricardo.postcalendarbackend.mappers.UserMapper;
import top.rrricardo.postcalendarbackend.models.GroupLink;
import top.rrricardo.postcalendarbackend.models.User;
import top.rrricardo.postcalendarbackend.services.UserService;
import top.rrricardo.postcalendarbackend.commons.Common;
import top.rrricardo.postcalendarbackend.utils.EncryptSha256Util;


@Service
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    private final GroupLinkMapper groupLinkMapper;

    @Value("${user.password-salt}")
    private String passwordSalt;

    public UserServiceImpl(UserMapper userMapper, GroupLinkMapper groupLinkMapper) {
        this.userMapper = userMapper;
        this.groupLinkMapper = groupLinkMapper;
    }

    @Override
    public User userLogin(String emailAddress, String password) {
        User result = userMapper.getUserByEmail(emailAddress);

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
        User result = userMapper.getUserByEmail(user.getEmailAddress());

        if (result != null) {
            // 电子邮件已经被使用
            return false;
        }

        // 密码加盐哈希之后入库
        user.setPassword(sha256Password(user.getPassword()));

        userMapper.createUser(user);

        // 将用户加入默认用户组
        var link = new GroupLink(
                user.getId(),
                Common.DefaultUsersGroupId,
                UserPermission.SUPER
        );
        groupLinkMapper.createGroupLink(link);

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
