package top.rrricardo.postcalendarbackend.services.Impl;

import org.springframework.stereotype.Service;
import top.rrricardo.postcalendarbackend.dtos.UserDTO;
import top.rrricardo.postcalendarbackend.services.AuthorizeService;

/**
 * 需要用户登录的权限校验
 */
@Service("onlyLogin")
public class OnlyLoginAuthorizeService implements AuthorizeService {
    @Override
    public boolean authorize(UserDTO user, String requestUri) {
        return true;
    }
}
