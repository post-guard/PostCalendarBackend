package top.rrricardo.postcalendarbackend.services.Impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import top.rrricardo.postcalendarbackend.dtos.UserDTO;
import top.rrricardo.postcalendarbackend.enums.UserPermission;
import top.rrricardo.postcalendarbackend.exceptions.NoIdInPathException;
import top.rrricardo.postcalendarbackend.mappers.GroupLinkMapper;
import top.rrricardo.postcalendarbackend.services.AuthorizeService;

@Service("currentGroupAdministrator")
public class CurrentGroupAdministratorAuthorizeService implements AuthorizeService {
    private final GroupLinkMapper groupLinkMapper;
    private final Logger logger;

    public CurrentGroupAdministratorAuthorizeService(GroupLinkMapper groupLinkMapper) {
        this.groupLinkMapper = groupLinkMapper;
        this.logger = LoggerFactory.getLogger(CurrentGroupAdministratorAuthorizeService.class);
    }

    @Override
    public boolean authorize(UserDTO user, String requestUri) throws NoIdInPathException {
        try {
            var array = requestUri.split("/");
            var id = Integer.parseInt(array[array.length - 1]);

            var link = groupLinkMapper.getGroupLinkByUserIdAndGroupId(user.getId(), id);

            if (link == null) {
                logger.info("授权失败，该组织中不存在该用户");
                return false;
            } else {
                return link.getPermissionEnum().getCode() >= UserPermission.ADMIN.getCode();
            }
        } catch (NumberFormatException e) {
            logger.error("Failed to get id in uri: " + requestUri);

            throw new NoIdInPathException(e.getMessage());
        }
    }
}
