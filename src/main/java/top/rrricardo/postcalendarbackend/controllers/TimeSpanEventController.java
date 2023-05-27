package top.rrricardo.postcalendarbackend.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final Logger logger;
    public TimeSpanEventController(TimeSpanEventService timeSpanEventService) {
        this.timeSpanEventService = timeSpanEventService;
        this.logger = LoggerFactory.getLogger(TimeSpanEventController.class);
    }

    @GetMapping("/user/{id}")
    @Authorize(policy = AuthorizePolicy.CURRENT_USER)
    public ResponseEntity<ResponseDTO<List<TimeSpanEvent>>> getUserEvents(
            @PathVariable int id,
            @RequestParam long begin,
            @RequestParam long end
    ) {
        if (begin >= end) {
            logger.info("获取用户事件段事件失败，开始时间晚于结束时间");
            return badRequest("开始时间晚于结束时间");
        }

        var instant = Instant.ofEpochSecond(begin);
        var beginTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());

        instant = Instant.ofEpochSecond(end);
        var endTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());

        try {
            var result = timeSpanEventService.queryUserEvent(id, beginTime, endTime);

            logger.info("成功获取id={}的用户的时间段事件", id);
            return ok(result.toList());
        } catch (TimeSpanEventException e) {
            logger.error("获取id={}的用户的时间段事件失败 ", id, e);
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
            logger.info("给指定用户创建时间段事件失败，请求的用户id和事件用户id不一致");
            return badRequest("请求的用户ID和事件用户ID不一致");
        }

        try {
            timeSpanEventService.addUserEvent(event);
            logger.info("给id={}的用户创建时间段事件成功", id);
            return created(event);
        } catch (TimeSpanEventException e) {
            logger.error("给id={}的用户创建时间段时间失败 ", id, e);
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
            logger.info("修改指定用户的事件段事件失败，请求的用户ID和事件用户ID不一致");
            return badRequest("请求的用户ID和事件用户ID不一致");
        }

        try {
            timeSpanEventService.updateUserEvent(event);

            logger.info("成功修改id={}的用户的时间段事件", id);
            return ok(event);
        } catch (TimeSpanEventException e) {
            logger.error("修改时间段事件失败 ", e);
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
            logger.info("删除指定用户的时间段事件失败，请求的用户ID和事件用户ID不一致");
            return badRequest("请求的用户ID和事件用户ID不一致");
        }

        try {
            timeSpanEventService.removeUserEvent(event);
            logger.info("删除指定用户的时间段事件成功");
            return noContent();
        } catch (TimeSpanEventException e) {
            logger.error("删除指定用户的时间段事件失败 ", e);
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
            logger.info("获取组织时间段事件失败，开始时间晚于结束时间");
            return badRequest("开始时间晚于结束时间");
        }

        var instant = Instant.ofEpochSecond(begin);
        var beginTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());

        instant = Instant.ofEpochSecond(end);
        var endTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());

        try {
            var result = timeSpanEventService.queryGroupEvent(id, beginTime, endTime);

            logger.info("成功获取id={}的组织的时间段事件", id);
            return ok(result.toList());
        } catch (TimeSpanEventException e) {
            logger.error("获取组织时间段事件失败 ", e);
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
            logger.info("给指定组织创建时间段事件失败，请求的组织ID和事件组织ID不一致");
            return badRequest("请求的组织ID和事件组织ID不一致");
        }

        try {
            timeSpanEventService.addGroupEvent(event);
            logger.info("给id={}的组织创建时间段事件成功", id);
            return created(event);
        } catch (TimeSpanEventException e) {
            logger.error("给id={}的组织创建时间段事件失败 ", id, e);
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
            logger.info("修改指定组织的时间段事件失败，请求的组织ID和事件组织ID不一致");
            return badRequest("请求的组织ID和事件组织ID不一致");
        }

        try {
            timeSpanEventService.updateGroupEvent(event);
            logger.info("修改指定组织的时间段事件成功");
            return ok(event);
        } catch (TimeSpanEventException e) {
            logger.error("修改指定组织的时间段事件失败 ", e);
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
            logger.info("删除指定组织的时间段事件失败，请求的组织ID和事件组织ID不一致");
            return badRequest("请求的组织ID和事件组织ID不一致");
        }

        try {
            timeSpanEventService.removeGroupEvent(event);
            logger.info("删除指定组织的时间段事件成功");
            return noContent();
        } catch (TimeSpanEventException e) {
            logger.error("删除指定组织的时间段事件失败 ", e);
            return badRequest(e.getMessage());
        }
    }


}
