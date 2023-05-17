package top.rrricardo.postcalendarbackend.services;

import top.rrricardo.postcalendarbackend.models.TimeSpanEvent;
import top.rrricardo.postcalendarbackend.utils.generic.CustomList;

import java.time.LocalDateTime;

public abstract class TimeSpanEventService {
    /**
     * 添加一个时间段
     * @param event 需要添加的事件
     */
    public abstract void addEvent(TimeSpanEvent event);

    /**
     * 移除一个事件
     * @param event 需要移除的事件
     */
    public abstract void removeEvent(TimeSpanEvent event);

    /**
     * 更新事件
     * @param event 需要更新的事件
     */
    public abstract void updateEvent(TimeSpanEvent event);

    /**
     * 获得指定用户在指定时间范围的事件
     * @param id ID
     * @param begin 开始时间
     * @param end 结束时间
     * @return 事件列表
     */
    public abstract CustomList<TimeSpanEvent> queryEvent(int id, LocalDateTime begin, LocalDateTime end);
}
