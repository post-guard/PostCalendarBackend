package top.rrricardo.postcalendarbackend.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import top.rrricardo.postcalendarbackend.annotations.Authorize;
import top.rrricardo.postcalendarbackend.dtos.ResponseDTO;
import top.rrricardo.postcalendarbackend.enums.AuthorizePolicy;
import top.rrricardo.postcalendarbackend.exceptions.TimeSpanEventException;
import top.rrricardo.postcalendarbackend.mappers.GroupLinkMapper;
import top.rrricardo.postcalendarbackend.mappers.GroupMapper;
import top.rrricardo.postcalendarbackend.mappers.TimeSpanEventMapper;
import top.rrricardo.postcalendarbackend.mappers.UserMapper;
import top.rrricardo.postcalendarbackend.models.TimeSpanEvent;
import top.rrricardo.postcalendarbackend.services.TimeSpanEventService;
import top.rrricardo.postcalendarbackend.utils.ControllerBase;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/timeSpanEvent")
public class TimeSpanEventController extends ControllerBase {
    private final UserMapper userMapper;
    private final GroupMapper groupMapper;
    private final GroupLinkMapper groupLinkMapper;
    private final TimeSpanEventMapper eventMapper;
    private final TimeSpanEventService userTimeSpanEventService;
    private final TimeSpanEventService groupTimeSpanEventService;

    public TimeSpanEventController(TimeSpanEventMapper eventMapper,
                                   UserMapper userMapper,
                                   GroupMapper groupMapper,
                                   GroupLinkMapper groupLinkMapper,
                                   Map<String, TimeSpanEventService> serviceMap) {
        this.userMapper = userMapper;
        this.groupMapper = groupMapper;
        this.groupLinkMapper = groupLinkMapper;
        this.eventMapper = eventMapper;
        this.userTimeSpanEventService = serviceMap.get("userTimeSpanEvent");
        this.groupTimeSpanEventService = serviceMap.get("groupTimeSpanEvent");
    }

    @GetMapping("/user/{id}")
    @Authorize(policy = AuthorizePolicy.CURRENT_USER)
    public ResponseEntity<ResponseDTO<List<TimeSpanEvent>>> getUserEvents(
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
            var result = userTimeSpanEventService.queryEvent(id, beginTime, endTime);
            // 查询当前用户所属组织中的事件
            var groupLinks = groupLinkMapper.getGroupLinksByUserId(id);

            for (var groupLink : groupLinks) {
                var eventsInGroup = groupTimeSpanEventService.queryEvent(groupLink.getGroupId(), beginTime, endTime);

                for (var item : eventsInGroup) {
                    result.add(item);
                }
            }

            return ok(result.toList());
        } catch (TimeSpanEventException e) {
            return badRequest(e.getMessage());
        }
    }

    @PostMapping("/user/{id}")
    @Authorize(policy = AuthorizePolicy.CURRENT_USER)
    public ResponseEntity<ResponseDTO<TimeSpanEvent>> createUserEvent(
            @PathVariable int id,
            @RequestBody TimeSpanEvent event
    ) {
        if (id != event.getUserId()) {
            return badRequest("请求的用户ID和事件用户ID不一致");
        }

        var user = userMapper.getUserById(id);
        if (user == null) {
            return notFound("用户不存在");
        }

        // 判断事件是否冲突
        if (!userTimeSpanEventService.judgeConflict(id, event)) {
            return badRequest("同用户中的事件发生冲突");
        }

        var groupLinks = groupLinkMapper.getGroupLinksByUserId(id);

        for (var groupLink : groupLinks) {
            // 检查是否同用户所在组织的事件冲突
            if (!groupTimeSpanEventService.judgeConflict(groupLink.getGroupId(), event)) {
                return badRequest("同所在组织：" + groupLink.getGroupId() + "的事件冲突");
            }
        }

        try {
            userTimeSpanEventService.addEvent(event);

            return created(event);
        } catch (TimeSpanEventException e) {
            return badRequest(e.getMessage());
        }
    }

    @PutMapping("/user/{id}")
    @Authorize(policy = AuthorizePolicy.CURRENT_USER)
    public ResponseEntity<ResponseDTO<TimeSpanEvent>> updateUserEvent(
            @PathVariable int id,
            @RequestBody TimeSpanEvent event
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
            userTimeSpanEventService.updateEvent(event);

            return ok(event);
        } catch (TimeSpanEventException e) {
            return badRequest(e.getMessage());
        }
    }

    @DeleteMapping("/user/{id}")
    @Authorize(policy = AuthorizePolicy.CURRENT_USER)
    public ResponseEntity<ResponseDTO<TimeSpanEvent>> deleteUserEvent(
            @PathVariable int id,
            @RequestBody TimeSpanEvent event
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
            userTimeSpanEventService.removeEvent(event);
            return noContent();
        } catch (TimeSpanEventException e) {
            return badRequest(e.getMessage());
        }
    }

    @GetMapping("/group/{id}")
    @Authorize(policy = AuthorizePolicy.CURRENT_GROUP_USER)
    public ResponseEntity<ResponseDTO<List<TimeSpanEvent>>> getGroupEvent(
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
            var result = groupTimeSpanEventService.queryEvent(id, beginTime, endTime);

            return ok(result.toList());
        } catch (TimeSpanEventException e) {
            return badRequest(e.getMessage());
        }
    }

    @PostMapping("/group/{id}")
    @Authorize(policy = AuthorizePolicy.CURRENT_GROUP_USER)
    public ResponseEntity<ResponseDTO<TimeSpanEvent>> addGroupEvent(
            @PathVariable int id,
            @RequestBody TimeSpanEvent event
    ) {
        if (id != event.getGroupId()) {
            return badRequest("请求的组织ID和事件组织ID不一致");
        }

        var group = groupMapper.getGroupById(id);
        if (group == null) {
            return notFound("请求的组织不存在");
        }

        // 判断冲突
        if (!groupTimeSpanEventService.judgeConflict(id, event)) {
            return badRequest("同组织中的事件发生冲突");
        }

        var groupLinks = groupLinkMapper.getGroupLinksByGroupId(id);

        for (var groupLink : groupLinks) {
            if (!userTimeSpanEventService.judgeConflict(groupLink.getUserId(), event)) {
                return badRequest("同组织中用户：" + groupLink.getUserId() + "的事件发生冲突");
            }
        }

        try {
            groupTimeSpanEventService.addEvent(event);

            return created(event);
        } catch (TimeSpanEventException e) {
            return badRequest(e.getMessage());
        }
    }

    @PutMapping("/group/{id}")
    @Authorize(policy = AuthorizePolicy.CURRENT_GROUP_USER)
    public ResponseEntity<ResponseDTO<TimeSpanEvent>> updateGroupEvent(
            @PathVariable int id,
            @RequestBody TimeSpanEvent event
    ) {
        var group = groupMapper.getGroupById(id);
        if (group == null) {
            return notFound("请求的组织不存在");
        }

        var oldEvent = eventMapper.getEventById(event.getId());
        if (oldEvent == null) {
            return notFound("事件不存在");
        }

        try {
            groupTimeSpanEventService.updateEvent(event);

            return ok(event);
        } catch (TimeSpanEventException e) {
            return badRequest(e.getMessage());
        }
    }

    @DeleteMapping("/group/{id}")
    @Authorize(policy = AuthorizePolicy.CURRENT_GROUP_USER)
    public ResponseEntity<ResponseDTO<TimeSpanEvent>> removeGroupEvent(
            @PathVariable int id,
            @RequestBody TimeSpanEvent event
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
            groupTimeSpanEventService.removeEvent(event);
            return noContent();
        } catch (TimeSpanEventException e) {
            return badRequest(e.getMessage());
        }
    }


}
