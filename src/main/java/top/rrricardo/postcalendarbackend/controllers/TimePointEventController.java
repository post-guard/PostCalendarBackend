package top.rrricardo.postcalendarbackend.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final Logger logger;
    public TimePointEventController(
            TimePointEventService timePointEventService
    ) {
        this.timePointEventService = timePointEventService;
        this.logger = LoggerFactory.getLogger(TimePointEventController.class);
    }

    @GetMapping("/user/{id}")
    @Authorize(policy = AuthorizePolicy.CURRENT_USER)
    public ResponseEntity<ResponseDTO<List<TimePointEvent>>> getUserEvents(
            @PathVariable int id,
            @RequestParam long begin,
            @RequestParam long end
    ) {
        if (begin >= end) {
            logger.info("获取用户时间点事件失败，开始事件晚于结束时间");
            return badRequest("开始时间晚于结束时间");
        }


        var instant = Instant.ofEpochSecond(begin);
        var beginTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());

        instant = Instant.ofEpochSecond(end);
        var endTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());

        try {
            var result = timePointEventService.queryUserEvents(id, beginTime, endTime);

            logger.info("成功获取id={}的用户的时间点事件", id);
            return ok(result.toList());
        } catch (TimePointEventException e) {
            logger.error("获取id={}的用户的时间点事件失败 ", id, e);
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
            logger.info("给指定用户创建时间点事件失败，请求的用户id和事件用户id不一致");
            return badRequest("请求的用户ID和事件用户ID不一致");
        }

        try {
            timePointEventService.addUserEvent(event);

            logger.info("给id={}的用户创建时间点事件成功", id);
            return created(event);
        } catch (TimePointEventException e) {
            logger.error("给id={}的用户创建时间点时间失败 ", id, e);
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
            logger.info("修改时间点事件失败，请求的用户ID和事件用户ID不一致");
            return badRequest("请求的用户ID和事件用户ID不一致");
        }

        try {
            timePointEventService.updateUserEvent(event);

            logger.info("修改时间点事件成功");
            return ok(event);
        } catch (TimePointEventException e) {
            logger.error("修改时间点事件失败 ", e);
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
            logger.info("删除用户时间点事件失败，请求的用户ID和事件用户ID不一致");
            return badRequest("请求的用户ID和事件用户ID不一致");
        }

        try {
            timePointEventService.removeUserEvent(event);
            logger.info("删除用户时间点事件成功");
            return noContent();
        } catch (TimePointEventException e) {
            logger.error("删除用户时间点事件失败 ", e);
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
            logger.info("获取组织时间点事件失败，开始时间晚于结束时间");
            return badRequest("开始时间晚于结束时间");
        }

        var instant = Instant.ofEpochSecond(begin);
        var beginTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        instant = Instant.ofEpochSecond(end);
        var endTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());

        try {
            var result = timePointEventService.queryGroupEvents(id, beginTime, endTime);

            logger.info("成功获取id={}的组织的时间点事件", id);
            return ok(result.toList());
        } catch (TimePointEventException e) {
            logger.error("获取组织时间点事件失败 ", e);
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
            logger.info("给指定组织创建时间点事件失败，请求的组织ID和事件组织ID不一致");
            return badRequest("请求的组织ID和事件组织ID不一致");
        }

        try {
            timePointEventService.addGroupEvent(event);

            logger.info("给id={}的组织创建时间点事件成功", id);
            return created(event);
        } catch (TimePointEventException e) {
            logger.error("给id={}的组织创建时间点事件失败 ", id, e);
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
            logger.info("修改指定组织的时间点事件失败，请求的组织ID和事件组织ID不一致");
            return badRequest("请求的组织ID和事件组织ID不一致");
        }

        try {
            timePointEventService.updateGroupEvent(event);

            logger.info("修改指定组织的时间点事件成功");
            return ok(event);
        } catch (TimePointEventException e) {
            logger.error("修改指定组织的时间点事件失败 ", e);
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
            logger.info("删除指定组织的时间点事件失败，请求的组织ID和事件组织ID不一致");
            return badRequest("请求的组织ID和事件组织ID不一致");
        }

        try {
            timePointEventService.removeGroupEvent(event);
            logger.info("删除指定组织的时间点事件成功");
            return noContent();
        } catch (TimePointEventException e) {
            logger.error("删除指定组织的时间点事件失败 ", e);
            return badRequest(e.getMessage());
        }
    }
}
