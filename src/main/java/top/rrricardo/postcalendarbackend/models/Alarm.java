package top.rrricardo.postcalendarbackend.models;

import top.rrricardo.postcalendarbackend.enums.AlarmType;
import top.rrricardo.postcalendarbackend.utils.generic.CustomList;

import java.time.LocalDateTime;

/**
 * 闹钟类
 */
public class Alarm implements Comparable<Alarm> {
    private final LocalDateTime time;
    private final TimeSpanEvent timeSpanEvent;
    private final CustomList<TimeSpanEvent> timeSpanEvents;
    private final CustomList<TimePointEvent> timePointEvents;

    private final TimePointEvent timePointEvent;
    private final int userId;
    private final AlarmType type;

    public Alarm(TimeSpanEvent event, AlarmType type, int userId) {
        timeSpanEvent = event;
        timePointEvent = null;
        timeSpanEvents = null;
        timePointEvents = null;
        this.type = type;
        this.userId = userId;

        switch (type) {
            case OneHour -> time = event.getBeginDateTime().minusHours(1);
            case OnTime -> time = event.getBeginDateTime();
            default -> throw new IllegalArgumentException("类型错误");
        }
    }

    public Alarm(TimePointEvent event, AlarmType type, int userId) {
        timeSpanEvent = null;
        timeSpanEvents = null;
        timePointEvents = null;
        timePointEvent = event;
        this.type = type;
        this.userId = userId;

        switch (type) {
            case OneHour -> time = event.getEndDateTime().minusHours(1);
            case OnTime -> time = event.getEndDateTime();
            default -> throw new IllegalArgumentException("类型错误");
        }
    }

    public Alarm(AlarmType type, LocalDateTime time, int userId,
                 CustomList<TimeSpanEvent> timeSpanEvents,
                 CustomList<TimePointEvent> timePointEvents) {
        if (type != AlarmType.Tomorrow) {
            throw new IllegalArgumentException("类型错误");
        }
        this.type = type;

        this.time = time;
        timeSpanEvent = null;
        timePointEvent = null;
        this.timeSpanEvents = timeSpanEvents;
        this.timePointEvents = timePointEvents;
        this.userId = userId;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public TimeSpanEvent getTimeSpanEvent() {
        return timeSpanEvent;
    }

    public TimePointEvent getTimePointEvent() {
        return timePointEvent;
    }

    public AlarmType getType() {
        return type;
    }

    public String getTypeString() {
        return switch (type) {
            case OneHour -> "提前一小时闹钟";
            case OnTime -> "当前事件闹钟";
            case Tomorrow -> "明天事件闹钟";
        };
    }

    @Override
    public int compareTo(Alarm alarm) {
        return this.time.compareTo(alarm.time);
    }

    @Override
    public String toString() {
        var message = getTypeString() + "：";

        if (timeSpanEvent != null && timePointEvent == null) {
            return message + timeSpanEvent;
        } else if (timeSpanEvent == null && timePointEvent != null) {
            return message + timePointEvent;
        } else {
            return message + "提醒第二天的事务";
        }
    }

    public CustomList<TimeSpanEvent> getTimeSpanEvents() {
        return timeSpanEvents;
    }

    public CustomList<TimePointEvent> getTimePointEvents() {
        return timePointEvents;
    }

    public int getUserId() {
        return userId;
    }
}
