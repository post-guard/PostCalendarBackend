package top.rrricardo.postcalendarbackend.services;

import top.rrricardo.postcalendarbackend.exceptions.TimeSpanEventException;
import top.rrricardo.postcalendarbackend.models.TimeSpanEvent;
import top.rrricardo.postcalendarbackend.utils.generic.CustomList;

import java.time.LocalDateTime;

public interface TimeSpanEventService {
    /**
     * 添加一个用户的时间段事件
     *
     * @param event 需要添加的事件
     * @throws TimeSpanEventException 事件冲突等情况引发的异常
     */
    void addUserEvent(TimeSpanEvent event) throws TimeSpanEventException;

    /**
     * 添加一个组织的时间段事件
     * @param event 需要添加的事件
     * @throws TimeSpanEventException 事件冲突等情况引发的异常
     */
    void addGroupEvent(TimeSpanEvent event) throws TimeSpanEventException;

    /**
     * 移除一个用户的时间段事件
     *
     * @param event 需要移除的事件
     * @throws TimeSpanEventException 事件冲突等情况引发的异常
     */
    void removeUserEvent(TimeSpanEvent event) throws TimeSpanEventException;

    /**
     * 移除一个组织的时间段事件
     * @param event 需要移除的事件
     * @throws TimeSpanEventException 事件冲突等情况引发的异常
     */
    void removeGroupEvent(TimeSpanEvent event) throws TimeSpanEventException;

    /**
     * 更新一个用户的时间段事件
     *
     * @param event 需要更新的事件
     * @throws TimeSpanEventException 事件冲突等情况引发的异常
     */
    void updateUserEvent(TimeSpanEvent event) throws TimeSpanEventException;

    /**
     * 更新一个组织的时间段时间
     * @param event 需要更新的事件
     * @throws TimeSpanEventException 事件冲突等情况引发的异常
     */
    void updateGroupEvent(TimeSpanEvent event) throws TimeSpanEventException;

    /**
     * 判断指定用户的这一时间段是否空闲
     * @param beginTime 开始时间
     * @param endTime 结束时间
     * @param userId 指定用户ID
     * @throws TimeSpanEventException 如果不空闲引发的异常
     */
    void judgeUserTimeConflict(LocalDateTime beginTime, LocalDateTime endTime, int userId) throws TimeSpanEventException;

    /**
     * 判断指定组织的这一时间段是否空闲
     * @param beginTime 开始时间
     * @param endTime 结束时间
     * @param groupId 指定组织ID
     * @throws TimeSpanEventException 如果不空闲引发的异常
     */
    void judgeGroupTimeConflict(LocalDateTime beginTime, LocalDateTime endTime, int groupId) throws TimeSpanEventException;

    /**
     * 获得指定用户在指定时间范围的事件
     *
     * @param id    ID
     * @param begin 开始时间
     * @param end   结束时间
     * @return 事件列表
     */
    CustomList<TimeSpanEvent> queryUserEvent(int id, LocalDateTime begin, LocalDateTime end) throws TimeSpanEventException;

    /**
     * 获得指定组织在指定时间范围内的时间
     * @param id 组织ID
     * @param begin 开始时间
     * @param end 结束时间
     * @return 事件列表
     * @throws TimeSpanEventException 可能引发的各种错误
     */
    CustomList<TimeSpanEvent> queryGroupEvent(int id, LocalDateTime begin, LocalDateTime end) throws TimeSpanEventException;
}
