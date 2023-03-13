package top.rrricardo.postcalendarbackend.dtos;

import top.rrricardo.postcalendarbackend.models.User;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户DTO对象
 */
public class UserDTO {
    private int id;
    private String username;
    private String emailAddress;

    public UserDTO(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.emailAddress = user.getEmailAddress();
    }

    /**
     * 创建userDTO数组的辅助函数
     * @param users 用户类型列表
     * @return DTO列表
     */
    public static List<UserDTO> arrayOf(List<User> users) {
        var result = new ArrayList<UserDTO>();

        for(var user : users) {
            result.add(new UserDTO(user));
        }

        return result;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }
}
