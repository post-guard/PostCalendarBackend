package top.rrricardo.postcalendarbackend.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import top.rrricardo.postcalendarbackend.annotations.Authorize;
import top.rrricardo.postcalendarbackend.dtos.ResponseDTO;
import top.rrricardo.postcalendarbackend.enums.AuthorizePolicy;
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
    private final TimeSpanEventMapper eventMapper;
    private final TimeSpanEventService userTimeSpanEventService;
    private final TimeSpanEventService groupTimeSpanEventService;

    public TimeSpanEventController(TimeSpanEventMapper eventMapper, UserMapper userMapper, Map<String, TimeSpanEventService> serviceMap) {
        this.userMapper = userMapper;
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

            return ok(result.toList());
        } catch (IllegalArgumentException e) {
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

        try {
            userTimeSpanEventService.addEvent(event);

            return created(event);
        } catch (IllegalArgumentException e) {
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

        try {
            userTimeSpanEventService.updateEvent(event);

            return ok(event);
        } catch (IllegalArgumentException e) {
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
            return notFound("要删除的时间不存在");
        }

        if (!event.equals(oldEvent)) {
            return badRequest("要删除的事件信息同数据库不符");
        }

        try {
            userTimeSpanEventService.removeEvent(event);
            return noContent();
        } catch (IllegalArgumentException e) {
            return badRequest(e.getMessage());
        }
    }


}
