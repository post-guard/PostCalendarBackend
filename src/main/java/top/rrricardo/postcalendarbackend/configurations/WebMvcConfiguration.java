package top.rrricardo.postcalendarbackend.configurations;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import top.rrricardo.postcalendarbackend.components.AuthorizeInterceptor;
import top.rrricardo.postcalendarbackend.services.JwtService;

@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {
    private final JwtService jwtService;

    public WebMvcConfiguration(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AuthorizeInterceptor(jwtService));
    }
}
