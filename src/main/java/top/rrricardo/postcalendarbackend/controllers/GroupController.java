package top.rrricardo.postcalendarbackend.controllers;

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
import top.rrricardo.postcalendarbackend.utils.ControllerBase;
import top.rrricardo.postcalendarbackend.components.AuthorizeInterceptor;
import java.util.List;

@RestController
@RequestMapping("/group")
public class GroupController extends ControllerBase {


    private final GroupMapper groupMapper;
    private final GroupLinkMapper groupLinkMapper;


    public GroupController(GroupMapper groupMapper, GroupLinkMapper groupLinkMapper) {
        this.groupMapper = groupMapper;
        this.groupLinkMapper = groupLinkMapper;
    }

    @GetMapping("/")
    @Authorize(policy = AuthorizePolicy.ONLY_LOGIN)
    public ResponseEntity<ResponseDTO<List<Group>>> getGroups(){
        var groups = groupMapper.getGroups();

        return ok(groups);
    }

    @GetMapping("/{id}")
    @Authorize(policy = AuthorizePolicy.ONLY_LOGIN)
    public ResponseEntity<ResponseDTO<Group>> getGroup(@PathVariable(value = "id") int id) {
        var group = groupMapper.getGroupById(id);

        if (group == null) {
            return notFound("组织不存在");
        }

        return ok(group);
    }

    @PostMapping("/")
    @Authorize(policy = AuthorizePolicy.ONLY_LOGIN)
    public ResponseEntity<ResponseDTO<Group>> createGroup(@RequestBody Group group){

        groupMapper.createGroup(group);

        //创建好组织后，将创建者以管理员身份加入组织
        UserDTO userDTO = AuthorizeInterceptor.getUserDTO();
        var link = new GroupLink(userDTO.getId(), group.getId(), UserPermission.ADMIN);
        groupLinkMapper.createGroupLink(link);

        return created();

    }

    @PutMapping("/{id}")
    @Authorize(policy = AuthorizePolicy.CURRENT_GROUP_ADMINISTRATOR)
    public ResponseEntity<ResponseDTO<Group>> updateGroup
            (@PathVariable (value = "id") int id, @RequestBody Group group) throws NullPointerException{

        if (id != group.getId()) {
            return badRequest();
        }

        var oldGroup = groupMapper.getGroupById(id);
        if(oldGroup == null){
            //组织不存在
            return notFound("组织不存在");
        }

        groupMapper.updateGroup(group);

        var newGroup = groupMapper.getGroupById(id);

        if(newGroup == null){
            throw new NullPointerException();
        }

        return ok(group);
    }

    @DeleteMapping("/{id}")
    @Authorize(policy = AuthorizePolicy.CURRENT_GROUP_ADMINISTRATOR)
    public ResponseEntity<ResponseDTO<Group>> deleteGroup(@PathVariable(value = "id") int id){

        var group = groupMapper.getGroupById(id);

        if (group == null) {
            return notFound("组织不存在");
        }

        groupMapper.deleteGroup(id);

        return noContent();
    }
}
