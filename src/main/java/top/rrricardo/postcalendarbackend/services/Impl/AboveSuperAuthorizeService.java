package top.rrricardo.postcalendarbackend.services.Impl;

import org.springframework.stereotype.Service;
import top.rrricardo.postcalendarbackend.dtos.UserDTO;
import top.rrricardo.postcalendarbackend.enums.UserPermission;
import top.rrricardo.postcalendarbackend.mappers.OrganizationLinkMapper;
import top.rrricardo.postcalendarbackend.services.AuthorizeService;

/**
 * 需要用户权限大于超级管理员的权限校验
 */
@Service("aboveSuperman")
public class AboveSuperAuthorizeService implements AuthorizeService {
    private final OrganizationLinkMapper organizationLinkMapper;

    public AboveSuperAuthorizeService(OrganizationLinkMapper organizationLinkMapper) {
        this.organizationLinkMapper = organizationLinkMapper;
    }

    @Override
    public boolean authorize(UserDTO user, String requestUri) {
        var link = organizationLinkMapper.getOrganizationLinkByUserIdAndOrganizationId(user.getId(), 1);

        return link.getPermission().getCode() >= UserPermission.SUPER.getCode();
    }
}
