package top.rrricardo.postcalendarbackend.services.Impl;

import org.springframework.stereotype.Service;
import top.rrricardo.postcalendarbackend.dtos.UserDTO;
import top.rrricardo.postcalendarbackend.enums.UserPermission;
import top.rrricardo.postcalendarbackend.mappers.GroupLinkMapper;
import top.rrricardo.postcalendarbackend.services.AuthorizeService;
import top.rrricardo.postcalendarbackend.utils.Common;

/**
 * 需要用户权限大于超级管理员的权限校验
 */
@Service("aboveSuperman")
public class AboveSuperAuthorizeService implements AuthorizeService {
    private final GroupLinkMapper groupLinkMapper;

    public AboveSuperAuthorizeService(GroupLinkMapper groupLinkMapper) {
        this.groupLinkMapper = groupLinkMapper;
    }

    @Override
    public boolean authorize(UserDTO user, String requestUri) {
        var link = groupLinkMapper.getGroupLinkByUserIdAndGroupId(user.getId(),
                Common.DefaultUsersGroupId);

        if (link == null) {
            return false;
        }

        return link.getPermissionEnum().getCode() >= UserPermission.SUPER.getCode();
    }
}
