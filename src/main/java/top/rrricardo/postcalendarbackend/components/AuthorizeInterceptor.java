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
import top.rrricardo.postcalendarbackend.exceptions.NoIdInPathException;
import top.rrricardo.postcalendarbackend.services.JwtService;

@Component
public class AuthorizeInterceptor implements HandlerInterceptor {
    private final JwtService jwtService;
    private final AuthorizeServiceFactory authorizeServiceFactory;

    private static final ThreadLocal<UserDTO> local = new ThreadLocal<>();

    public AuthorizeInterceptor(JwtService jwtService, AuthorizeServiceFactory authorizeServiceFactory) {
        this.jwtService = jwtService;
        this.authorizeServiceFactory = authorizeServiceFactory;
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
            var responseDTO = new ResponseDTO<UserDTO>("No token provided.");
            response.setStatus(401);
            response.getWriter().println(responseDTO);

            return false;
        }

        try {
            var claims = jwtService.parseJwtToken(tokenHeader);
            var userDTO = new UserDTO(
                    claims.get("userId", Integer.class),
                    claims.getIssuer(),
                    claims.get("emailAddress", String.class)
            );
            var authService = authorizeServiceFactory.getAuthorizeService(authorize.policy());

            if (authService.authorize(userDTO, request.getRequestURI())) {
                local.set(userDTO);
                return true;
            } else {
                var responseDTO = new ResponseDTO<UserDTO>("No permission");

                response.setStatus(403);
                response.getWriter().println(responseDTO);
                return false;
            }
        } catch (JwtException e) {
            // 解析令牌失败
            var responseDTO = new ResponseDTO<UserDTO>(e.getMessage());

            response.setStatus(401);
            response.getWriter().println(responseDTO);

            return false;
        } catch (NoIdInPathException e) {
            // 在请求路径中没有获取到用户ID
            var responseDTO = new ResponseDTO<UserDTO>("Internal server error, please contact administrator");

            response.setStatus(500);
            response.getWriter().println(responseDTO);

            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        local.remove();
    }

    public static UserDTO getUserDTO() {
        return local.get();
    }
}
