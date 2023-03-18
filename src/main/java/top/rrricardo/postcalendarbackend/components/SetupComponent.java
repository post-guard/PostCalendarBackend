package top.rrricardo.postcalendarbackend.components;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import top.rrricardo.postcalendarbackend.enums.UserPermission;
import top.rrricardo.postcalendarbackend.mappers.OrganizationLinkMapper;
import top.rrricardo.postcalendarbackend.mappers.OrganizationMapper;
import top.rrricardo.postcalendarbackend.mappers.UserMapper;
import top.rrricardo.postcalendarbackend.models.Organization;
import top.rrricardo.postcalendarbackend.models.OrganizationLink;
import top.rrricardo.postcalendarbackend.models.User;
import top.rrricardo.postcalendarbackend.services.UserService;
import top.rrricardo.postcalendarbackend.utils.Common;

@Component
public class SetupComponent implements CommandLineRunner {

    private final UserMapper userMapper;
    private final UserService userService;
    private final OrganizationMapper organizationMapper;
    private final OrganizationLinkMapper organizationLinkMapper;
    private final Logger logger;

    @Value("${user.rootUserEmailAddress}")
    private String rootUserEmailAddress;

    @Value("${user.rootUsername}")
    private String rootUsername;

    @Value("${user.rootPassword}")
    private String rootPassword;

    @Value("${organization.usersName}")
    private String usersOrganizationName;

    @Value("${organization.usersDetails}")
    private String usersOrganizationDetails;

    public SetupComponent(UserMapper userMapper,
                          UserService userService,
                          OrganizationMapper organizationMapper,
                          OrganizationLinkMapper organizationLinkMapper) {
        this.userMapper = userMapper;
        this.userService = userService;
        this.organizationMapper = organizationMapper;
        this.organizationLinkMapper = organizationLinkMapper;
        logger = LoggerFactory.getLogger(SetupComponent.class);
    }

    @Override
    public void run(String... args) {
        // 创建默认用户组
        var usersOrganization = organizationMapper.getOrganizationByName(usersOrganizationName);

        if (usersOrganization == null) {
            usersOrganization = new Organization(
                    usersOrganizationName,
                    usersOrganizationDetails
            );

            organizationMapper.createOrganization(usersOrganization);

            logger.info("Create default users organization.");
        } else {
            logger.info("Default users organization exists");
        }

        // 设置用户组ID
        Common.DefaultUsersOrganizationId = usersOrganization.getId();
        logger.info("Default users organization id is: " + Common.DefaultUsersOrganizationId);

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
            var link = new OrganizationLink(root.getId(), Common.DefaultUsersOrganizationId, UserPermission.SUPER);

            organizationLinkMapper.createOrganizationLink(link);
            logger.info("Add root to default users organization.");
        } else {
            logger.info("Root exists.");
        }
    }
}
