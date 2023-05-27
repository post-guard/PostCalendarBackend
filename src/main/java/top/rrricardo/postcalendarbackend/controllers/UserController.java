package top.rrricardo.postcalendarbackend.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import top.rrricardo.postcalendarbackend.annotations.Authorize;
import top.rrricardo.postcalendarbackend.dtos.ResponseDTO;
import top.rrricardo.postcalendarbackend.dtos.UserDTO;
import top.rrricardo.postcalendarbackend.enums.AuthorizePolicy;
import top.rrricardo.postcalendarbackend.mappers.UserMapper;
import top.rrricardo.postcalendarbackend.models.User;
import top.rrricardo.postcalendarbackend.utils.ControllerBase;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController extends ControllerBase {
    private final UserMapper userMapper;
    private final Logger logger;
    public UserController(UserMapper userMapper) {
        this.userMapper = userMapper;
        this.logger = LoggerFactory.getLogger(UserController.class);
    }

    @GetMapping("/")
    @Authorize(policy = AuthorizePolicy.ONLY_LOGIN)
    public ResponseEntity<ResponseDTO<List<UserDTO>>> getUsers() {
        var users = userMapper.getUsers();

        logger.info("成功获取所有用户列表");
        return ok(UserDTO.arrayOf(users));
    }

    @GetMapping("/{id}")
    @Authorize(policy = AuthorizePolicy.ONLY_LOGIN)
    public ResponseEntity<ResponseDTO<UserDTO>> getUser(@PathVariable(value = "id") int id) {
        var user = userMapper.getUserById(id);

        if (user == null) {
            logger.info("获取用户失败，不存在id={}的用户", id);
            return notFound("用户不存在");
        }

        logger.info("成功获取id={}的用户", id);
        return ok(new UserDTO(user));
    }

    @PutMapping("/{id}")
    @Authorize(policy = AuthorizePolicy.CURRENT_USER)
    public ResponseEntity<ResponseDTO<UserDTO>> update(@PathVariable(value = "id") int id,
                                       @RequestBody User user) throws NullPointerException {
        if (id != user.getId()) {
            logger.info("更新指定用户信息失败，用户ID和更新信息中的ID不一致");
            return badRequest();
        }

        var oldUser = userMapper.getUserById(id);
        if (oldUser == null) {
            // 用户不存在
            logger.info("更新指定用户信息失败，不存在id={}的用户", id);
            return notFound("用户不存在");
        }

        userMapper.updateUser(user);

        var newUser = userMapper.getUserById(id);

        if (newUser == null) {
            throw new NullPointerException();
        }

        logger.info("成功更新id={}的用户信息", id);
        return ok(new UserDTO(newUser));
    }

    @DeleteMapping("/{id}")
    @Authorize(policy = AuthorizePolicy.CURRENT_USER)
    public ResponseEntity<ResponseDTO<UserDTO>> deleteUser(@PathVariable(value = "id") int id) {
        var user = userMapper.getUserById(id);

        if (user == null) {
            logger.info("删除指定用户失败，不存在id={}的用户", id);
            return notFound("用户不存在");
        }

        userMapper.deleteUser(id);

        logger.info("成功删除id={}的用户", id);
        return noContent();
    }
}
