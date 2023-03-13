package top.rrricardo.postcalendarbackend.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.rrricardo.postcalendarbackend.dtos.UserDTO;
import top.rrricardo.postcalendarbackend.models.User;
import top.rrricardo.postcalendarbackend.models.UserLogin;
import top.rrricardo.postcalendarbackend.services.UserService;
import top.rrricardo.postcalendarbackend.utils.ResponseUtil;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<UserDTO> login(@RequestBody UserLogin userLogin) {
        if (userLogin.getEmailAddress() == null || userLogin.getPassword() == null) {
            return ResponseUtil.badRequest();
        }

        var result = userService.userLogin(userLogin.getEmailAddress(), userLogin.getPassword());

        if (result == null) {
            return ResponseUtil.notFound();
        } else {
            return ResponseUtil.ok(new UserDTO(result));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@RequestBody User user) {
        if (user.getEmailAddress() == null
                || user.getUsername() == null
                || user.getPassword() == null) {
            return ResponseUtil.badRequest();
        }

        var result = userService.userRegister(user);

        if (result) {
            return ResponseUtil.created();
        } else {
            return ResponseUtil.badRequest();
        }
    }


}
