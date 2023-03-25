package top.rrricardo.postcalendarbackend.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import top.rrricardo.postcalendarbackend.annotations.Authorize;
import top.rrricardo.postcalendarbackend.components.AuthorizeInterceptor;
import top.rrricardo.postcalendarbackend.dtos.ResponseDTO;
import top.rrricardo.postcalendarbackend.dtos.UserDTO;
import top.rrricardo.postcalendarbackend.enums.AuthorizePolicy;
import top.rrricardo.postcalendarbackend.enums.UserPermission;
import top.rrricardo.postcalendarbackend.mappers.GroupLinkMapper;
import top.rrricardo.postcalendarbackend.mappers.GroupMapper;
import top.rrricardo.postcalendarbackend.mappers.UserMapper;
import top.rrricardo.postcalendarbackend.models.Group;
import top.rrricardo.postcalendarbackend.models.GroupLink;
import top.rrricardo.postcalendarbackend.utils.ControllerBase;

import java.util.List;

@RestController
@RequestMapping("/groupLink")
public class GroupLinkController extends ControllerBase {
    private final UserMapper userMapper;
    private final GroupMapper groupMapper;
    private final GroupLinkMapper groupLinkMapper;

    public GroupLinkController(
            UserMapper userMapper,
            GroupMapper groupMapper,
            GroupLinkMapper groupLinkMapper
    ) {
        this.userMapper = userMapper;
        this.groupMapper = groupMapper;
        this.groupLinkMapper = groupLinkMapper;
    }

    @GetMapping("/group/{id}")
    @Authorize(policy = AuthorizePolicy.CURRENT_GROUP_USER)
    public ResponseEntity<ResponseDTO<List<UserDTO>>> getUsersInGroup(@PathVariable(value = "id") int id) {
        var group = groupMapper.getGroupById(id);

        if (group == null) {
            return badRequest("查询的组织不存在");
        }

        var users = groupLinkMapper.getGroupLinksByGroupId(id);

        return ok(UserDTO.arrayOf(users));
    }

    @PostMapping("/group/{id}")
    @Authorize(policy = AuthorizePolicy.CURRENT_GROUP_ADMINISTRATOR)
    public ResponseEntity<ResponseDTO<GroupLink>> addUserToGroup(@PathVariable(value = "id") int id,
                                                                     @RequestBody GroupLink groupLink) {
        // 有权限控制的情况下
        // 判断组织是否存在是不必要的
        if (id != groupLink.getGroupId()) {
            return badRequest("请求的组织id不一致");
        }

        // 获得请求人的权限
        var client = AuthorizeInterceptor.getUserDTO();
        var clientLink = groupLinkMapper.getGroupLinkByUserIdAndGroupId(client.getId(), id);

        var link = groupLinkMapper.getGroupLinkByUserIdAndGroupId(groupLink.getUserId(), groupLink.getGroupId());

        if (link != null) {
            if (groupLink.getPermissionEnum() != link.getPermissionEnum()) {
                if (clientLink.getPermissionEnum() != UserPermission.SUPER) {
                    return forbidden("没有权限修改用户的权限");
                } else {
                    link.setPermissionEnum(groupLink.getPermissionEnum());
                    groupLinkMapper.updateGroupLink(link);
                    return ok("用户权限修改成功", link);
                }
            } else {
                return badRequest("用户已经存在");
            }
        } else {
            groupLinkMapper.createGroupLink(groupLink);

            return ok("创建用户成功", groupLink);
        }
    }

    @DeleteMapping("/group/{id}")
    @Authorize(policy = AuthorizePolicy.CURRENT_GROUP_ADMINISTRATOR)
    public ResponseEntity<ResponseDTO<GroupLink>> deleteGroupLink(@PathVariable(value = "id") int id,
                                                                  @RequestBody GroupLink groupLink) {
        if (id != groupLink.getGroupId()) {
            return badRequest("请求的组织ID不一致");
        }

        // 获得请求用户的权限
        var client = AuthorizeInterceptor.getUserDTO();
        var clientLink = groupLinkMapper.getGroupLinkByUserIdAndGroupId(client.getId(), id);

        var link = groupLinkMapper.getGroupLinkByUserIdAndGroupId(groupLink.getUserId(), groupLink.getGroupId());

        if (link == null) {
            return notFound("指定的用户不在组织中");
        }

        if (link.getPermissionEnum().getCode() > clientLink.getPermissionEnum().getCode()) {
            return forbidden("不能删除权限比自己高的用户", link);
        } else {
            groupLinkMapper.deleteGroupLink(link.getId());
            return noContent();
        }
    }

    @GetMapping("/user/{id}")
    @Authorize(policy = AuthorizePolicy.CURRENT_USER)
    public ResponseEntity<ResponseDTO<List<Group>>> getGroupsOfUser(@PathVariable(value = "id") int id) {
        var user = userMapper.getUserById(id);

        if (user == null) {
            return badRequest("查询的用户不存在");
        }

        var groups = groupLinkMapper.getGroupLinksByUserId(id);

        return ok(groups);
    }
}
