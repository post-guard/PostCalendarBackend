package top.rrricardo.postcalendarbackend.dtos;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import top.rrricardo.postcalendarbackend.models.TimePointEvent;
import top.rrricardo.postcalendarbackend.models.TimeSpanEvent;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 闹钟的消息类
 */
public class AlarmDTO {
    private String message;

    private int alarmType;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime alarmTime;
    private List<TimeSpanEvent> timeSpanEvents;
    private List<TimePointEvent> timePointEvents;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<TimeSpanEvent> getTimeSpanEvents() {
        return timeSpanEvents;
    }

    public void setTimeSpanEvents(List<TimeSpanEvent> timeSpanEvents) {
        this.timeSpanEvents = timeSpanEvents;
    }

    public List<TimePointEvent> getTimePointEvents() {
        return timePointEvents;
    }

    public void setTimePointEvents(List<TimePointEvent> timePointEvents) {
        this.timePointEvents = timePointEvents;
    }

    public LocalDateTime getAlarmTime() {
        return alarmTime;
    }

    public void setAlarmTime(LocalDateTime alarmTime) {
        this.alarmTime = alarmTime;
    }

    public int getAlarmType() {
        return alarmType;
    }

    public void setAlarmType(int alarmType) {
        this.alarmType = alarmType;
    }
}
