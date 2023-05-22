package top.rrricardo.postcalendarbackend.services;

import top.rrricardo.postcalendarbackend.exceptions.AvlNodeRepeatException;
import top.rrricardo.postcalendarbackend.exceptions.TimeSpanEventException;
import top.rrricardo.postcalendarbackend.models.TimeSpanEvent;
import top.rrricardo.postcalendarbackend.utils.generic.AvlTree;
import top.rrricardo.postcalendarbackend.utils.generic.CustomHashTable;
import top.rrricardo.postcalendarbackend.utils.generic.CustomList;

import java.time.LocalDateTime;

public abstract class TimeSpanEventService {
    /**
     * 添加一个时间段
     *
     * @param event 需要添加的事件
     */
    public abstract void addEvent(TimeSpanEvent event) throws TimeSpanEventException;

    /**
     * 移除一个事件
     *
     * @param event 需要移除的事件
     */
    public abstract void removeEvent(TimeSpanEvent event) throws TimeSpanEventException;

    /**
     * 更新事件
     *
     * @param event 需要更新的事件
     */
    public abstract void updateEvent(TimeSpanEvent event) throws TimeSpanEventException;

    /**
     * 获得指定用户在指定时间范围的事件
     *
     * @param id    ID
     * @param begin 开始时间
     * @param end   结束时间
     * @return 事件列表
     */
    public abstract CustomList<TimeSpanEvent> queryEvent(int id, LocalDateTime begin, LocalDateTime end) throws TimeSpanEventException;

    public abstract boolean judgeConflict(int id, TimeSpanEvent event);

    /**
     * 添加事件辅助函数
     *
     * @param event  需要添加的时间
     * @param forest 需要添加的森林
     * @param id     欲添加的属主ID
     */
    protected void addEventHelper(TimeSpanEvent event, CustomHashTable<Integer, AvlTree<TimeSpanEvent>> forest, int id)
            throws TimeSpanEventException {
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
            throw new TimeSpanEventException("发生冲突");
        }
    }

    /**
     * 删除事件辅助函数
     *
     * @param event  需要删除的事件
     * @param forest 需要删除的森林
     * @param id     属主ID
     */
    protected void removeEventHelper(TimeSpanEvent event, CustomHashTable<Integer, AvlTree<TimeSpanEvent>> forest,
                                     int id) throws TimeSpanEventException {
        var tree = forest.get(id);

        if (tree == null) {
            throw new TimeSpanEventException("欲删除的事件非法：ID：" + id + "不存在");
        }

        tree.remove(event);
    }

    /**
     * 修改事件辅助函数
     *
     * @param event  需要修改的事件
     * @param forest 需要修改的森林
     * @param id     属主ID
     */
    protected void updateEventHelper(TimeSpanEvent event, CustomHashTable<Integer, AvlTree<TimeSpanEvent>> forest,
                                     int id) throws TimeSpanEventException {
        var tree = forest.get(id);

        if (tree == null) {
            throw new TimeSpanEventException("欲删除的事件非法：ID：" + id + "不存在");
        }

        TimeSpanEvent oldEvent = null;

        for (var item : tree) {
            if (item.getId() == event.getId()) {
                oldEvent = item;
                break;
            }
        }

        if (oldEvent == null) {
            throw new TimeSpanEventException("欲修改的事件不存在");
        }

        if (oldEvent.getBeginDateTime().equals(event.getBeginDateTime())
                && oldEvent.getEndDateTime().equals(event.getEndDateTime())) {
            // 不修改事件的开始和结束时间
            // 直接修改原来的二叉树
            oldEvent.setName(event.getName());
            oldEvent.setDetails(event.getDetails());
            oldEvent.setPlaceId(event.getPlaceId());
        } else {
            // 修改事件的开始和结束时间
            // 那就删除事件重新添加
            tree.remove(oldEvent);
            try {
                tree.insert(event);
            } catch (AvlNodeRepeatException exception) {
                throw new TimeSpanEventException("发生冲突");
            }
        }
    }

    /**
     * 查询事件辅助函数
     *
     * @param forest 查询的森林
     * @param id     属主ID
     * @param begin  开始时间
     * @param end    结束时间
     * @return 在时间段内的事件列表
     * @throws TimeSpanEventException 查询不存在的用户/组织引发的错误
     */
    protected CustomList<TimeSpanEvent> queryEventHelper(CustomHashTable<Integer, AvlTree<TimeSpanEvent>> forest,
                                                         int id, LocalDateTime begin, LocalDateTime end)
            throws TimeSpanEventException {
        var tree = forest.get(id);

        if (tree == null) {
            throw new TimeSpanEventException("属主：" + id + "不存在");
        }

        var beginTimeObj = new TimeSpanEvent();
        beginTimeObj.setBeginDateTime(begin);
        beginTimeObj.setEndDateTime(begin);

        var endTimeObj = new TimeSpanEvent();
        endTimeObj.setBeginDateTime(end);
        endTimeObj.setEndDateTime(end);

        return tree.selectRange(beginTimeObj, endTimeObj);
    }

    protected boolean judgeConflictHelper(TimeSpanEvent event, AvlTree<TimeSpanEvent> tree) {
        var iterator = tree.iterator();

        TimeSpanEvent left;
        TimeSpanEvent right = null;

        if (iterator.hasNext()) {
            left = iterator.next();
        } else {
            // 在树中不存在元素
            return true;
        }

        if (iterator.hasNext()) {
            right = iterator.next();
        }

        if (event.compareTo(left) <= 0) {
            // 事件在树中事件的前面
            return event.getEndDateTime().isBefore(left.getBeginDateTime());
        }

        while (iterator.hasNext()) {
            if (left.compareTo(event) <= 0 && event.compareTo(right) <= 0) {
                // 找到位于中间的状态
                return left.getEndDateTime().isBefore(event.getBeginDateTime())
                        && event.getEndDateTime().isBefore(right.getBeginDateTime());
            }

            left = right;
            right = iterator.next();
        }

        // 最后两个元素
        // 当然还有还要考虑树中只有一个元素的情况
        if (right != null) {
            if (left.compareTo(event) <= 0 && event.compareTo(right) <= 0) {
                // 找到位于中间的状态
                return left.getEndDateTime().isBefore(event.getBeginDateTime())
                        && event.getEndDateTime().isBefore(right.getBeginDateTime());
            } else {
                return right.getEndDateTime().isBefore(event.getBeginDateTime());
            }
        } else {
            return left.getEndDateTime().isBefore(event.getBeginDateTime());
        }
    }
}
