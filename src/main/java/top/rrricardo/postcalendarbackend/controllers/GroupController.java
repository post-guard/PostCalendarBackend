package top.rrricardo.postcalendarbackend.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import top.rrricardo.postcalendarbackend.annotations.Authorize;
import top.rrricardo.postcalendarbackend.dtos.ResponseDTO;
import top.rrricardo.postcalendarbackend.dtos.UserDTO;
import top.rrricardo.postcalendarbackend.enums.AuthorizePolicy;
import top.rrricardo.postcalendarbackend.enums.UserPermission;
import top.rrricardo.postcalendarbackend.mappers.GroupLinkMapper;
import top.rrricardo.postcalendarbackend.mappers.GroupMapper;
import top.rrricardo.postcalendarbackend.models.Group;
import top.rrricardo.postcalendarbackend.models.GroupLink;
import top.rrricardo.postcalendarbackend.commons.Common;
import top.rrricardo.postcalendarbackend.utils.ControllerBase;
import top.rrricardo.postcalendarbackend.components.AuthorizeInterceptor;
import java.util.List;

@RestController
@RequestMapping("/group")
public class GroupController extends ControllerBase {


    private final GroupMapper groupMapper;
    private final GroupLinkMapper groupLinkMapper;
    private final Logger logger;
    public GroupController(GroupMapper groupMapper, GroupLinkMapper groupLinkMapper) {
        this.groupMapper = groupMapper;
        this.groupLinkMapper = groupLinkMapper;
        this.logger = LoggerFactory.getLogger(GroupController.class);
    }

    @GetMapping("/")
    @Authorize(policy = AuthorizePolicy.ONLY_LOGIN)
    public ResponseEntity<ResponseDTO<List<Group>>> getGroups(){
        var groups = groupMapper.getGroups();

        logger.info("成功获取全部组织列表");
        return ok(groups);
    }

    @GetMapping("/{id}")
    @Authorize(policy = AuthorizePolicy.ONLY_LOGIN)
    public ResponseEntity<ResponseDTO<Group>> getGroup(@PathVariable(value = "id") int id) {
        var group = groupMapper.getGroupById(id);

        if (group == null) {
            logger.info("获取组织失败，不存在id={}的组织", id);
            return notFound("组织不存在");
        }

        logger.info("获取组织成功，组织id={}", id);
        return ok(group);
    }

    @PostMapping("/")
    @Authorize(policy = AuthorizePolicy.ONLY_LOGIN)
    public ResponseEntity<ResponseDTO<Group>> createGroup(@RequestBody Group group){

        groupMapper.createGroup(group);

        //创建好组织后，将创建者以超级管理员身份加入组织
        UserDTO userDTO = AuthorizeInterceptor.getUserDTO();
        var link = new GroupLink(userDTO.getId(), group.getId(), UserPermission.SUPER);
        groupLinkMapper.createGroupLink(link);

        logger.info("创建组织成功，组织id={}", group.getId());
        return created(group);
    }

    @PutMapping("/{id}")
    @Authorize(policy = AuthorizePolicy.CURRENT_GROUP_ADMINISTRATOR)
    public ResponseEntity<ResponseDTO<Group>> updateGroup
            (@PathVariable (value = "id") int id, @RequestBody Group group) throws NullPointerException{

        if (id != group.getId()) {
            logger.info("更新组织信息失败，id不一致");
            return badRequest();
        }

        var oldGroup = groupMapper.getGroupById(id);
        if(oldGroup == null){
            //组织不存在
            logger.info("更新组织信息失败，不存在id={}的组织", id);
            return notFound("组织不存在");
        }

        groupMapper.updateGroup(group);

        var newGroup = groupMapper.getGroupById(id);

        if(newGroup == null){
            throw new NullPointerException();
        }

        logger.info("更新组织信息成功，组织id={}", id);
        return ok(group);
    }

    @DeleteMapping("/{id}")
    @Authorize(policy = AuthorizePolicy.CURRENT_GROUP_ADMINISTRATOR)
    public ResponseEntity<ResponseDTO<Group>> deleteGroup(@PathVariable(value = "id") int id){

        var group = groupMapper.getGroupById(id);

        if (group == null) {
            logger.info("删除组织失败，不存在id={}的组织", id);
            return notFound("组织不存在");
        }

        if (id == Common.DefaultUsersGroupId) {
            logger.info("删除失败，禁止删除默认组织");
            return badRequest("禁止删除默认组织");
        }

        groupMapper.deleteGroup(id);
        var groupLinks = groupLinkMapper.getGroupLinksByGroupId(id);
        for (var groupLink : groupLinks) {
            groupLinkMapper.deleteGroupLink(groupLink.getId());
        }

        logger.info("删除成功，被删除的组织id={}", id);
        return noContent();
    }
}
