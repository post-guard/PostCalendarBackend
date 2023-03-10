package top.rrricardo.postcalendarbackend.mappers;

import org.apache.ibatis.annotations.Mapper;
import top.rrricardo.postcalendarbackend.models.User;

import java.util.List;

@Mapper
public interface UserMapper {
    List<User> getUsers();

    User getUser(int id);

    int createUser(User user);

    int updateUser(User user);

    int deleteUser(int id);
}
