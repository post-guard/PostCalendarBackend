package top.rrricardo.postcalendarbackend.services;

import top.rrricardo.postcalendarbackend.dtos.UserDTO;
import top.rrricardo.postcalendarbackend.exceptions.NoUserIdException;
import top.rrricardo.postcalendarbackend.models.User;

public interface AuthorizeService {
    /**
     * 验证用户的权限
     * @param user 用户DTO模型
     * @param requestUri 请求的URI
     * @return 是否通过拥有权限
     */
    boolean authorize(UserDTO user, String requestUri) throws NoUserIdException;
}
