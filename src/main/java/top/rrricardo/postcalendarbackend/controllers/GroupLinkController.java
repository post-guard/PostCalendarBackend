package top.rrricardo.postcalendarbackend.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import top.rrricardo.postcalendarbackend.annotations.Authorize;
import top.rrricardo.postcalendarbackend.components.AuthorizeInterceptor;
import top.rrricardo.postcalendarbackend.dtos.ResponseDTO;
import top.rrricardo.postcalendarbackend.enums.AuthorizePolicy;
import top.rrricardo.postcalendarbackend.enums.UserPermission;
import top.rrricardo.postcalendarbackend.mappers.GroupLinkMapper;
import top.rrricardo.postcalendarbackend.mappers.GroupMapper;
import top.rrricardo.postcalendarbackend.mappers.UserMapper;
import top.rrricardo.postcalendarbackend.models.GroupLink;
import top.rrricardo.postcalendarbackend.commons.Common;
import top.rrricardo.postcalendarbackend.utils.ControllerBase;

import java.util.List;

@RestController
@RequestMapping("/groupLink")
public class GroupLinkController extends ControllerBase {
    private final UserMapper userMapper;
    private final GroupMapper groupMapper;
    private final GroupLinkMapper groupLinkMapper;
    private final Logger logger;
    public GroupLinkController(
            UserMapper userMapper,
            GroupMapper groupMapper,
            GroupLinkMapper groupLinkMapper
    ) {
        this.userMapper = userMapper;
        this.groupMapper = groupMapper;
        this.groupLinkMapper = groupLinkMapper;
        this.logger = LoggerFactory.getLogger(GroupLinkController.class);
    }

    @GetMapping("/group/{id}")
    @Authorize(policy = AuthorizePolicy.CURRENT_GROUP_USER)
    public ResponseEntity<ResponseDTO<List<GroupLink>>> getUsersInGroup(@PathVariable(value = "id") int id) {
        var group = groupMapper.getGroupById(id);

        if (group == null) {
            logger.info("获取组织中的用户失败，不存在id={}的组织", id);
            return badRequest("查询的组织不存在");
        }

        var links = groupLinkMapper.getGroupLinksByGroupId(id);

        logger.info("获取组织中的用户成功，组织id={}", id);
        return ok(links);
    }

    @GetMapping("/user/{id}")
    @Authorize(policy = AuthorizePolicy.CURRENT_USER)
    public ResponseEntity<ResponseDTO<List<GroupLink>>> getGroupsOfUser(@PathVariable(value = "id") int id) {
        var user = userMapper.getUserById(id);

        if (user == null) {
            logger.info("查询用户组织失败，不存在id={}的用户", id);
            return badRequest("查询的用户不存在");
        }

        var links = groupLinkMapper.getGroupLinksByUserId(id);

        logger.info("查询用户组织成功, 用户id={}", id);
        return ok(links);
    }

    @PostMapping("/group/{id}")
    @Authorize(policy = AuthorizePolicy.CURRENT_GROUP_ADMINISTRATOR)
    public ResponseEntity<ResponseDTO<GroupLink>> addUserToGroup(@PathVariable(value = "id") int id,
                                                                     @RequestBody GroupLink groupLink) {
        // 有权限控制的情况下
        // 判断组织是否存在是不必要的
        if (id != groupLink.getGroupId()) {
            logger.info("添加用户失败，请求的组织id不一致");
            return badRequest("请求的组织id不一致");
        }

        if (id == Common.DefaultUsersGroupId) {
            logger.info("添加失败，禁止向默认组织中添加用户");
            return badRequest("禁止向默认组织中添加用户");
        }

        // 获得请求人的权限
        var client = AuthorizeInterceptor.getUserDTO();
        var clientLink = groupLinkMapper.getGroupLinkByUserIdAndGroupId(client.getId(), id);

        var link = groupLinkMapper.getGroupLinkByUserIdAndGroupId(groupLink.getUserId(), groupLink.getGroupId());

        if (link != null) {
            if (groupLink.getPermissionEnum() != link.getPermissionEnum()) {
                if (clientLink.getPermissionEnum() != UserPermission.SUPER) {
                    logger.info("没有权限修改用户的权限");
                    return forbidden("没有权限修改用户的权限");
                } else {
                    link.setPermissionEnum(groupLink.getPermissionEnum());
                    groupLinkMapper.updateGroupLink(link);
                    logger.info("用户权限修改成功");
                    return created("用户权限修改成功", link);
                }
            } else {
                logger.info("添加失败，用户已存在");
                return badRequest("用户已经存在");
            }
        } else {
            groupLinkMapper.createGroupLink(groupLink);

            logger.info("添加用户到组织成功");
            return created("添加用户到组织成功", groupLink);
        }
    }

    @PutMapping("/group/{id}")
    @Authorize(policy = AuthorizePolicy.CURRENT_GROUP_ADMINISTRATOR)
    public ResponseEntity<ResponseDTO<GroupLink>> updateGroupLink(@PathVariable(value = "id") int id,
                                                                  @RequestBody GroupLink groupLink) {
        if (id != groupLink.getGroupId()) {
            logger.info("修改组织中用户权限失败，请求的组织id不一致");
            return badRequest("请求的组织ID不一致");
        }

        var client = AuthorizeInterceptor.getUserDTO();
        var clientLink = groupLinkMapper.getGroupLinkByUserIdAndGroupId(client.getId(), id);

        var link = groupLinkMapper.getGroupLinkById(groupLink.getId());

        if (link == null) {
            logger.info("修改组织中用户权限失败，指定的用户不在该组织中");
            return notFound("指定的用户不在组织中");
        }

        if (link.getGroupId() != groupLink.getGroupId() || link.getUserId() != groupLink.getUserId()) {
            logger.info("修改组织中用户权限失败，禁止修改用户和组织");
            return badRequest("禁止修改用户和组织");
        }

        if (link.getPermissionEnum().getCode() > clientLink.getPermissionEnum().getCode()) {
            logger.info("修改组织中用户权限失败，不能修改权限比自己高的用户");
            return forbidden("不能修改权限比自己高的用户", link);
        } else {
            groupLinkMapper.updateGroupLink(groupLink);
            logger.info("修改组织中的用户权限成功");
            return ok(groupLink);
        }
    }

    @DeleteMapping("/group/{id}")
    @Authorize(policy = AuthorizePolicy.CURRENT_GROUP_ADMINISTRATOR)
    public ResponseEntity<ResponseDTO<GroupLink>> deleteGroupLink(@PathVariable(value = "id") int id,
                                                                  @RequestBody GroupLink groupLink) {
        if (id != groupLink.getGroupId()) {
            logger.info("删除指定组织中的用户失败，请求的组织id不一致");
            return badRequest("请求的组织ID不一致");
        }

        if (id == Common.DefaultUsersGroupId) {
            logger.info("删除指定组织中的用户失败，默认组织中的用户禁止删除");
            return badRequest("默认组织中的用户禁止删除");
        }

        // 获得请求用户的权限
        var client = AuthorizeInterceptor.getUserDTO();
        var clientLink = groupLinkMapper.getGroupLinkByUserIdAndGroupId(client.getId(), id);

        var link = groupLinkMapper.getGroupLinkByUserIdAndGroupId(groupLink.getUserId(), groupLink.getGroupId());

        if (link == null) {
            logger.info("删除指定组织中的用户失败，指定的用户不在组织中");
            return notFound("指定的用户不在组织中");
        }

        if (link.getPermissionEnum().getCode() > clientLink.getPermissionEnum().getCode()) {
            logger.info("删除指定组织中的用户失败，不能删除权限比自己高的用户");
            return forbidden("不能删除权限比自己高的用户", link);
        } else {
            groupLinkMapper.deleteGroupLink(link.getId());

            logger.info("成功删除指定组织中的用户");
            return noContent();
        }
    }
}
