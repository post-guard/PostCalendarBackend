package top.rrricardo.postcalendarbackend.components;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import top.rrricardo.postcalendarbackend.mappers.UserMapper;
import top.rrricardo.postcalendarbackend.models.User;
import top.rrricardo.postcalendarbackend.services.UserService;

@Component
public class SetupComponent implements CommandLineRunner {

    private final UserMapper userMapper;
    private final UserService userService;
    private final Logger logger;

    @Value("${user.rootUserEmailAddress}")
    private String rootUserEmailAddress;

    @Value("${user.rootUsername}")
    private String rootUsername;

    @Value("${user.rootPassword}")
    private String rootPassword;

    public SetupComponent(UserMapper userMapper, UserService userService) {
        this.userMapper = userMapper;
        this.userService = userService;
        logger = LoggerFactory.getLogger(SetupComponent.class);
    }

    @Override
    public void run(String... args) throws Exception {
        var users = userMapper.getUsers();

        User result = null;
        for(var user : users) {
            if (user.getEmailAddress().equals(rootUserEmailAddress)) {
                result = user;
                break;
            }
        }

        if (result == null) {
            var root = new User(
                    rootUserEmailAddress,
                    rootUsername,
                    rootPassword
            );

            userService.userRegister(root);
            logger.info("Root doesn't exist, created.");
        } else {
            logger.info("Root exists.");
        }
    }
}
