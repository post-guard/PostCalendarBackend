package top.rrricardo.postcalendarbackend.services;

import io.jsonwebtoken.Claims;
import top.rrricardo.postcalendarbackend.models.User;

public interface JwtService {

    /**
     * 令牌HTTP请求头
     */
    public String header = "Authorization";

    /**
     * 令牌前缀
     */
    public String tokenPrefix = "Bearer ";

    /**
     * 生成jwt令牌
     * @param user 用户模型
     * @return jwt令牌
     */
    String generateJwtToken(User user);

    /**
     * 获得jwt令牌中的信息
     * @param token jwt令牌
     * @return 信息Map
     */
    Claims parseJwtToken(String token);
}
