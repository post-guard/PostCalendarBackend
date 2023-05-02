package top.rrricardo.postcalendarbackend.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import top.rrricardo.postcalendarbackend.dtos.ResponseDTO;
import top.rrricardo.postcalendarbackend.mappers.TimeSpanEventMapper;
import top.rrricardo.postcalendarbackend.models.TimeSpanEvent;
import top.rrricardo.postcalendarbackend.utils.ControllerBase;

import java.util.List;

@RestController
@RequestMapping("/timeSpanEvent")
public class TimeSpanEventController extends ControllerBase {
    private final TimeSpanEventMapper timeSpanEventMapper;

    public TimeSpanEventController(TimeSpanEventMapper mapper) {
        this.timeSpanEventMapper = mapper;
    }

    @GetMapping("/")
    public ResponseEntity<ResponseDTO<List<TimeSpanEvent>>> getEvents() {
        var events = timeSpanEventMapper.getEvents();

        return ok(events);
    }

    @PostMapping("/")
    public ResponseEntity<ResponseDTO<TimeSpanEvent>> createEvent(@RequestBody TimeSpanEvent event) {
        timeSpanEventMapper.createEvent(event);

        return created();
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseDTO<TimeSpanEvent>> updateEvent(
            @PathVariable(value = "id") int id, @RequestBody TimeSpanEvent event) {
        if (id != event.getId()) {
            return badRequest();
        }

        var oldEvent = timeSpanEventMapper.getEventById(id);

        if (oldEvent == null) {
            return notFound("欲修改的事件不存在");
        }

        timeSpanEventMapper.updateEvent(event);

        var newEvent = timeSpanEventMapper.getEventById(id);

        if (newEvent == null) {
            throw new NullPointerException();
        }

        return ok(newEvent);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDTO<TimeSpanEvent>> deleteEvent(@PathVariable(value = "id") int id) {
        var event = timeSpanEventMapper.getEventById(id);

        if (event == null) {
            return notFound("事件不存在");
        }

        timeSpanEventMapper.deleteEvent(id);

        return noContent();
    }
}
