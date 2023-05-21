package top.rrricardo.postcalendarbackend.services;

import top.rrricardo.postcalendarbackend.exceptions.AvlNodeRepeatException;
import top.rrricardo.postcalendarbackend.exceptions.TimePointEventException;
import top.rrricardo.postcalendarbackend.exceptions.TimeSpanEventException;
import top.rrricardo.postcalendarbackend.models.TimePointEvent;
import top.rrricardo.postcalendarbackend.utils.generic.AvlTree;
import top.rrricardo.postcalendarbackend.utils.generic.CustomHashTable;
import top.rrricardo.postcalendarbackend.utils.generic.CustomList;

import java.time.LocalDateTime;

/**
 * 时间点事件服务
 */
public abstract class TimePointEventService {
    public abstract void addEvent(TimePointEvent event) throws TimePointEventException;

    public abstract void removeEvent(TimePointEvent event) throws TimePointEventException;

    public  abstract void updateEvent(TimePointEvent event) throws TimePointEventException;

    public abstract CustomList<TimePointEvent> queryEvent(int id, LocalDateTime begin, LocalDateTime end)
            throws TimePointEventException;

    /**
     * 添加事件辅助函数
     * @param event 需要添加的时间
     * @param forest 需要添加的森林
     * @param id 欲添加的属主ID
     */
    protected void addEventHelper(TimePointEvent event, CustomHashTable<Integer, AvlTree<TimePointEvent>> forest, int id)
            throws TimePointEventException {
        try {
            var tree = forest.get(id);

            if (tree == null) {
                // 存在在应用启动之后
                // 创建新用户的情况
                tree = new AvlTree<>();
                tree.insert(event);
                forest.put(id, tree);
            } else {
                tree.insert(event);
            }
        } catch (AvlNodeRepeatException e) {
            throw new TimePointEventException(e.getMessage());
        }
    }

    /**
     * 删除事件辅助函数
     * @param event 需要删除的事件
     * @param forest 需要删除的森林
     * @param id 属主ID
     */
    protected void removeEventHelper(TimePointEvent event, CustomHashTable<Integer, AvlTree<TimePointEvent>> forest,
                                     int id) throws TimePointEventException {
        var tree = forest.get(id);

        if (tree == null) {
            throw new TimePointEventException("欲删除的事件非法：ID：" + id + "不存在");
        }

        tree.remove(event);
    }

    /**
     * 修改事件辅助函数
     * @param event 需要修改的事件
     * @param forest 需要修改的森林
     * @param id 属主ID
     */
    protected void updateEventHelper(TimePointEvent event, CustomHashTable<Integer, AvlTree<TimePointEvent>> forest,
                                     int id) throws TimePointEventException {
        var tree = forest.get(id);

        if (tree == null) {
            throw new TimePointEventException("欲删除的事件非法：ID：" + id + "不存在");
        }

        TimePointEvent oldEvent = null;

        for (var item : tree) {
            if (item.getId() == event.getId()) {
                oldEvent = item;
                break;
            }
        }

        if (oldEvent == null) {
            throw new TimePointEventException("欲修改的事件不存在");
        }

        if (oldEvent.getEndDateTime().equals(event.getEndDateTime())) {
            // 不修改事件的开始和结束时间
            // 直接修改原来的二叉树
            oldEvent.setName(event.getName());
            oldEvent.setDetails(event.getDetails());
            oldEvent.setPlaceId(event.getPlaceId());
            oldEvent.setType(event.getType());
        } else {
            // 修改事件的开始和结束时间
            // 那就删除事件重新添加
            tree.remove(oldEvent);
            try {
                tree.insert(event);
            } catch (AvlNodeRepeatException exception) {
                throw new TimePointEventException("发生冲突");
            }
        }
    }

    /**
     * 查询事件辅助函数
     * @param forest 查询的森林
     * @param id 属主ID
     * @param begin 开始时间
     * @param end 结束时间
     * @return 在时间段内的事件列表
     * @throws TimePointEventException 查询不存在的用户/组织引发的错误
     */
    protected CustomList<TimePointEvent> queryEventHelper(CustomHashTable<Integer, AvlTree<TimePointEvent>> forest,
                                                         int id, LocalDateTime begin, LocalDateTime end)
            throws TimePointEventException {
        var tree = forest.get(id);

        if (tree == null) {
            throw new TimePointEventException("属主：" + id + "不存在");
        }

        var beginTimeObj = new TimePointEvent();
        beginTimeObj.setEndDateTime(begin);

        var endTimeObj = new TimePointEvent();
        endTimeObj.setEndDateTime(end);

        return tree.selectRange(beginTimeObj, endTimeObj);
    }
}
