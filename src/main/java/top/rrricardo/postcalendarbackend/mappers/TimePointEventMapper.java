package top.rrricardo.postcalendarbackend.mappers;

import org.apache.ibatis.annotations.Mapper;
import top.rrricardo.postcalendarbackend.models.TimePointEvent;

import java.util.List;

@Mapper
public interface TimePointEventMapper {
    List<TimePointEvent> getEvents();

    TimePointEvent getEventById(int id);

    void createEvent(TimePointEvent event);

    void updateEvent(TimePointEvent event);

    void deleteEvent(int id);
}
