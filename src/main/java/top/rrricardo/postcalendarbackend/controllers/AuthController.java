package top.rrricardo.postcalendarbackend.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.rrricardo.postcalendarbackend.dtos.ResponseDTO;
import top.rrricardo.postcalendarbackend.dtos.UserDTO;
import top.rrricardo.postcalendarbackend.models.User;
import top.rrricardo.postcalendarbackend.dtos.UserLoginDTO;
import top.rrricardo.postcalendarbackend.services.JwtService;
import top.rrricardo.postcalendarbackend.services.UserService;
import top.rrricardo.postcalendarbackend.utils.ControllerBase;

import java.util.HashMap;

@RestController
@RequestMapping("/auth")
public class AuthController extends ControllerBase {
    private final UserService userService;
    private final JwtService jwtService;

    private final Logger logger;

    public AuthController(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.logger = LoggerFactory.getLogger(AuthController.class);
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseDTO<HashMap<String, Object>>> login(@RequestBody UserLoginDTO userLogin) {
        if (userLogin.getEmailAddress() == null || userLogin.getPassword() == null) {
            logger.info("登录失败，缺少电子邮件地址或密码");
            return badRequest();
        }

        var result = userService.userLogin(userLogin.getEmailAddress(), userLogin.getPassword());

        if (result == null) {
            logger.info("登录失败，电子邮箱或密码错误");
            return notFound("电子邮件或密码错误");
        } else {
            var map = new HashMap<String, Object>();
            map.put("id", result.getId());
            map.put("username", result.getUsername());
            map.put("emailAddress", result.getEmailAddress());
            map.put("token", jwtService.generateJwtToken(result));

            logger.info("登录成功, id = {}", result.getId());
            return ok("登录成功", map);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<ResponseDTO<UserDTO>> register(@RequestBody User user) {
        if (user.getEmailAddress() == null
                || user.getUsername() == null
                || user.getPassword() == null) {
            logger.info("注册失败，缺少电子邮箱或用户名或密码");
            return badRequest();
        }

        var result = userService.userRegister(user);

        if (result) {
            logger.info("注册成功, id = {}", user.getId());
            return created("注册成功");
        } else {
            logger.info("注册失败");
            return badRequest("注册失败");
        }
    }


}
