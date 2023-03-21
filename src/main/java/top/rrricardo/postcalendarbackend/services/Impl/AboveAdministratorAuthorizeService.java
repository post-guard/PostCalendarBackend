package top.rrricardo.postcalendarbackend.services.Impl;

import org.springframework.stereotype.Service;
import top.rrricardo.postcalendarbackend.dtos.UserDTO;
import top.rrricardo.postcalendarbackend.enums.UserPermission;
import top.rrricardo.postcalendarbackend.mappers.GroupLinkMapper;
import top.rrricardo.postcalendarbackend.services.AuthorizeService;
import top.rrricardo.postcalendarbackend.utils.Common;

/**
 * 需要用户权限大于等于管理员的权限校验
 */
@Service("aboveAdministrator")
public class AboveAdministratorAuthorizeService implements AuthorizeService {
    private final GroupLinkMapper groupLinkMapper;

    public AboveAdministratorAuthorizeService(GroupLinkMapper groupLinkMapper) {
        this.groupLinkMapper = groupLinkMapper;
    }

    @Override
    public boolean authorize(UserDTO user, String requestUri) {
        var link = groupLinkMapper.getGroupLinkByUserIdAndGroupId(user.getId(),
                Common.DefaultUsersGroupId);

        return link.getPermissionEnum().getCode() >= UserPermission.ADMIN.getCode();
    }
}
