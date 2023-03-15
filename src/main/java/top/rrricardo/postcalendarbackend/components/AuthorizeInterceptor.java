package top.rrricardo.postcalendarbackend.components;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import top.rrricardo.postcalendarbackend.annotations.Authorize;
import top.rrricardo.postcalendarbackend.dtos.ResponseDTO;
import top.rrricardo.postcalendarbackend.dtos.UserDTO;
import top.rrricardo.postcalendarbackend.enums.UserPermission;
import top.rrricardo.postcalendarbackend.services.JwtService;

@Component
public class AuthorizeInterceptor implements HandlerInterceptor {
    private final JwtService jwtService;

    public AuthorizeInterceptor(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        var method = handlerMethod.getMethod();
        var authorize = method.getAnnotation(Authorize.class);
        if (authorize == null) {
            // 没有使用注解
            // 说明不需要身份验证
            return true;
        }

        // 验证是否携带令牌
        var tokenHeader = request.getHeader(jwtService.header);
        if (tokenHeader == null || !tokenHeader.startsWith(jwtService.tokenPrefix)) {
            var responseDTO = new ResponseDTO<UserDTO>("没有携带令牌");

            response.setStatus(401);
            response.getWriter().println(responseDTO);

            return false;
        }

        try {
            var claims = jwtService.parseJwtToken(tokenHeader);
            var permission = claims.get("permission", UserPermission.class);

            if (permission.getCode() > authorize.permission().getCode()) {
                return true;
            } else {
                var responseDTO = new ResponseDTO<UserDTO>("没有对应的权限");

                response.setStatus(403);
                response.getWriter().println(responseDTO);

                return false;
            }


        } catch (JwtException e) {
            var responseDTO = new ResponseDTO<UserDTO>(e.getMessage());

            response.setStatus(401);
            response.getWriter().println(responseDTO);

            return false;
        }
    }
}
