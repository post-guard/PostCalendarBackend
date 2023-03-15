package top.rrricardo.postcalendarbackend.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import top.rrricardo.postcalendarbackend.annotations.Authorize;
import top.rrricardo.postcalendarbackend.dtos.ResponseDTO;
import top.rrricardo.postcalendarbackend.dtos.UserDTO;
import top.rrricardo.postcalendarbackend.enums.UserPermission;
import top.rrricardo.postcalendarbackend.mappers.UserMapper;
import top.rrricardo.postcalendarbackend.models.User;
import top.rrricardo.postcalendarbackend.utils.ControllerBase;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController extends ControllerBase {
    private final UserMapper userMapper;

    public UserController(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @GetMapping("/")
    @Authorize(permission = UserPermission.ADMIN)
    public ResponseEntity<ResponseDTO<List<UserDTO>>> getUsers() {
        var users = userMapper.getUsers();

        return ok(UserDTO.arrayOf(users));
    }

    @GetMapping("/{id}")
    @Authorize(permission = UserPermission.USER)
    public ResponseEntity<ResponseDTO<UserDTO>> getUser(@PathVariable(value = "id") int id) {
        var user = userMapper.getUser(id);

        if (user == null) {
            return notFound("用户不存在");
        }

        return ok(new UserDTO(user));
    }

    @PutMapping("/{id}")
    @Authorize(permission = UserPermission.ADMIN)
    public ResponseEntity<ResponseDTO<UserDTO>> update(@PathVariable(value = "id") int id,
                                       @RequestBody User user) throws NullPointerException {
        if (id != user.getId()) {
            return badRequest();
        }

        var oldUser = getUser(id);
        if (oldUser == null) {
            // 用户不存在
            return notFound("用户不存在");
        }

        userMapper.updateUser(user);

        var newUser = userMapper.getUser(id);

        if (newUser == null) {
            throw new NullPointerException();
        }

        return ok(new UserDTO(newUser));
    }

    @DeleteMapping("/{id}")
    @Authorize(permission = UserPermission.ADMIN)
    public ResponseEntity<ResponseDTO<UserDTO>> deleteUser(@PathVariable(value = "id") int id) {
        var user = userMapper.getUser(id);

        if (user == null) {
            return notFound("用户不存在");
        }

        userMapper.deleteUser(id);

        return noContent();
    }
}
