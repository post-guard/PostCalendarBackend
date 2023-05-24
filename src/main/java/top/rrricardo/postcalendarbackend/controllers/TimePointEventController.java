package top.rrricardo.postcalendarbackend.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import top.rrricardo.postcalendarbackend.annotations.Authorize;
import top.rrricardo.postcalendarbackend.dtos.ResponseDTO;
import top.rrricardo.postcalendarbackend.enums.AuthorizePolicy;
import top.rrricardo.postcalendarbackend.exceptions.TimePointEventException;
import top.rrricardo.postcalendarbackend.models.TimePointEvent;
import top.rrricardo.postcalendarbackend.services.TimePointEventService;
import top.rrricardo.postcalendarbackend.utils.ControllerBase;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@RestController
@RequestMapping("timePointEvent")
public class TimePointEventController extends ControllerBase {
    private final TimePointEventService timePointEventService;

    public TimePointEventController(
            TimePointEventService timePointEventService
    ) {
        this.timePointEventService = timePointEventService;
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


        var instant = Instant.ofEpochSecond(begin);
        var beginTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());

        instant = Instant.ofEpochSecond(end);
        var endTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());

        try {
            var result = timePointEventService.queryUserEvents(id, beginTime, endTime);

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

        try {
            timePointEventService.addUserEvent(event);

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
        if (id != event.getUserId()) {
            return badRequest("请求的用户ID和事件用户ID不一致");
        }

        try {
            timePointEventService.updateUserEvent(event);

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
        if (id != event.getUserId()) {
            return badRequest("请求的用户ID和事件用户ID不一致");
        }

        try {
            timePointEventService.removeUserEvent(event);
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

        var instant = Instant.ofEpochSecond(begin);
        var beginTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        instant = Instant.ofEpochSecond(end);
        var endTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());

        try {
            var result = timePointEventService.queryGroupEvents(id, beginTime, endTime);

            return ok(result.toList());
        } catch (TimePointEventException e) {
            return badRequest(e.getMessage());
        }
    }

    @PostMapping("/group/{id}")
    @Authorize(policy = AuthorizePolicy.CURRENT_GROUP_ADMINISTRATOR)
    public ResponseEntity<ResponseDTO<TimePointEvent>> addGroupEvent(
            @PathVariable int id,
            @RequestBody TimePointEvent event
    ) {
        if (id != event.getGroupId()) {
            return badRequest("请求的组织ID和事件组织ID不一致");
        }

        try {
            timePointEventService.addGroupEvent(event);

            return created(event);
        } catch (TimePointEventException e) {
            return badRequest(e.getMessage());
        }
    }

    @PutMapping("/group/{id}")
    @Authorize(policy = AuthorizePolicy.CURRENT_GROUP_ADMINISTRATOR)
    public ResponseEntity<ResponseDTO<TimePointEvent>> updateGroupEvent(
            @PathVariable int id,
            @RequestBody TimePointEvent event
    ) {
        if (id != event.getGroupId()) {
            return badRequest("请求的组织ID和事件组织ID不一致");
        }

        try {
            timePointEventService.updateGroupEvent(event);

            return ok(event);
        } catch (TimePointEventException e) {
            return badRequest(e.getMessage());
        }
    }

    @DeleteMapping("/group/{id}")
    @Authorize(policy = AuthorizePolicy.CURRENT_GROUP_ADMINISTRATOR)
    public ResponseEntity<ResponseDTO<TimePointEvent>> removeGroupEvent(
            @PathVariable int id,
            @RequestBody TimePointEvent event
    ) {
        if (id != event.getGroupId()) {
            return badRequest("请求的组织ID和事件组织ID不一致");
        }

        try {
            timePointEventService.removeGroupEvent(event);
            return noContent();
        } catch (TimePointEventException e) {
            return badRequest(e.getMessage());
        }
    }
}
