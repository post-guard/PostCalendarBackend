package top.rrricardo.postcalendarbackend.services.Impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import top.rrricardo.postcalendarbackend.dtos.UserDTO;
import top.rrricardo.postcalendarbackend.enums.UserPermission;
import top.rrricardo.postcalendarbackend.exceptions.NoUserIdException;
import top.rrricardo.postcalendarbackend.mappers.OrganizationLinkMapper;
import top.rrricardo.postcalendarbackend.services.AuthorizeService;
import top.rrricardo.postcalendarbackend.utils.Common;

@Service("currentUser")
public class CurrentUserAuthorizeService implements AuthorizeService {
    private final OrganizationLinkMapper organizationLinkMapper;
    private final Logger logger;

    public CurrentUserAuthorizeService(OrganizationLinkMapper organizationLinkMapper) {
        this.organizationLinkMapper = organizationLinkMapper;
        this.logger = LoggerFactory.getLogger(CurrentUserAuthorizeService.class);
    }

    @Override
    public boolean authorize(UserDTO user, String requestUri) throws NoUserIdException {
        var link = organizationLinkMapper.getOrganizationLinkByUserIdAndOrganizationId(user.getId(),
                Common.DefaultUsersOrganizationId);

        if (link.getPermissionEnum().getCode() >= UserPermission.ADMIN.getCode()) {
            // 如果请求者是管理员
            return true;
        }

        var array = requestUri.split("/");
        try {
            var id = Integer.parseInt(array[array.length - 1]);

            return id == user.getId();
        } catch (NumberFormatException e) {
            logger.error("Failed to get id in uri: " + requestUri);

            throw new NoUserIdException(e.getMessage());
        }
    }
}
