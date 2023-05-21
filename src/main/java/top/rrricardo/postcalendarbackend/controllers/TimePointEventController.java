package top.rrricardo.postcalendarbackend.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import top.rrricardo.postcalendarbackend.annotations.Authorize;
import top.rrricardo.postcalendarbackend.dtos.ResponseDTO;
import top.rrricardo.postcalendarbackend.enums.AuthorizePolicy;
import top.rrricardo.postcalendarbackend.exceptions.TimePointEventException;
import top.rrricardo.postcalendarbackend.mappers.GroupLinkMapper;
import top.rrricardo.postcalendarbackend.mappers.GroupMapper;
import top.rrricardo.postcalendarbackend.mappers.TimePointEventMapper;
import top.rrricardo.postcalendarbackend.mappers.UserMapper;
import top.rrricardo.postcalendarbackend.models.TimePointEvent;
import top.rrricardo.postcalendarbackend.services.TimePointEventService;
import top.rrricardo.postcalendarbackend.utils.ControllerBase;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("timePointEvent")
public class TimePointEventController extends ControllerBase {
    private final UserMapper userMapper;
    private final GroupMapper groupMapper;
    private final GroupLinkMapper groupLinkMapper;
    private final TimePointEventMapper eventMapper;
    private final TimePointEventService userTimePointEventService;
    private final TimePointEventService groupTimePointEventService;

    public TimePointEventController(
            UserMapper userMapper,
            GroupMapper groupMapper,
            GroupLinkMapper groupLinkMapper,
            TimePointEventMapper eventMapper,
            Map<String, TimePointEventService> serviceMap
    ) {
       this.userMapper = userMapper;
       this.groupMapper = groupMapper;
       this.groupLinkMapper = groupLinkMapper;
       this.eventMapper = eventMapper;
       this.userTimePointEventService = serviceMap.get("userTimePointEvent");
       this.groupTimePointEventService = serviceMap.get("groupTimePointEvent");
    }

    @GetMapping("/user/{id}")
    @Authorize(policy = AuthorizePolicy.CURRENT_USER)
    public ResponseEntity<ResponseDTO<List<TimePointEvent>>> getUserEvents(
            @PathVariable int id,
            @RequestParam long begin,
            @RequestParam long end
    ) {
        if (begin >= end) {
            return badRequest("开始时间晚于结束时间");
        }

        var user = userMapper.getUserById(id);
        if (user == null) {
            return notFound("用户不存在");
        }

        var instant = Instant.ofEpochSecond(begin);
        var beginTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());

        instant = Instant.ofEpochSecond(end);
        var endTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());

        try {
            var result = userTimePointEventService.queryEvent(id, beginTime, endTime);
            // 查询当前用户所属组织中的事件
            var groupLinks = groupLinkMapper.getGroupLinksByUserId(id);

            for (var groupLink : groupLinks) {
                var eventsInGroup = groupTimePointEventService.queryEvent(groupLink.getGroupId(), beginTime, endTime);

                for (var item : eventsInGroup) {
                    result.add(item);
                }
            }

            return ok(result.toList());
        } catch (TimePointEventException e) {
            return badRequest(e.getMessage());
        }
    }

    @PostMapping("/user/{id}")
    @Authorize(policy = AuthorizePolicy.CURRENT_USER)
    public ResponseEntity<ResponseDTO<TimePointEvent>> createUserEvent(
            @PathVariable int id,
            @RequestBody TimePointEvent event
    ) {
        if (id != event.getUserId()) {
            return badRequest("请求的用户ID和事件用户ID不一致");
        }

        var user = userMapper.getUserById(id);
        if (user == null) {
            return notFound("用户不存在");
        }

        try {
            userTimePointEventService.addEvent(event);

            return created(event);
        } catch (TimePointEventException e) {
            return badRequest(e.getMessage());
        }
    }

    @PutMapping("/user/{id}")
    @Authorize(policy = AuthorizePolicy.CURRENT_USER)
    public ResponseEntity<ResponseDTO<TimePointEvent>> updateUserEvent(
            @PathVariable int id,
            @RequestBody TimePointEvent event
    ) {
        var user = userMapper.getUserById(id);

        if (user == null) {
            return notFound("用户不存在");
        }

        var oldEvent = eventMapper.getEventById(event.getId());
        if (oldEvent == null) {
            return notFound("事件不存在");
        }

        try {
            userTimePointEventService.updateEvent(event);

            return ok(event);
        } catch (TimePointEventException e) {
            return badRequest(e.getMessage());
        }
    }

    @DeleteMapping("/user/{id}")
    @Authorize(policy = AuthorizePolicy.CURRENT_USER)
    public ResponseEntity<ResponseDTO<TimePointEvent>> deleteUserEvent(
            @PathVariable int id,
            @RequestBody TimePointEvent event
    ) {
        var user = userMapper.getUserById(id);

        if (user == null) {
            return notFound("用户不存在");
        }

        var oldEvent = eventMapper.getEventById(event.getId());

        if (oldEvent == null) {
            return notFound("要删除的事件不存在");
        }

        if (!event.equals(oldEvent)) {
            return badRequest("要删除的事件信息同数据库不符");
        }

        try {
            userTimePointEventService.removeEvent(event);
            return noContent();
        } catch (TimePointEventException e) {
            return badRequest(e.getMessage());
        }
    }

    @GetMapping("/group/{id}")
    @Authorize(policy = AuthorizePolicy.CURRENT_GROUP_USER)
    public ResponseEntity<ResponseDTO<List<TimePointEvent>>> getGroupEvent(
            @PathVariable int id,
            @RequestParam long begin,
            @RequestParam long end
    ) {
        if (begin >= end) {
            return badRequest("开始时间晚于结束时间");
        }

        var group = groupMapper.getGroupById(id);
        if (group == null) {
            return notFound("组织不存在");
        }

        var instant = Instant.ofEpochSecond(begin);
        var beginTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        instant = Instant.ofEpochSecond(end);
        var endTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());

        try {
            var result = groupTimePointEventService.queryEvent(id, beginTime, endTime);

            return ok(result.toList());
        } catch (TimePointEventException e) {
            return badRequest(e.getMessage());
        }
    }

    @PostMapping("/group/{id}")
    @Authorize(policy = AuthorizePolicy.CURRENT_GROUP_USER)
    public ResponseEntity<ResponseDTO<TimePointEvent>> addGroupEvent(
            @PathVariable int id,
            @RequestBody TimePointEvent event
    ) {
        if (id != event.getGroupId()) {
            return badRequest("请求的组织ID和事件组织ID不一致");
        }

        var group = groupMapper.getGroupById(id);
        if (group == null) {
            return notFound("请求的组织不存在");
        }

        var oldEvent = eventMapper.getEventById(event.getId());
        if (oldEvent == null) {
            return notFound("事件不存在");
        }

        try {
            groupTimePointEventService.addEvent(event);

            return created(event);
        } catch (TimePointEventException e) {
            return badRequest(e.getMessage());
        }
    }

    @PutMapping("/group/{id}")
    @Authorize(policy = AuthorizePolicy.CURRENT_GROUP_USER)
    public ResponseEntity<ResponseDTO<TimePointEvent>> updateGroupEvent(
            @PathVariable int id,
            @RequestBody TimePointEvent event
    ) {
        var group = groupMapper.getGroupById(id);
        if (group == null) {
            return notFound("请求的组织不存在");
        }

        try {
            groupTimePointEventService.updateEvent(event);

            return ok(event);
        } catch (TimePointEventException e) {
            return badRequest(e.getMessage());
        }
    }

    @DeleteMapping("/group/{id}")
    @Authorize(policy = AuthorizePolicy.CURRENT_GROUP_USER)
    public ResponseEntity<ResponseDTO<TimePointEvent>> removeGroupEvent(
            @PathVariable int id,
            @RequestBody TimePointEvent event
    ) {
        var group = groupMapper.getGroupById(id);
        if (group == null) {
            return notFound("请求的用户不存在");
        }

        var oldEvent = eventMapper.getEventById(event.getId());

        if (oldEvent == null) {
            return notFound("要删除的事件不存在");
        }

        if (!event.equals(oldEvent)) {
            return badRequest("要删除的事件信息同数据库不符");
        }

        try {
            groupTimePointEventService.updateEvent(event);
            return noContent();
        } catch (TimePointEventException e) {
            return badRequest(e.getMessage());
        }
    }
}
