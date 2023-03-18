package top.rrricardo.postcalendarbackend.services.Impl;

import org.springframework.stereotype.Service;
import top.rrricardo.postcalendarbackend.dtos.UserDTO;
import top.rrricardo.postcalendarbackend.enums.UserPermission;
import top.rrricardo.postcalendarbackend.mappers.OrganizationLinkMapper;
import top.rrricardo.postcalendarbackend.services.AuthorizeService;

/**
 * 需要用户权限大于等于管理员的权限校验
 */
@Service("aboveAdministrator")
public class AboveAdministratorAuthorizeService implements AuthorizeService {
    private final OrganizationLinkMapper organizationLinkMapper;

    public AboveAdministratorAuthorizeService(OrganizationLinkMapper organizationLinkMapper) {
        this.organizationLinkMapper = organizationLinkMapper;
    }

    @Override
    public boolean authorize(UserDTO user, String requestUri) {
        var link = organizationLinkMapper.getOrganizationLinkByUserIdAndOrganizationId(user.getId(), 1);

        return link.getPermission().getCode() >= UserPermission.ADMIN.getCode();
    }
}
