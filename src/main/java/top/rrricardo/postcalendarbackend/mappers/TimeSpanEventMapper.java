package top.rrricardo.postcalendarbackend.mappers;

import org.apache.ibatis.annotations.Mapper;
import top.rrricardo.postcalendarbackend.models.TimeSpanEvent;

import java.util.List;

@Mapper
public interface TimeSpanEventMapper {
    List<TimeSpanEvent> getEvents();

    TimeSpanEvent getEventById(int id);

    void createEvent(TimeSpanEvent event);

    void updateEvent(TimeSpanEvent event);

    void deleteEvent(int id);
}
