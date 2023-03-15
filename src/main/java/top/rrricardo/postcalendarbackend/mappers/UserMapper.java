package top.rrricardo.postcalendarbackend.mappers;

import org.apache.ibatis.annotations.Mapper;
import top.rrricardo.postcalendarbackend.models.User;

import java.util.List;

@Mapper
public interface UserMapper {
    List<User> getUsers();

    User getUserById(int id);

    User getUserByEmail(String emailAddress);

    void createUser(User user);

    void updateUser(User user);

    void deleteUser(int id);
}
