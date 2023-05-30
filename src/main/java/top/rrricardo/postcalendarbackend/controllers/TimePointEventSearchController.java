package top.rrricardo.postcalendarbackend.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import top.rrricardo.postcalendarbackend.annotations.Authorize;
import top.rrricardo.postcalendarbackend.dtos.ResponseDTO;
import top.rrricardo.postcalendarbackend.dtos.SearchPrefixDTO;
import top.rrricardo.postcalendarbackend.enums.AuthorizePolicy;
import top.rrricardo.postcalendarbackend.exceptions.TimePointEventSearchException;
import top.rrricardo.postcalendarbackend.models.TimePointEvent;
import top.rrricardo.postcalendarbackend.services.TimePointEventSearchService;
import top.rrricardo.postcalendarbackend.utils.ControllerBase;

import java.util.List;

@RestController
@RequestMapping("/searchTimePointEvent")
public class TimePointEventSearchController extends ControllerBase {
    private final TimePointEventSearchService timePointEventSearchService;
    private final Logger logger;

    public TimePointEventSearchController(TimePointEventSearchService timePointEventSearchService) {
        this.timePointEventSearchService = timePointEventSearchService;

        logger = LoggerFactory.getLogger(getClass());
    }

    @PostMapping("/{id}")
    @Authorize(policy = AuthorizePolicy.CURRENT_USER)
    public ResponseEntity<ResponseDTO<List<TimePointEvent>>> searchByUserIdAndPrefix(
            @PathVariable int id,
            @RequestBody SearchPrefixDTO prefix
            ) {
        try {
            logger.info("查询用户：{}的时间时间点事件，前缀为：{}", id, prefix.getPrefix());

            var events = timePointEventSearchService.searchByUserIdAndPrefix(id, prefix.getPrefix());

            return ok(events.toList());
        } catch (TimePointEventSearchException e) {
            logger.info("查询时间点事件错误", e);

            return badRequest("查询失败");
        }
    }
}
