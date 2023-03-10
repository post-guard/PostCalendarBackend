package top.rrricardo.postcalendarbackend.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import top.rrricardo.postcalendarbackend.mappers.UserMapper;
import top.rrricardo.postcalendarbackend.models.User;

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

        return ResponseEntity.ok(users);
    }

    @PostMapping("/")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        var result = userMapper.createUser(user);
        System.out.println(result);

        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable(value = "id") int id) {
        var user = userMapper.getUser(id);

        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return ResponseEntity.ok(user);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> update(@PathVariable(value = "id") int id,
                                       @RequestBody User user) {
        if (id != user.getId()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        var oldUser = getUser(id);
        if (oldUser == null) {
            // 用户不存在
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        userMapper.updateUser(user);

        var newUser = userMapper.getUser(id);

        if (newUser == null) {
            throw new NullPointerException();
        }

        return new ResponseEntity<>(newUser, HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<User> deleteUser(@PathVariable(value = "id") int id) {
        userMapper.deleteUser(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
