package top.rrricardo.postcalendarbackend.services;

import top.rrricardo.postcalendarbackend.exceptions.TimeConflictException;
import top.rrricardo.postcalendarbackend.exceptions.TimePointEventException;
import top.rrricardo.postcalendarbackend.models.TimePointEvent;
import top.rrricardo.postcalendarbackend.utils.generic.CustomList;

import java.time.LocalDateTime;

public interface TimePointEventService {
    /**
     * 向指定的用户添加事件
     *
     * @param event 需要添加的事件
     */
    void addUserEvent(TimePointEvent event) throws TimePointEventException, TimeConflictException;

    /**
     * 向指定的组织添加事件
     *
     * @param event 需要添加的事件
     */
    void addGroupEvent(TimePointEvent event) throws TimePointEventException, TimeConflictException;

    /**
     * 从指定的用户删除事件
     *
     * @param event 需要删除的事件
     */
    void removeUserEvent(TimePointEvent event) throws TimePointEventException;

    /**
     * 从指定的组织删除事件
     *
     * @param event 需要删除的事件
     */
    void removeGroupEvent(TimePointEvent event) throws TimePointEventException;

    /**
     * 修改指定用户的事件
     *
     * @param event 修改的事件
     */
    void updateUserEvent(TimePointEvent event) throws TimePointEventException, TimeConflictException;

    /**
     * 修改指定组织的事件
     *
     * @param event 修改的事件
     */
    void updateGroupEvent(TimePointEvent event) throws TimePointEventException, TimeConflictException;

    /**
     * 查询指定用户在指定时间段内的事件
     *
     * @param userId    用户ID
     * @param beginTime 开始时间
     * @param endTime   结束时间
     * @return 查询到的事件列表
     */
    CustomList<TimePointEvent> queryUserEvents(int userId, LocalDateTime beginTime, LocalDateTime endTime)
            throws TimePointEventException;

    /**
     * 查询指定组织在指定时间段内的事件
     *
     * @param groupId   组织ID
     * @param beginTime 开始时间
     * @param endTime   结束时间
     * @return 查询到的事件列表
     */
    CustomList<TimePointEvent> queryGroupEvents(int groupId, LocalDateTime beginTime, LocalDateTime endTime)
            throws TimePointEventException;
}
