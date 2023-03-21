package top.rrricardo.postcalendarbackend.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import top.rrricardo.postcalendarbackend.dtos.ResponseDTO;
import top.rrricardo.postcalendarbackend.mappers.GroupMapper;
import top.rrricardo.postcalendarbackend.models.Group;
import top.rrricardo.postcalendarbackend.utils.ControllerBase;

import java.util.List;

@RestController
@RequestMapping("/group")
public class GroupController extends ControllerBase {


    private final GroupMapper groupMapper;


    public GroupController(GroupMapper groupMapper) {
        this.groupMapper = groupMapper;
    }

    @GetMapping("/")
    public ResponseEntity<ResponseDTO<List<Group>>> getGroups(){
        var groups = groupMapper.getGroups();

        return ok(groups);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDTO<Group>> getGroup(@PathVariable(value = "id") int id) {
        var group = groupMapper.getGroupById(id);

        if (group == null) {
            return notFound("组织不存在");
        }

        return ok(group);
    }

    @PostMapping("/")
    public ResponseEntity<ResponseDTO<Group>> createGroup(@RequestBody Group group){

        groupMapper.createGroup(group);

        return created();

    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseDTO<Group>> updateGroup
            (@PathVariable (value = "id") int id, @RequestBody Group group) throws NullPointerException{

        if (id != group.getId()) {
            return badRequest();
        }

        var oldGroup = getGroup(id);
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
    public ResponseEntity<ResponseDTO<Group>> deleteGroup(@PathVariable(value = "id") int id){

        var group = groupMapper.getGroupById(id);

        if (group == null) {
            return notFound("组织不存在");
        }

        groupMapper.deleteGroup(id);

        return noContent();
    }
}
