package top.rrricardo.postcalendarbackend.components;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import top.rrricardo.postcalendarbackend.annotations.Authorize;
import top.rrricardo.postcalendarbackend.dtos.ResponseDTO;
import top.rrricardo.postcalendarbackend.dtos.UserDTO;
import top.rrricardo.postcalendarbackend.enums.UserPermission;
import top.rrricardo.postcalendarbackend.services.JwtService;

import java.io.IOException;

@Component
public class AuthorizeInterceptor implements HandlerInterceptor {
    private final JwtService jwtService;
    private final Logger logger;

    public AuthorizeInterceptor(JwtService jwtService) {
        this.jwtService = jwtService;
        this.logger = LoggerFactory.getLogger(AuthorizeInterceptor.class);
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
            returnNoAuthorized(response);

            return false;
        }

        try {
            var claims = jwtService.parseJwtToken(tokenHeader);
            var permission = UserPermission.valueOf(claims.get("permission", String.class));
            var policy = authorize.policy();

            switch (policy) {
                case ONLY_LOGIN -> {
                    return true;
                }
                case CURRENT_USER -> {
                    if (permission.getCode() == UserPermission.USER.getCode()) {
                        // 获得请求URL的结尾id
                        var array = request.getRequestURI().split("/");
                        try {
                            var id = Integer.parseInt(array[array.length - 1]);
                            logger.info("Get User ID: " + id);

                            if (id == claims.get("userId", Integer.class)) {
                                return true;
                            } else {
                                returnForbidden(response, "No permission");
                                return false;
                            }
                        } catch (NumberFormatException e) {
                            logger.error("Failed to apply CURRENT_USER policy on request: " + request.getRequestURI());

                            returnInternalError(response);
                            return false;
                        }
                    } else {
                        return true;
                    }
                }
                case ABOVE_ADMINISTRATOR -> {
                    if (permission.getCode() >= UserPermission.ADMIN.getCode()) {
                        return true;
                    } else {
                        returnForbidden(response, "No permission");
                        return false;
                    }
                }
                case ABOVE_SUPERMAN -> {
                    if (permission.getCode() >= UserPermission.SUPER.getCode()) {
                        return true;
                    } else {
                        returnForbidden(response, "No permission");
                        return false;
                    }
                }
                default -> {
                    returnForbidden(response, "NO permission");
                    return false;
                }
            }
        } catch (JwtException e) {
            var responseDTO = new ResponseDTO<UserDTO>(e.getMessage());

            response.setStatus(401);
            response.getWriter().println(responseDTO);

            return false;
        }
    }

    /**
     * 返回401没有授权响应
     *
     * @throws IOException getWriter 返回的错误
     */
    private void returnNoAuthorized(HttpServletResponse response) throws IOException {
        var responseDTO = new ResponseDTO<UserDTO>("No token provided.");

        response.setStatus(401);
        response.getWriter().println(responseDTO);
    }

    /**
     * 返回403无权限被禁止响应
     * @param message 响应中的消息字段
     * @throws IOException  返回的错误
     */
    private void returnForbidden(HttpServletResponse response, String message) throws IOException {
        var responseDTO = new ResponseDTO<UserDTO>(message);

        response.setStatus(403);
        response.getWriter().println(responseDTO);
    }

    /**
     * 返回500服务器内部错误
     */
    private void returnInternalError(HttpServletResponse response) throws IOException {
        var responseDTO = new ResponseDTO<UserDTO>("No user id found in request URI, it is an internal server error!");

        response.setStatus(500);
        response.getWriter().println(responseDTO);
    }
}
