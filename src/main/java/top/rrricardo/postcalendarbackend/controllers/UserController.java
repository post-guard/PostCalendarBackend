package top.rrricardo.postcalendarbackend.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import top.rrricardo.postcalendarbackend.mappers.UserMapper;
import top.rrricardo.postcalendarbackend.models.User;
import top.rrricardo.postcalendarbackend.utils.ResponseUtil;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserMapper userMapper;

    public UserController(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @GetMapping("/")
    public ResponseEntity<List<User>> getUsers() {
        var users = userMapper.getUsers();

        return ResponseUtil.ok(users);
    }

    @PostMapping("/")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        userMapper.createUser(user);

        // Mybatis框架会自动设置自增的ID值
        return ResponseUtil.created(user);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable(value = "id") int id) {
        var user = userMapper.getUser(id);

        if (user == null) {
            return ResponseUtil.notFound();
        }

        return ResponseUtil.ok(user);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> update(@PathVariable(value = "id") int id,
                                       @RequestBody User user) throws NullPointerException {
        if (id != user.getId()) {
            return ResponseUtil.badRequest();
        }

        var oldUser = getUser(id);
        if (oldUser == null) {
            // 用户不存在
            return ResponseUtil.notFound();
        }

        userMapper.updateUser(user);

        var newUser = userMapper.getUser(id);

        if (newUser == null) {
            throw new NullPointerException();
        }

        return ResponseUtil.ok(newUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<User> deleteUser(@PathVariable(value = "id") int id) {
        var user = userMapper.getUser(id);

        if (user == null) {
            return ResponseUtil.notFound();
        }

        userMapper.deleteUser(id);

        return ResponseUtil.noContent();
    }
}
