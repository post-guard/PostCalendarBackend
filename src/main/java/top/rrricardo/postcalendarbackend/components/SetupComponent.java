package top.rrricardo.postcalendarbackend.components;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import top.rrricardo.postcalendarbackend.enums.UserPermission;
import top.rrricardo.postcalendarbackend.mappers.GroupLinkMapper;
import top.rrricardo.postcalendarbackend.mappers.GroupMapper;
import top.rrricardo.postcalendarbackend.mappers.UserMapper;
import top.rrricardo.postcalendarbackend.models.Group;
import top.rrricardo.postcalendarbackend.models.GroupLink;
import top.rrricardo.postcalendarbackend.models.User;
import top.rrricardo.postcalendarbackend.services.UserService;
import top.rrricardo.postcalendarbackend.commons.Common;

@Component
public class SetupComponent implements CommandLineRunner {

    private final UserMapper userMapper;
    private final UserService userService;
    private final GroupMapper groupMapper;
    private final GroupLinkMapper groupLinkMapper;
    private final Logger logger;

    @Value("${user.rootUserEmailAddress}")
    private String rootUserEmailAddress;

    @Value("${user.rootUsername}")
    private String rootUsername;

    @Value("${user.rootPassword}")
    private String rootPassword;

    @Value("${group.usersName}")
    private String usersGroupName;

    @Value("${group.usersDetails}")
    private String usersGroupDetails;

    public SetupComponent(UserMapper userMapper,
                          UserService userService,
                          GroupMapper groupMapper,
                          GroupLinkMapper groupLinkMapper) {
        this.userMapper = userMapper;
        this.userService = userService;
        this.groupMapper = groupMapper;
        this.groupLinkMapper = groupLinkMapper;
        logger = LoggerFactory.getLogger(SetupComponent.class);
    }

    @Override
    public void run(String... args) {
        // 创建默认用户组
        var usersGroup = groupMapper.getGroupByName(usersGroupName);

        if (usersGroup == null) {
            usersGroup = new Group(
                    usersGroupName,
                    usersGroupDetails
            );

            groupMapper.createGroup(usersGroup);

            logger.info("Create default users group.");
        } else {
            logger.info("Default users group exists");
        }

        // 设置用户组ID
        Common.DefaultUsersGroupId = usersGroup.getId();
        logger.info("Default users group id is: " + Common.DefaultUsersGroupId);

        // 创建默认管理员账户
        var result = userMapper.getUserByEmail(rootUserEmailAddress);

        if (result == null) {
            var root = new User(
                    rootUserEmailAddress,
                    rootUsername,
                    rootPassword
            );

            userService.userRegister(root);
            logger.info("Root doesn't exist, created.");

            // 将默认管理员加入默认用户组
            var link = new GroupLink(root.getId(), Common.DefaultUsersGroupId, UserPermission.SUPER);

            groupLinkMapper.createGroupLink(link);
            logger.info("Add root to default users group.");
        } else {
            logger.info("Root exists.");
        }
    }
}
