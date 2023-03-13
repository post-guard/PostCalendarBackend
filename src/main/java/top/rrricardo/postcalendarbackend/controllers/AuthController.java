package top.rrricardo.postcalendarbackend.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.rrricardo.postcalendarbackend.dtos.ResponseDTO;
import top.rrricardo.postcalendarbackend.dtos.UserDTO;
import top.rrricardo.postcalendarbackend.models.User;
import top.rrricardo.postcalendarbackend.dtos.UserLoginDTO;
import top.rrricardo.postcalendarbackend.services.UserService;
import top.rrricardo.postcalendarbackend.utils.ControllerBase;

@RestController
@RequestMapping("/auth")
public class AuthController extends ControllerBase {
    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseDTO<UserDTO>> login(@RequestBody UserLoginDTO userLogin) {
        if (userLogin.getEmailAddress() == null || userLogin.getPassword() == null) {
            return badRequest();
        }

        var result = userService.userLogin(userLogin.getEmailAddress(), userLogin.getPassword());

        if (result == null) {
            return notFound("用户不存在");
        } else {
            return ok("登录成功", new UserDTO(result));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<ResponseDTO<UserDTO>> register(@RequestBody User user) {
        if (user.getEmailAddress() == null
                || user.getUsername() == null
                || user.getPassword() == null) {
            return badRequest();
        }

        var result = userService.userRegister(user);

        if (result) {
            return created("注册成功");
        } else {
            return badRequest("注册失败");
        }
    }


}
