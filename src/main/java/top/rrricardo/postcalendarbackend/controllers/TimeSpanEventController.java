package top.rrricardo.postcalendarbackend.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import top.rrricardo.postcalendarbackend.annotations.Authorize;
import top.rrricardo.postcalendarbackend.dtos.ResponseDTO;
import top.rrricardo.postcalendarbackend.enums.AuthorizePolicy;
import top.rrricardo.postcalendarbackend.exceptions.TimeSpanEventException;
import top.rrricardo.postcalendarbackend.models.TimeSpanEvent;
import top.rrricardo.postcalendarbackend.services.TimeSpanEventService;
import top.rrricardo.postcalendarbackend.utils.ControllerBase;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@RestController
@RequestMapping("/timeSpanEvent")
public class TimeSpanEventController extends ControllerBase {
    private final TimeSpanEventService timeSpanEventService;

    public TimeSpanEventController(TimeSpanEventService timeSpanEventService) {
        this.timeSpanEventService = timeSpanEventService;
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

        var instant = Instant.ofEpochSecond(begin);
        var beginTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());

        instant = Instant.ofEpochSecond(end);
        var endTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());

        try {
            var result = timeSpanEventService.queryUserEvent(id, beginTime, endTime);

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

        try {
            timeSpanEventService.addUserEvent(event);

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
        if (id != event.getUserId()) {
            return badRequest("请求的用户ID和事件用户ID不一致");
        }

        try {
            timeSpanEventService.updateUserEvent(event);

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
        if (id != event.getUserId()) {
            return badRequest("请求的用户ID和事件用户ID不一致");
        }

        try {
            timeSpanEventService.removeUserEvent(event);
            return noContent();
        } catch (TimeSpanEventException e) {
            return badRequest(e.getMessage());
        }
    }

    @GetMapping("/group/{id}")
    @Authorize(policy = AuthorizePolicy.CURRENT_GROUP_USER)
    public ResponseEntity<ResponseDTO<List<TimeSpanEvent>>> getGroupEvents(
            @PathVariable int id,
            @RequestParam long begin,
            @RequestParam long end
    ) {
        if (begin >= end) {
            return badRequest("开始时间晚于结束时间");
        }

        var instant = Instant.ofEpochSecond(begin);
        var beginTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());

        instant = Instant.ofEpochSecond(end);
        var endTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());

        try {
            var result = timeSpanEventService.queryGroupEvent(id, beginTime, endTime);

            return ok(result.toList());
        } catch (TimeSpanEventException e) {
            return badRequest(e.getMessage());
        }
    }

    @PostMapping("/group/{id}")
    @Authorize(policy = AuthorizePolicy.CURRENT_GROUP_ADMINISTRATOR)
    public ResponseEntity<ResponseDTO<TimeSpanEvent>> addGroupEvent(
            @PathVariable int id,
            @RequestBody TimeSpanEvent event
    ) {
        if (id != event.getGroupId()) {
            return badRequest("请求的组织ID和事件组织ID不一致");
        }

        try {
            timeSpanEventService.addGroupEvent(event);

            return created(event);
        } catch (TimeSpanEventException e) {
            return badRequest(e.getMessage());
        }
    }

    @PutMapping("/group/{id}")
    @Authorize(policy = AuthorizePolicy.CURRENT_GROUP_ADMINISTRATOR)
    public ResponseEntity<ResponseDTO<TimeSpanEvent>> updateGroupEvent(
            @PathVariable int id,
            @RequestBody TimeSpanEvent event
    ) {
        if (id != event.getGroupId()) {
            return badRequest("请求的组织ID和事件组织ID不一致");
        }

        try {
            timeSpanEventService.updateGroupEvent(event);

            return ok(event);
        } catch (TimeSpanEventException e) {
            return badRequest(e.getMessage());
        }
    }

    @DeleteMapping("/group/{id}")
    @Authorize(policy = AuthorizePolicy.CURRENT_GROUP_ADMINISTRATOR)
    public ResponseEntity<ResponseDTO<TimeSpanEvent>> removeGroupEvent(
            @PathVariable int id,
            @RequestBody TimeSpanEvent event
    ) {
        if (id != event.getGroupId()) {
            return badRequest("请求的组织ID和事件组织ID不一致");
        }

        try {
            timeSpanEventService.removeGroupEvent(event);
            return noContent();
        } catch (TimeSpanEventException e) {
            return badRequest(e.getMessage());
        }
    }


}
