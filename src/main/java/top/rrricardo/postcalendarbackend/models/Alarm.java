package top.rrricardo.postcalendarbackend.models;

import top.rrricardo.postcalendarbackend.enums.AlarmType;

import java.time.LocalDateTime;

/**
 * 闹钟类
 */
public class Alarm implements Comparable<Alarm> {
    private final LocalDateTime time;
    private final TimeSpanEvent timeSpanEvent;
    private final TimePointEvent timePointEvent;
    private final int userId;
    private final AlarmType type;

    public Alarm(TimeSpanEvent event, AlarmType type, int userId) {
        timeSpanEvent = event;
        timePointEvent = null;
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
        timePointEvent = event;
        this.type = type;
        this.userId = userId;

        switch (type) {
            case OneHour -> time = event.getEndDateTime().minusHours(1);
            case OnTime -> time = event.getEndDateTime();
            default -> throw new IllegalArgumentException("类型错误");
        }
    }

    public Alarm(AlarmType type, LocalDateTime time, int userId) {
        if (type != AlarmType.Tomorrow) {
            throw new IllegalArgumentException("类型错误");
        }
        this.type = type;

        this.time = time;
        timeSpanEvent = null;
        timePointEvent = null;
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

    @Override
    public int compareTo(Alarm alarm) {
        return this.time.compareTo(alarm.time);
    }

    @Override
    public String toString() {
        var message = switch (type) {
            case OneHour -> "提前一小时闹钟:";
            case OnTime -> "当前事件闹钟:";
            case Tomorrow -> "明天事件闹钟:";
        };

        if (timeSpanEvent != null && timePointEvent == null) {
            return message + timeSpanEvent;
        } else if (timeSpanEvent == null && timePointEvent != null) {
            return message + timePointEvent;
        } else {
            return message + "提醒第二天的事务";
        }
    }
}
