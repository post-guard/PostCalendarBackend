package top.rrricardo.postcalendarbackend.components;

import org.springframework.stereotype.Component;
import top.rrricardo.postcalendarbackend.enums.AuthorizePolicy;
import top.rrricardo.postcalendarbackend.services.AuthorizeService;

import java.util.Map;

@Component
public class AuthorizeServiceFactory {
    Map<String, AuthorizeService> authorizeServiceMap;

    public AuthorizeServiceFactory(Map<String, AuthorizeService> authorizeServiceMap) {
        this.authorizeServiceMap = authorizeServiceMap;
    }

    public AuthorizeService getAuthorizeService(AuthorizePolicy policy) {
        return authorizeServiceMap.get(policy.getImplementName());
    }
}
